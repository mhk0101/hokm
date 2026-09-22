package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.PlayerSlot
import com.example.ui.theme.BurgundyBottom
import com.example.ui.theme.BurgundyPanel
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.GoldMid
import com.example.ui.theme.IvoryTextPrimary
import com.example.ui.theme.IvoryTextSecondary
import com.example.ui.theme.LalezarFamily
import com.example.ui.theme.VazirmatnFamily
import com.example.ui.theme.toPersianDigits

/**
 * Player Avatar Seat View:
 * - Circular avatar with gold rim
 * - Name in a dark pill plate below avatar; rating in small text
 * - Turn indicator: animated gold glow ring around active player's avatar + floating pill "نوبت شماست"
 * - Disconnected player: desaturated avatar + animated dashed connection ring + "در حال اتصال مجدد"
 */
@Composable
fun PlayerAvatarView(
    player: PlayerSlot,
    isCurrentTurn: Boolean,
    modifier: Modifier = Modifier,
    activeEmote: String? = null,
    level: Int = 14,
    rating: Int = 1420
) {
    val infiniteTransition = rememberInfiniteTransition(label = "turnPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = if (isCurrentTurn) 1.18f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    val dashOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dashOffset"
    )

    val avatarBrush = when (player.seatIndex % 4) {
        0 -> Brush.linearGradient(listOf(Color(0xFF8B1228), Color(0xFFC2185B), Color(0xFF4A001F)))
        1 -> Brush.linearGradient(listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF004D40)))
        2 -> Brush.linearGradient(listOf(Color(0xFF1B5E20), Color(0xFF388E3C), Color(0xFF003300)))
        else -> Brush.linearGradient(listOf(Color(0xFFE65100), Color(0xFFF57C00), Color(0xFFBF360C)))
    }

    Box(
        modifier = modifier.testTag("avatar_seat_${player.seatIndex}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Animated Gold Glow Ring for Active Turn
                if (isCurrentTurn && player.isConnected) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(pulseGlow)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        GoldLight.copy(alpha = 0.55f),
                                        GoldMid.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // Disconnected Player Dashed Ring
                if (!player.isConnected) {
                    Canvas(modifier = Modifier.size(56.dp)) {
                        drawCircle(
                            color = Color(0xFFFF5252),
                            radius = size.width / 2,
                            style = Stroke(
                                width = 2.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), dashOffset)
                            )
                        )
                    }
                }

                // Main Avatar Circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .alpha(if (player.isConnected) 1f else 0.55f)
                        .shadow(if (isCurrentTurn) 12.dp else 4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(avatarBrush)
                        .border(
                            width = if (isCurrentTurn) 2.5.dp else 1.8.dp,
                            brush = if (isCurrentTurn) GoldBorderBrush else Brush.linearGradient(
                                listOf(GoldLight.copy(alpha = 0.8f), GoldBorder.copy(alpha = 0.4f))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = player.username,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Hakem Crown
                if (player.isHakem) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-24).dp)
                            .shadow(6.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldLinearGradient)
                            .border(1.2.dp, Color(0xFFFFF9C4), RoundedCornerShape(12.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "👑 حاکم",
                            color = BurgundyBottom,
                            fontSize = 10.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                // Online/Offline status dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 1.dp, y = 1.dp)
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(if (player.isConnected) Color(0xFF00E676) else Color(0xFFFF1744))
                        .border(1.2.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Dark Pill Plate for Name & Rating
            Box(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xDD1E0505))
                    .border(1.dp, if (isCurrentTurn) GoldBorder else GoldBorder.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = player.username.ifEmpty { "بازیکن ${(player.seatIndex + 1).toPersianDigits()}" },
                        color = if (isCurrentTurn) GoldLight else IvoryTextPrimary,
                        fontSize = 11.5.sp,
                        fontFamily = LalezarFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (!player.isConnected) "قطع ارتباط" else "⭐ ${rating.toPersianDigits()}",
                        color = if (!player.isConnected) Color(0xFFFF8A80) else IvoryTextSecondary,
                        fontSize = 9.sp,
                        fontFamily = VazirmatnFamily
                    )
                }
            }

            // Cards count chip
            Box(
                modifier = Modifier
                    .offset(y = (-2).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xAA000000))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "${player.cardCount.toPersianDigits()} برگ",
                    color = GoldLight,
                    fontSize = 9.sp,
                    fontFamily = VazirmatnFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Floating Pill: "نوبت شماست" for bottom player (seat 0 / current turn)
        if (isCurrentTurn && player.seatIndex == 0) {
            Box(
                modifier = Modifier
                    .offset(y = (-48).dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(GoldLinearGradient)
                    .border(1.2.dp, Color(0xFFFFFDE7), RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "نوبت شماست!",
                    color = BurgundyBottom,
                    fontSize = 11.sp,
                    fontFamily = LalezarFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Disconnected notice pill
        if (!player.isConnected) {
            Box(
                modifier = Modifier
                    .offset(y = (-44).dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xEEB71C1C))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "در حال اتصال مجدد...",
                    color = Color.White,
                    fontSize = 9.5.sp,
                    fontFamily = VazirmatnFamily
                )
            }
        }

        // Active Emote Bubble
        AnimatedVisibility(
            visible = activeEmote != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.offset(y = (-52).dp)
        ) {
            Box(
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2B0A0A))
                    .border(1.5.dp, GoldBorderBrush, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = activeEmote ?: "", fontSize = 22.sp)
            }
        }
    }
}
