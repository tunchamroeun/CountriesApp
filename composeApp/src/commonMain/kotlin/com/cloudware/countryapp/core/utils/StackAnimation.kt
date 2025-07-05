package com.cloudware.countryapp.core.utils

import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.essenty.backhandler.BackHandler

expect fun <C : Any, T : Any> backAnimation(
    backHandler: BackHandler,
    animationSelector: ((childInstance: T) -> StackAnimation<C, T>?)? = null,
    onBack: () -> Unit,
): StackAnimation<C, T>
