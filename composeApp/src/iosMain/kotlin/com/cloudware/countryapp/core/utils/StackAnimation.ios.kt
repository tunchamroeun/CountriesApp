package com.cloudware.countryapp.core.utils

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimatable as decomposeBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimator
import com.arkivanov.essenty.backhandler.BackHandler

@OptIn(ExperimentalDecomposeApi::class)
actual fun <C : Any, T : Any> backAnimation(
    backHandler: BackHandler,
    animationSelector: ((childInstance: T) -> StackAnimation<C, T>?)?,
    onBack: () -> Unit,
): StackAnimation<C, T> =
    predictiveBackAnimation(
        backHandler = backHandler,
        animationSelector = animationSelector,
        fallbackAnimation = stackAnimation(iosLikeSlide()),
        selector = { initialBackEvent, _, _ ->
          val decomposeAnimatable =
              decomposeBackAnimatable(
                  initialBackEvent = initialBackEvent,
                  exitModifier = { progress: Float, _ ->
                    Modifier.slideExitModifier(progress = progress)
                  },
                  enterModifier = { progress: Float, _ ->
                    Modifier.slideEnterModifier(progress = progress)
                  },
              )

          // Adapter to convert from Decompose's PredictiveBackAnimatable to our custom interface
          object : PredictiveBackAnimatable {
            override suspend fun animate(backEvent: com.arkivanov.essenty.backhandler.BackEvent) {
              decomposeAnimatable.animate(backEvent)
            }

            override suspend fun finish() {
              decomposeAnimatable.finish()
            }

            override suspend fun cancel() {
              decomposeAnimatable.cancel()
            }

            @androidx.compose.runtime.Composable
            override fun enterModifier(): Modifier = decomposeAnimatable.enterModifier

            @androidx.compose.runtime.Composable
            override fun exitModifier(): Modifier = decomposeAnimatable.exitModifier
          }
        },
        onBack = onBack,
    )

private fun iosLikeSlide(animationSpec: FiniteAnimationSpec<Float> = tween()): StackAnimator =
    stackAnimator(animationSpec = animationSpec) { factor, direction, content ->
      content(
          Modifier.then(if (direction.isFront) Modifier else Modifier.fade(factor + 1F))
              .offsetXFactor(factor = if (direction.isFront) factor else factor * 0.5F))
    }

private fun Modifier.slideExitModifier(progress: Float): Modifier = offsetXFactor(progress)

private fun Modifier.slideEnterModifier(progress: Float): Modifier =
    fade(progress).offsetXFactor((progress - 1f) * 0.5f)

private fun Modifier.fade(factor: Float) = drawWithContent {
  drawContent()
  drawRect(color = Color(red = 0F, green = 0F, blue = 0F, alpha = (1F - factor) / 4F))
}

private fun Modifier.offsetXFactor(factor: Float): Modifier = layout { measurable, constraints ->
  val placeable = measurable.measure(constraints)

  layout(placeable.width, placeable.height) {
    placeable.placeRelative(x = (placeable.width.toFloat() * factor).toInt(), y = 0)
  }
}
