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
import com.arkivanov.decompose.extensions.compose.stack.animation.LocalStackAnimationProvider
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.materialPredictiveBackAnimatable
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackEvent
import com.arkivanov.essenty.backhandler.BackHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Creates an empty StackAnimation that applies no animations */
@Composable
private fun <C : Any, T : Any> createEmptyStackAnimation(): StackAnimation<C, T> = remember {
  StackAnimation { stack, modifier, content -> Box(modifier = modifier) { content(stack.active) } }
}

/** A simple overlay that consumes all input events */
@Composable
private fun CustomInputConsumingOverlay(modifier: Modifier) {
  Box(
      modifier =
          modifier.pointerInput(Unit) {
            // Consume all pointer input events
            awaitPointerEventScope {
              while (true) {
                awaitPointerEvent()
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
 * @param animationSelector a selector function that selects an animation based on the child
 *   instance.
 * @param selector a selector function that is called when the predictive back gesture begins,
 *   returns
 *   [com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackAnimatable]
 *   responsible for animations.
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

@OptIn(ExperimentalDecomposeApi::class)
private class PredictiveBackAnimation<C : Any, T : Any>(
    private val backHandler: BackHandler,
    private val animation: StackAnimation<C, T>?,
    private val animationSelector: ((childInstance: T) -> StackAnimation<C, T>?)?,
    private val selector:
        (
            initialBackEvent: BackEvent,
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

    // Check for custom animation based on active child instance
    val selectedAnimation = animationSelector?.invoke(stack.active.instance)

    val anim =
        selectedAnimation
            ?: animation
            ?: remember(animationProvider, animationProvider::provide)
            ?: createEmptyStackAnimation()

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
          // Select animation based on the active child in this stack item
          val itemAnimation =
              if (animationSelector != null) {
                animationSelector.invoke(item.stack.active.instance) ?: anim
              } else {
                anim
              }

          itemAnimation(
              stack = item.stack,
              modifier = Modifier.fillMaxSize().then(item.modifier()),
              content = childContent,
          )
        }
      }

      if (handler.items.size > 1) {
        CustomInputConsumingOverlay(modifier = Modifier.fillMaxSize())
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
    var previousHandler by remember { mutableStateOf<Handler<C, T>?>(null) }

    val newHandler =
        remember(stack) {
          Handler(
              stack = stack,
              scope = scope,
              isGestureEnabled = isGestureEnabled,
              key = previousHandler?.items?.maxOfOrNull { it.key } ?: 0,
              selector = selector,
              onBack = onBack,
          )
        }

    DisposableEffect(newHandler) {
      previousHandler = newHandler
      onDispose {}
    }

    return newHandler
  }

  private data class Item<out C : Any, out T : Any>(
      val stack: ChildStack<C, T>,
      val key: Int,
      val modifier: () -> Modifier = { Modifier },
  )

  private class Handler<C : Any, T : Any>(
      private val stack: ChildStack<C, T>,
      private val scope: CoroutineScope,
      private val isGestureEnabled: () -> Boolean,
      private val key: Int,
      private val selector:
          (
              initialBackEvent: BackEvent,
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
                Item(stack = stack.dropLast(), key = key + 1, modifier = animatable::enterModifier),
                Item(stack = stack, key = key, modifier = animatable::exitModifier),
            )
      }

      scope.launch { animatable?.animate(backEvent) }
    }

    private fun <C : Any, T : Any> ChildStack<C, T>.dropLast(): ChildStack<C, T> =
        if (backStack.isNotEmpty()) {
          ChildStack(active = backStack.last(), backStack = backStack.dropLast(1))
        } else {
          this
        }

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
