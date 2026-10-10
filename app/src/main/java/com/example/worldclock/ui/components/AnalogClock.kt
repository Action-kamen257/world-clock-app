package com.example.worldclock.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.worldclock.theme.Amber
import com.example.worldclock.theme.DarkBorder
import com.example.worldclock.theme.TextPrimary
import com.example.worldclock.theme.TextSecondary
import java.time.ZonedDateTime
import kotlin.math.cos
import kotlin.math.sin

private data class MarkerInfo(
    val start: Offset,
    val end: Offset,
    val isMain: Boolean,
    val width: Float,
    val color: Color
)

@Composable
fun AnalogClock(
    time: ZonedDateTime,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    accentColor: Color = Amber,
) {
    val hour = time.hour % 12
    val minute = time.minute
    val second = time.second

    // Smooth second hand animation
    val targetAngle = second * 6f
    val animatedSecondAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "secondHand",
    )

    // Compute pixel measurements once using LocalDensity rather than inside the draw block
    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }
    val center = Offset(sizePx / 2f, sizePx / 2f)
    val radius = sizePx / 2f - 8f

    // Cache precomputed coordinates for the 12 hour markers
    val hourMarkers = remember(center, radius) {
        val markers = ArrayList<MarkerInfo>()
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val isMainMarker = i % 3 == 0
            val markerLength = if (isMainMarker) 14f else 8f
            val markerWidth = if (isMainMarker) 3f else 1.5f
            val markerColor = if (isMainMarker) TextPrimary else TextSecondary.copy(alpha = 0.5f)

            val outerX = center.x + (radius - 6f) * cos(angle).toFloat()
            val outerY = center.y + (radius - 6f) * sin(angle).toFloat()
            val innerX = center.x + (radius - 6f - markerLength) * cos(angle).toFloat()
            val innerY = center.y + (radius - 6f - markerLength) * sin(angle).toFloat()

            markers.add(
                MarkerInfo(
                    start = Offset(innerX, innerY),
                    end = Offset(outerX, outerY),
                    isMain = isMainMarker,
                    width = markerWidth,
                    color = markerColor
                )
            )
        }
        markers
    }

    // Cache precomputed coordinates for the 60 minute dots (excluding hour marker slots)
    val minuteDots = remember(center, radius) {
        val dots = ArrayList<Offset>()
        for (i in 0 until 60) {
            if (i % 5 != 0) {
                val angle = Math.toRadians((i * 6 - 90).toDouble())
                val dotX = center.x + (radius - 6f) * cos(angle).toFloat()
                val dotY = center.y + (radius - 6f) * sin(angle).toFloat()
                dots.add(Offset(dotX, dotY))
            }
        }
        dots
    }

    Canvas(modifier = modifier.size(size)) {
        // Outer glow ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentColor.copy(alpha = 0.15f), Color.Transparent),
                center = center,
                radius = radius + 20f,
            ),
            radius = radius + 20f,
            center = center,
        )

        // Outer ring
        drawCircle(
            color = DarkBorder,
            radius = radius,
            center = center,
            style = Stroke(width = 2f),
        )

        // Inner subtle ring
        drawCircle(
            color = DarkBorder.copy(alpha = 0.3f),
            radius = radius - 16f,
            center = center,
            style = Stroke(width = 1f),
        )

        // Draw Cached Hour markers (avoiding trig math on every frame)
        for (marker in hourMarkers) {
            drawLine(
                color = marker.color,
                start = marker.start,
                end = marker.end,
                strokeWidth = marker.width,
                cap = StrokeCap.Round,
            )
        }

        // Draw Cached Minute dots (avoiding trig math on every frame)
        for (dot in minuteDots) {
            drawCircle(
                color = TextSecondary.copy(alpha = 0.2f),
                radius = 1.0f,
                center = dot,
            )
        }

        // Hour hand (only 1 trig calculation per hand, negligible cost)
        val hourAngle = Math.toRadians(((hour * 30) + (minute * 0.5) - 90).toDouble())
        drawHand(
            center = center,
            angle = hourAngle,
            length = radius * 0.5f,
            width = 4f,
            color = TextPrimary,
        )

        // Minute hand
        val minuteAngle = Math.toRadians((minute * 6 - 90).toDouble())
        drawHand(
            center = center,
            angle = minuteAngle,
            length = radius * 0.7f,
            width = 3f,
            color = TextPrimary,
        )

        // Second hand (smoothly sweeps via animation angle updates)
        val secAngle = Math.toRadians((animatedSecondAngle - 90).toDouble())
        drawHand(
            center = center,
            angle = secAngle,
            length = radius * 0.8f,
            width = 1.5f,
            color = accentColor,
        )

        // Center dot (layered)
        drawCircle(
            color = accentColor,
            radius = 6f,
            center = center,
        )
        drawCircle(
            color = accentColor.copy(alpha = 0.3f),
            radius = 10f,
            center = center,
        )
    }
}

private fun DrawScope.drawHand(
    center: Offset,
    angle: Double,
    length: Float,
    width: Float,
    color: Color,
) {
    val endX = center.x + length * cos(angle).toFloat()
    val endY = center.y + length * sin(angle).toFloat()

    // Small tail in opposite direction
    val tailLength = length * 0.15f
    val tailX = center.x - tailLength * cos(angle).toFloat()
    val tailY = center.y - tailLength * sin(angle).toFloat()

    drawLine(
        color = color,
        start = Offset(tailX, tailY),
        end = Offset(endX, endY),
        strokeWidth = width,
        cap = StrokeCap.Round,
    )
}
