package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldPrimary

@Composable
fun MusaffaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = EmeraldPrimary,
    animated: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (animated) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier.size(size * pulse),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val strokeWidth = w * 0.09f

            // Center circle
            drawCircle(
                color = tint,
                radius = w * 0.12f,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Inner center dot
            drawCircle(
                color = tint,
                radius = w * 0.05f,
                center = center
            )

            // Four symmetrical looping clover knot petals
            for (angle in 0 until 4) {
                rotate(degrees = angle * 90f, pivot = center) {
                    val path = Path().apply {
                        val startX = w * 0.5f
                        val startY = h * 0.38f
                        moveTo(startX, startY)
                        // Loop up to the top lobe
                        cubicTo(
                            w * 0.35f, h * 0.28f,
                            w * 0.28f, h * 0.08f,
                            w * 0.5f, h * 0.08f
                        )
                        cubicTo(
                            w * 0.72f, h * 0.08f,
                            w * 0.65f, h * 0.28f,
                            w * 0.5f, h * 0.38f
                        )
                    }

                    drawPath(
                        path = path,
                        color = tint,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Outer interlock loop
                    val outerPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.36f)
                        cubicTo(
                            w * 0.22f, h * 0.22f,
                            w * 0.14f, h * 0.02f,
                            w * 0.5f, h * 0.02f
                        )
                        cubicTo(
                            w * 0.86f, h * 0.02f,
                            w * 0.78f, h * 0.22f,
                            w * 0.5f, h * 0.36f
                        )
                    }
                    drawPath(
                        path = outerPath,
                        color = tint.copy(alpha = 0.95f),
                        style = Stroke(
                            width = strokeWidth * 0.85f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
