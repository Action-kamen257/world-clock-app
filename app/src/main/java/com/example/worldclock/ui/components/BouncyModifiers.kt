package com.example.worldclock.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Reusable modifier that scales down an element on press and bounces it back up on release,
 * mimicking premium iOS-style physics.
 */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Scale goes down to 0.94 on press for solid physical feedback
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = 0.48f, // iOS-style bouncy spring
            stiffness = 250f
        ),
        label = "bounceClickScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null, // Disable default gray box/ripple to keep the scale feedback pure and clean
            enabled = enabled,
            onClick = onClick
        )
}
