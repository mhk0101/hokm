package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.GoldFramePanel
import com.example.ui.components.OrnamentBackground
import com.example.ui.theme.BurgundyPanel
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMid
import com.example.ui.theme.IvoryTextPrimary
import com.example.ui.theme.IvoryTextSecondary
import com.example.ui.theme.LalezarFamily
import com.example.ui.theme.TitleGold
import com.example.ui.theme.VazirmatnFamily
import com.example.ui.theme.toPersianDigits
import kotlinx.coroutines.delay

/**
 * Matchmaking Screen:
 * - Ornate container
 * - Circular rotating compass/radar
 * - Persian player counter
 * - "لغو جستجو" button
 */
@Composable
fun MatchmakingScreen(viewModel: MainViewModel) {
    var secondsElapsed by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsElapsed++
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "radarTransition")
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarScale"
    )

    val playerCount = (2 + (secondsElapsed / 4)).coerceAtMost(4)

    OrnamentBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            GoldFramePanel(
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Circular Rotating Compass / Radar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(170.dp)
                    ) {
                        // Expanding Pulse Ring
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(GoldMid.copy(alpha = 0.12f))
                                .border(1.5.dp, GoldBorder.copy(alpha = 0.35f), CircleShape)
                        )

                        // Outer Gold Radar Compass Rim
                        Canvas(modifier = Modifier.size(140.dp)) {
                            val r = size.width / 2
                            val center = Offset(r, r)

                            drawCircle(
                                color = GoldBorder.copy(alpha = 0.4f),
                                radius = r,
                                style = Stroke(width = 2f)
                            )
                            drawCircle(
                                color = GoldBorder.copy(alpha = 0.25f),
                                radius = r * 0.65f,
                                style = Stroke(width = 1.5f)
                            )

                            // Rotating Sweep Line
                            val angleRad = (radarRotation * Math.PI / 180.0)
                            val sweepX = (center.x + Math.cos(angleRad) * r).toFloat()
                            val sweepY = (center.y + Math.sin(angleRad) * r).toFloat()
                            drawLine(
                                brush = Brush.linearGradient(
                                    listOf(GoldLight, GoldMid.copy(alpha = 0.2f), Color.Transparent),
                                    start = center,
                                    end = Offset(sweepX, sweepY)
                                ),
                                start = center,
                                end = Offset(sweepX, sweepY),
                                strokeWidth = 3f
                            )
                        }

                        // Center Emblem
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .shadow(12.dp, CircleShape)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF380E0E), BurgundyPanel)
                                    )
                                )
                                .border(2.5.dp, GoldBorderBrush, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 32.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "در حال یافتن حریفان...",
                        color = TitleGold,
                        fontSize = 22.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "تطبیق ۳ هم‌تیمی و حریف بر اساس رتبه‌بندی",
                        color = IvoryTextSecondary,
                        fontSize = 12.5.sp,
                        fontFamily = VazirmatnFamily
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Persian Player Counter
                    Box(
                        modifier = Modifier
                            .shadow(6.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x66000000))
                            .border(1.2.dp, GoldBorderBrush, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "👥 بازیکنان آماده:",
                                color = IvoryTextPrimary,
                                fontSize = 12.5.sp,
                                fontFamily = VazirmatnFamily
                            )
                            Text(
                                text = "${playerCount.toPersianDigits()} از ۴ نفر",
                                color = GoldLight,
                                fontSize = 13.sp,
                                fontFamily = LalezarFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Elapsed Time Badge
                    Text(
                        text = "⏱️ زمان انتظار: ${secondsElapsed.toPersianDigits()} ثانیه",
                        color = IvoryTextSecondary,
                        fontSize = 11.5.sp,
                        fontFamily = VazirmatnFamily
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // "لغو جستجو" Button
                    GameButton(
                        text = "لغو جستجو",
                        onClick = { viewModel.cancelMatchmaking() },
                        colorStyle = GameButtonColor.CRIMSON,
                        height = 48.dp,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "btn_cancel_matchmaking"
                    )
                }
            }
        }
    }
}
