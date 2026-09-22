package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.Card
import com.example.core.model.CardBackStyle
import com.example.core.model.Suit
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.LalezarFamily

/**
 * Realistic playing card face:
 * - Rounded 10-12dp corners
 * - Subtle gray border & realistic soft shadow
 * - Suit colors: red #D32F2F / black #1A1A1A
 * - Playable state: full brightness + thin gold edge glow
 * - Illegal state: dimmed to ~55% + desaturated
 * - Playing animation: lifts 24dp, scales 1.05
 */
@Composable
fun CardView(
    card: Card,
    modifier: Modifier = Modifier,
    isPlayable: Boolean = true,
    isSelected: Boolean = false,
    rotation: Float = 0f,
    width: Dp = 66.dp,
    height: Dp = 96.dp,
    onClick: (() -> Unit)? = null
) {
    val liftOffset by animateDpAsState(
        targetValue = if (isSelected) (-24).dp else if (isPlayable) (-4).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "cardLift"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "cardScale"
    )

    val currentRotation by animateFloatAsState(
        targetValue = rotation,
        label = "cardRotation"
    )

    val suitRed = Color(0xFFD32F2F)
    val suitBlack = Color(0xFF1A1A1A)
    val suitColor = if (card.suit.isRed) suitRed else suitBlack

    val cardAlpha = if (isPlayable) 1f else 0.55f

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .offset(y = liftOffset)
            .rotate(currentRotation)
            .scale(scale)
            .alpha(cardAlpha)
            .shadow(
                elevation = if (isSelected) 16.dp else if (isPlayable) 8.dp else 2.dp,
                shape = RoundedCornerShape(11.dp),
                ambientColor = if (isPlayable) GoldLight.copy(alpha = 0.4f) else Color(0x66000000),
                spotColor = if (isPlayable) GoldLight else Color(0x88000000)
            )
            .clip(RoundedCornerShape(11.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFCFCF9),
                        Color(0xFFF3F1EC)
                    )
                )
            )
            .border(
                width = if (isPlayable) 1.5.dp else 0.8.dp,
                brush = if (isPlayable) GoldBorderBrush else Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))),
                shape = RoundedCornerShape(11.dp)
            )
            .then(
                if (onClick != null) {
                    Modifier
                        .testTag("card_${card.suit.name}_${card.rank.name}")
                        .clickable(enabled = isPlayable, onClick = onClick)
                } else Modifier
            )
            .padding(4.dp)
    ) {
        // Top-right index (RTL orientation standard)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.display,
                color = suitColor,
                fontSize = (width.value * 0.22).sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal,
                lineHeight = (width.value * 0.22).sp
            )
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = (width.value * 0.21).sp,
                lineHeight = (width.value * 0.21).sp
            )
        }

        // Center Big Watermark & Emblem
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Subtle watermark
            Text(
                text = card.suit.symbol,
                color = suitColor.copy(alpha = 0.12f),
                fontSize = (width.value * 0.65).sp,
                fontWeight = FontWeight.Bold
            )

            // Sharp center suit emblem
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = (width.value * 0.36).sp
            )
        }

        // Bottom-left inverted mini index
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .rotate(180f)
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.display,
                color = suitColor,
                fontSize = (width.value * 0.20).sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal,
                lineHeight = (width.value * 0.20).sp
            )
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = (width.value * 0.19).sp,
                lineHeight = (width.value * 0.19).sp
            )
        }
    }
}

/**
 * Opponent & Deck Card Back
 * ORIGINAL Persian Design:
 * - Lacquer red with gold diamond lattice & central medallion
 * - Drawn strictly via Canvas (no bitmaps)
 * - Supports Persian, Royal Blue, Gold, Black, and Minimal variants
 */
@Composable
fun CardBackView(
    style: CardBackStyle = CardBackStyle.PERSIAN,
    modifier: Modifier = Modifier,
    width: Dp = 60.dp,
    height: Dp = 88.dp
) {
    val (baseBrush, goldAccent, secondaryAccent) = when (style) {
        CardBackStyle.PERSIAN, CardBackStyle.CLASSIC_RED -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF8B0000), Color(0xFF5E0000), Color(0xFF3B0000))),
            GoldLight,
            Color(0xFFD4A017)
        )
        CardBackStyle.ROYAL_BLUE -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF1565C0), Color(0xFF0D47A1), Color(0xFF07265E))),
            Color(0xFF90CAF9),
            Color(0xFF42A5F5)
        )
        CardBackStyle.GOLD -> Triple(
            Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFA000), Color(0xFF7F4F00))),
            Color(0xFFFFFDE7),
            Color(0xFFFFB300)
        )
        CardBackStyle.BLACK -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF2C2C2C), Color(0xFF1A1A1A), Color(0xFF0F0F0F))),
            GoldLight,
            GoldBorder
        )
        CardBackStyle.MINIMAL -> Triple(
            Brush.verticalGradient(listOf(Color(0xFF455A64), Color(0xFF263238), Color(0xFF131A1D))),
            Color(0xFFECEFF1),
            Color(0xFFB0BEC5)
        )
    }

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .shadow(6.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(baseBrush)
            .border(1.6.dp, goldAccent, RoundedCornerShape(10.dp))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Inset gold border
            drawRoundRect(
                color = goldAccent.copy(alpha = 0.8f),
                topLeft = Offset(2f, 2f),
                size = Size(w - 4f, h - 4f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.2f)
            )

            // Diamond lattice pattern
            val step = w * 0.22f
            var cy = 0f
            while (cy < h + step) {
                var cx = 0f
                while (cx < w + step) {
                    val diamond = Path().apply {
                        moveTo(cx, cy - step / 2)
                        lineTo(cx + step / 2, cy)
                        lineTo(cx, cy + step / 2)
                        lineTo(cx - step / 2, cy)
                        close()
                    }
                    drawPath(diamond, goldAccent.copy(alpha = 0.18f), style = Stroke(width = 0.8f))
                    cx += step
                }
                cy += step
            }

            // Central Persian Sun / Medallion
            val center = Offset(w / 2, h / 2)
            drawCircle(
                color = goldAccent.copy(alpha = 0.35f),
                center = center,
                radius = w * 0.35f,
                style = Stroke(width = 1.2f)
            )
            drawCircle(
                color = goldAccent.copy(alpha = 0.2f),
                center = center,
                radius = w * 0.22f
            )

            // Central star rays
            val rayCount = 8
            for (i in 0 until rayCount) {
                val angle = (i * (360f / rayCount)) * (Math.PI / 180.0)
                val rx = center.x + (Math.cos(angle) * w * 0.3f).toFloat()
                val ry = center.y + (Math.sin(angle) * w * 0.3f).toFloat()
                drawLine(
                    color = goldAccent.copy(alpha = 0.6f),
                    start = center,
                    end = Offset(rx, ry),
                    strokeWidth = 1f
                )
            }
        }

        // Center emblem
        Text(
            text = "👑",
            fontSize = (width.value * 0.26).sp
        )
    }
}

/**
 * Natural Fan / Arc Hand arrangement for player cards:
 * - Arc/fan arrangement at bottom, overlapping ~40%
 * - Cards lift and highlight when playable and touched
 */
@Composable
fun FannedHandLayout(
    hand: List<Card>,
    isMyTurn: Boolean,
    onCardClick: (Card) -> Unit,
    isCardPlayable: (Card) -> Boolean,
    modifier: Modifier = Modifier
) {
    val count = hand.size
    val totalSpreadAngle = 24f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(118.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy((-24).dp), // ~40% overlap
            verticalAlignment = Alignment.Bottom
        ) {
            hand.forEachIndexed { index, card ->
                val normalizedIndex = if (count > 1) (index.toFloat() / (count - 1)) - 0.5f else 0f
                val rot = normalizedIndex * totalSpreadAngle
                val parabolicLift = -((1f - (normalizedIndex * normalizedIndex * 4f).coerceIn(0f, 1f)) * 8f)

                val playable = isMyTurn && isCardPlayable(card)

                CardView(
                    card = card,
                    isPlayable = playable,
                    rotation = rot,
                    modifier = Modifier.offset(y = parabolicLift.dp),
                    width = 64.dp,
                    height = 96.dp,
                    onClick = {
                        if (playable) {
                            onCardClick(card)
                        }
                    }
                )
            }
        }
    }
}
