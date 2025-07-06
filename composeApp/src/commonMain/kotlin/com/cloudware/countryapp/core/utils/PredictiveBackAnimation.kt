package com.cloudware.countryapp.core.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.InternalDecomposeApi
import com.arkivanov.decompose.Ref
import com.arkivanov.decompose.extensions.compose.stack.animation.LocalStackAnimationProvider
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackEvent
import com.arkivanov.essenty.backhandler.BackHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Empty stack animation that doesn't apply any animation */
private fun <C : Any, T : Any> emptyStackAnimation(): StackAnimation<C, T> =
    StackAnimation { stack, modifier, content ->
      Box(modifier = modifier) { content(stack.active) }
    }

/** An overlay that consumes all input events */
@Composable
private fun InputConsumingOverlay(modifier: Modifier = Modifier) {
  Box(
      modifier =
          modifier.pointerInput(Unit) {
            // Consume all pointer events
            awaitPointerEventScope {
              while (true) {
                awaitPointerEvent()
                // Don't pass events through
              }
            }
          })
}

/**
 * Wraps the provided [fallbackAnimation], handles the predictive back gesture and animates the
 * transition from the current [Child] to the previous one. Calls [onBack] when the animation is
 * finished.
 *
 * @param backHandler a source of the predictive back gesture events, see [BackHandler].
 * @param fallbackAnimation a [StackAnimation] for regular transitions.
 * @param animationSelector a selector function that returns a [StackAnimation] based on the child
 *   instance.
 * @param selector a selector function that is called when the predictive back gesture begins,
 *   returns [PredictiveBackAnimatable] responsible for animations.
 * @param onBack a callback that is called when the gesture is finished.
 */
@ExperimentalDecomposeApi
fun <C : Any, T : Any> predictiveBackAnimation(
    backHandler: BackHandler,
    fallbackAnimation: StackAnimation<C, T>? = null,
    animationSelector: ((childInstance: T) -> StackAnimation<C, T>?)? = null,
    selector:
        (
            initialBackEvent: BackEvent,
            exitChild: Child.Created<C, T>,
            enterChild: Child.Created<C, T>,
        ) -> PredictiveBackAnimatable =
        { initialBackEvent, _, _ ->
          materialPredictiveBackAnimatable(initialBackEvent = initialBackEvent)
        },
    onBack: () -> Unit,
): StackAnimation<C, T> =
    PredictiveBackAnimation(
        backHandler = backHandler,
        animation = fallbackAnimation,
        animationSelector = animationSelector,
        selector = selector,
        onBack = onBack,
    )

@OptIn(ExperimentalDecomposeApi::class, InternalDecomposeApi::class)
private class PredictiveBackAnimation<C : Any, T : Any>(
    private val backHandler: BackHandler,
    private val animation: StackAnimation<C, T>?,
    private val animationSelector: ((childInstance: T) -> StackAnimation<C, T>?)?,
    private val selector:
        (
            BackEvent,
            exitChild: Child.Created<C, T>,
            enterChild: Child.Created<C, T>) -> PredictiveBackAnimatable,
    private val onBack: () -> Unit,
) : StackAnimation<C, T> {

  @Composable
  override fun invoke(
      stack: ChildStack<C, T>,
      modifier: Modifier,
      content: @Composable (child: Child.Created<C, T>) -> Unit
  ) {
    val activeKeys = remember { HashSet<Any>() }
    val handler = rememberHandler(stack = stack, isGestureEnabled = { activeKeys.size == 1 })
    val animationProvider = LocalStackAnimationProvider.current

    val childContent =
        remember(content) {
          movableContentOf<Child.Created<C, T>> { child ->
            key(child.key) {
              content(child)
              DisposableEffect(Unit) {
                activeKeys += child.key
                onDispose { activeKeys -= child.key }
              }
            }
          }
        }

    Box(modifier = modifier) {
      handler.items.forEach { item ->
        key(item.key) {
          val anim =
              when {
                animationSelector != null -> {
                  val selectedAnimation = animationSelector.invoke(item.stack.active.instance)
                  selectedAnimation
                      ?: animation
                      ?: remember(animationProvider, animationProvider::provide)
                      ?: emptyStackAnimation()
                }
                else ->
                    animation
                        ?: remember(animationProvider, animationProvider::provide)
                        ?: emptyStackAnimation()
              }

          anim(
              stack = item.stack,
              modifier = Modifier.fillMaxSize().then(item.modifier()),
              content = childContent,
          )
        }
      }

      if (handler.items.size > 1) {
        InputConsumingOverlay(modifier = Modifier.matchParentSize())
      }
    }

    if (stack.backStack.isNotEmpty()) {
      DisposableEffect(handler) {
        backHandler.register(handler)
        onDispose { backHandler.unregister(handler) }
      }
    }
  }

  @Composable
  private fun rememberHandler(
      stack: ChildStack<C, T>,
      isGestureEnabled: () -> Boolean
  ): Handler<C, T> {
    val scope = key(stack) { rememberCoroutineScope() }
    return rememberWithLatest(stack) { previousHandler ->
      Handler(
          stack = stack,
          scope = scope,
          isGestureEnabled = isGestureEnabled,
          key = previousHandler?.items?.maxOf { it.key } ?: 0,
          selector = selector,
          onBack = onBack,
      )
    }
  }

  @Composable
  private fun <T> rememberWithLatest(key: Any, supplier: (T?) -> T): T {
    val ref = remember { Ref<T?>(null) }
    val v = remember(key) { supplier(ref.value) }
    ref.value = v
    return v
  }

  private data class Item<C : Any, T : Any>(
      val stack: ChildStack<C, T>,
      val key: Int,
      val modifier: @Composable () -> Modifier = { Modifier },
  )

  private class Handler<C : Any, T : Any>(
      private val stack: ChildStack<C, T>,
      private val scope: CoroutineScope,
      private val isGestureEnabled: () -> Boolean,
      private val key: Int,
      private val selector:
          (
              BackEvent,
              exitChild: Child.Created<C, T>,
              enterChild: Child.Created<C, T>) -> PredictiveBackAnimatable,
      private val onBack: () -> Unit,
  ) : BackCallback() {

    var items: List<Item<C, T>> by mutableStateOf(listOf(Item(stack = stack, key = key)))
      private set

    private var animatable: PredictiveBackAnimatable? = null
    private var initialBackEvent: BackEvent? = null

    override fun onBackStarted(backEvent: BackEvent) {
      initialBackEvent = backEvent
    }

    override fun onBackProgressed(backEvent: BackEvent) {
      val initialBackEvent = initialBackEvent
      if ((initialBackEvent != null) && isGestureEnabled()) {
        val animatable = selector(initialBackEvent, stack.active, stack.backStack.last())
        this.animatable = animatable
        this.initialBackEvent = null

        items =
            listOf(
                Item(
                    stack = stack.dropLast(),
                    key = key + 1,
                    modifier = { animatable.enterModifier() }),
                Item(stack = stack, key = key, modifier = { animatable.exitModifier() }),
            )
      }

      scope.launch { animatable?.animate(backEvent) }
    }

    private fun <C : Any, T : Any> ChildStack<C, T>.dropLast(): ChildStack<C, T> =
        ChildStack(active = backStack.last(), backStack = backStack.dropLast(1))

    override fun onBack() {
      if (animatable == null) {
        onBack.invoke()
      } else {
        scope.launch {
          animatable?.finish()
          animatable = null
          onBack.invoke()
        }
      }
    }

    override fun onBackCancelled() {
      scope.launch {
        animatable?.cancel()
        animatable = null
        items = listOf(Item(stack = stack, key = key))
      }
    }
  }
}

/** A predictive back animatable that can be animated */
@ExperimentalDecomposeApi
interface PredictiveBackAnimatable {
  suspend fun animate(backEvent: BackEvent)

  suspend fun finish()

  suspend fun cancel()

  @Composable fun enterModifier(): Modifier

  @Composable fun exitModifier(): Modifier
}

/** Material design predictive back animatable */
@ExperimentalDecomposeApi
fun materialPredictiveBackAnimatable(initialBackEvent: BackEvent): PredictiveBackAnimatable {
  // This is a placeholder implementation
  // In a real implementation, this would create material-design-specific animations
  return object : PredictiveBackAnimatable {
    override suspend fun animate(backEvent: BackEvent) {
      // Animate based on back event progress
    }

    override suspend fun finish() {
      // Finish animation
    }

    override suspend fun cancel() {
      // Cancel animation
    }

    @Composable override fun enterModifier(): Modifier = Modifier

    @Composable override fun exitModifier(): Modifier = Modifier
  }
}
