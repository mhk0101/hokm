package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BurgundyBottom
import com.example.ui.theme.BurgundyPanel
import com.example.ui.theme.BurgundyPanelDark
import com.example.ui.theme.BurgundyTop
import com.example.ui.theme.CrimsonGameButtonGradient
import com.example.ui.theme.EmeraldGameButtonGradient
import com.example.ui.theme.GoldBevelGradient
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.GoldMid
import com.example.ui.theme.IvoryTextPrimary
import com.example.ui.theme.IvoryTextSecondary
import com.example.ui.theme.LalezarFamily
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldLight
import com.example.ui.theme.PurpleGameButtonGradient
import com.example.ui.theme.SuccessGreenGradient
import com.example.ui.theme.TitleGold
import com.example.ui.theme.VazirmatnFamily
import com.example.ui.theme.formatCoinsPersian
import com.example.ui.theme.toPersianDigits

enum class GameButtonColor {
    GOLD,
    CRIMSON,
    GREEN,
    BLUE,
    PURPLE,
    VELVET
}

/**
 * Global Reusable Background System
 * - Burgundy vertical gradient base (#4A1010 → #200404)
 * - Subtle Persian diamond/rhombus lattice pattern overlay at 4-6% opacity (drawn via Canvas)
 * - Soft radial vignette (darker corners)
 * - 6-8 small floating bokeh dots (gold/red, alpha 10-18%, slow drift)
 * - Lightweight: no expensive blur modifiers
 */
@Composable
fun OrnamentBackground(
    modifier: Modifier = Modifier,
    enableAnimations: Boolean = true,
    content: @Composable () -> Unit
) {
    // 6-8 Floating bokeh animation
    val infiniteTransition = rememberInfiniteTransition(label = "bokeh")
    val animOffset by if (enableAnimations) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 8000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bokehOffset"
        )
    } else {
        remember { mutableStateOf(0.5f) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BurgundyBottom)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Royal burgundy vertical gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(BurgundyTop, Color(0xFF330808), BurgundyBottom)
                )
            )

            // 2. Subtle Persian diamond lattice pattern (4-6% opacity)
            val latticeSize = 36f
            val latticeColor = GoldLight.copy(alpha = 0.05f)
            var y = 0f
            while (y < h + latticeSize) {
                var x = 0f
                while (x < w + latticeSize) {
                    val path = Path().apply {
                        moveTo(x, y - latticeSize / 2)
                        lineTo(x + latticeSize / 2, y)
                        lineTo(x, y + latticeSize / 2)
                        lineTo(x - latticeSize / 2, y)
                        close()
                    }
                    drawPath(path, latticeColor, style = Stroke(width = 1f))
                    x += latticeSize
                }
                y += latticeSize
            }

            // 3. Soft radial vignette (darker edges & corners)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x55000000),
                        Color(0xDD0A0000)
                    ),
                    center = Offset(w / 2, h / 2),
                    radius = (w.coerceAtLeast(h)) * 0.75f
                )
            )

            // 4. 6-8 small floating bokeh light particles (alpha 10-18%)
            val bokehPositions = listOf(
                Pair(0.2f, 0.15f),
                Pair(0.8f, 0.22f),
                Pair(0.15f, 0.45f),
                Pair(0.85f, 0.55f),
                Pair(0.35f, 0.72f),
                Pair(0.7f, 0.82f),
                Pair(0.5f, 0.35f)
            )

            bokehPositions.forEachIndexed { i, (relX, relY) ->
                val driftX = if (i % 2 == 0) (animOffset - 0.5f) * 30f else -(animOffset - 0.5f) * 25f
                val driftY = if (i % 3 == 0) -(animOffset - 0.5f) * 35f else (animOffset - 0.5f) * 30f
                val cx = (w * relX) + driftX
                val cy = (h * relY) + driftY
                val radius = (18f + (i * 3f))
                val color = if (i % 2 == 0) GoldLight.copy(alpha = 0.14f) else Color(0xFFEF5350).copy(alpha = 0.12f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color, Color.Transparent),
                        center = Offset(cx, cy),
                        radius = radius
                    ),
                    center = Offset(cx, cy),
                    radius = radius
                )
            }
        }

        // Content
        content()
    }
}

/**
 * Compatible alias for existing screens
 */
@Composable
fun PersianBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    OrnamentBackground(modifier = modifier, content = content)
}

/**
 * Glossy, Beveled, 3D Game Button with Metallic Gold Border
 * - 2.5dp metallic gold border (gradient gold, beveled look)
 * - 1dp inner light highlight on top edge
 * - Drop shadow below
 * - Pressed state = scale 0.97 + darker
 * - Lalezar Font
 */
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorStyle: GameButtonColor = GameButtonColor.GOLD,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 56.dp,
    fontSize: TextUnit = 16.sp,
    subtitle: String? = null,
    badge: String? = null,
    testTag: String? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = tween(durationMillis = 90, easing = FastOutSlowInEasing),
        label = "btnScale"
    )

    val gradient = when (colorStyle) {
        GameButtonColor.GOLD -> GoldLinearGradient
        GameButtonColor.CRIMSON -> CrimsonGameButtonGradient
        GameButtonColor.GREEN -> EmeraldGameButtonGradient
        GameButtonColor.PURPLE -> PurpleGameButtonGradient
        GameButtonColor.BLUE -> Brush.verticalGradient(listOf(Color(0xFF1E88E5), Color(0xFF1565C0), Color(0xFF0D47A1)))
        GameButtonColor.VELVET -> Brush.verticalGradient(listOf(Color(0xFF421010), BurgundyPanel, Color(0xFF1A0404)))
    }

    val textColor = if (colorStyle == GameButtonColor.GOLD) BurgundyBottom else IvoryTextPrimary

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .shadow(
                elevation = if (isPressed) 3.dp else 10.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x66000000),
                spotColor = GoldMid.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .border(
                width = 2.5.dp,
                brush = GoldBorderBrush,
                shape = RoundedCornerShape(18.dp)
            )
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { onClick() }
                    )
                }
            }
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Top edge 1dp glossy shine sweep highlight
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.White.copy(alpha = 0.35f),
                start = Offset(12f, 2f),
                end = Offset(size.width - 12f, 2f),
                strokeWidth = 1.8f,
                cap = StrokeCap.Round
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color(0x33000000))
                            .border(1.dp, GoldBorder.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (colorStyle == GameButtonColor.GOLD) BurgundyBottom else GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = text,
                        color = textColor,
                        fontSize = fontSize,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = textColor.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            fontFamily = VazirmatnFamily
                        )
                    }
                }
            }

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x44000000))
                        .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        color = GoldLight,
                        fontSize = 11.sp,
                        fontFamily = VazirmatnFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Secondary Action: CIRCULAR GOLD BUTTON (56-64dp)
 * - Red radial gradient interior
 * - Thick gold rim with top-left highlight arc
 * - Gold icon inside
 * - Gold label below the circle
 */
@Composable
fun CircularGoldButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 58.dp,
    testTag: String? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "circBtnScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF8B1212), Color(0xFF4A0A0A), Color(0xFF2B0404)),
                        center = Offset.Zero
                    )
                )
                .border(2.5.dp, GoldBorderBrush, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Highlight arc in top-left
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.toPx() / 2 - 3f
                drawArc(
                    color = Color.White.copy(alpha = 0.45f),
                    startAngle = 190f,
                    sweepAngle = 70f,
                    useCenter = false,
                    topLeft = Offset(3f, 3f),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 2.2f, cap = StrokeCap.Round)
                )
            }

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GoldLight,
                modifier = Modifier.size((size.value * 0.45).dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = GoldLight,
            fontSize = 11.5.sp,
            fontFamily = LalezarFamily,
            fontWeight = FontWeight.Normal
        )
    }
}

/**
 * Royal Gold Frame Panel
 * - #2B0A0A background
 * - 1.5dp gold border #C9A227
 * - Rounded 16dp
 * - Soft outer shadow
 */
@Composable
fun GoldFramePanel(
    modifier: Modifier = Modifier,
    borderColor: Color = GoldBorder,
    borderWidth: Dp = 1.5.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color(0x66000000),
                spotColor = GoldMid.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF380E0E), BurgundyPanel, BurgundyPanelDark)
                )
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        content()
    }
}

/**
 * GamePanel alias for existing usages
 */
@Composable
fun GamePanel(
    modifier: Modifier = Modifier,
    borderColor: Color = GoldBorder,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    GoldFramePanel(
        modifier = modifier,
        borderColor = borderColor,
        cornerRadius = cornerRadius,
        content = content
    )
}

/**
 * Resource Pill: Dark Translucent Capsule with Gold Border + Green "+" Button
 */
@Composable
fun ResourcePill(
    iconText: String,
    valueText: String,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = true
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xCC1A0505))
            .border(1.2.dp, GoldBorderBrush, RoundedCornerShape(20.dp))
            .padding(start = 8.dp, end = if (showAddButton) 3.dp else 10.dp, top = 3.dp, bottom = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = iconText,
                fontSize = 13.sp
            )

            Text(
                text = valueText.toPersianDigits(),
                color = GoldLight,
                fontSize = 13.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal
            )

            if (showAddButton) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(SuccessGreenGradient)
                        .border(1.dp, Color(0xFFC8E6C9), CircleShape)
                        .clickable(onClick = onAddClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزایش",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * CoinBadge backward compatibility
 */
@Composable
fun CoinBadge(
    coins: Long,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ResourcePill(
        iconText = "🪙",
        valueText = formatCoinsPersian(coins),
        onAddClick = { onClick?.invoke() },
        modifier = modifier,
        showAddButton = onClick != null
    )
}

/**
 * Rating & Level Badge
 */
@Composable
fun RatingBadge(
    rating: Int,
    leagueName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xDD240707))
            .border(1.dp, GoldBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⭐", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${rating.toPersianDigits()} ($leagueName)",
                color = IvoryTextPrimary,
                fontSize = 11.5.sp,
                fontFamily = VazirmatnFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Avatar with Ornate Gold Frame & Level Tag
 */
@Composable
fun AvatarBadge(
    username: String,
    level: Int,
    avatarId: String = "avatar_1",
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size + 6.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Avatar circle with thick gold frame
        Box(
            modifier = Modifier
                .size(size)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF5A1414), Color(0xFF280707))
                    )
                )
                .border(2.5.dp, GoldBorderBrush, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👑",
                fontSize = (size.value * 0.44).sp
            )
        }

        // Level pill on bottom edge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GoldBorderBrush)
                .border(0.8.dp, Color(0xFFFFF3D0), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 1.dp)
        ) {
            Text(
                text = "سطح ${level.toPersianDigits()}",
                color = BurgundyBottom,
                fontSize = 9.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Ornate Section Header with Persian Gold Wings
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.width(40.dp).height(2.dp)) {
            drawLine(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, GoldBorder)),
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 2f
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = TitleGold,
            fontSize = 16.sp,
            fontFamily = LalezarFamily,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.width(10.dp))

        Canvas(modifier = Modifier.width(40.dp).height(2.dp)) {
            drawLine(
                brush = Brush.horizontalGradient(listOf(GoldBorder, Color.Transparent)),
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 2f
            )
        }
    }
}

/**
 * Game Toast / Notification Banner
 * - Small floating pill top-center
 * - Dark translucent burgundy
 * - Gold border
 * - Ivory text
 * - Auto-dismiss with slide+fade
 */
@Composable
fun GameNotificationBanner(
    text: String?,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false
) {
    AnimatedVisibility(
        visible = !text.isNullOrEmpty(),
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isAlert) Brush.horizontalGradient(
                        listOf(Color(0xFF880E4F), Color(0xFFD81B60), Color(0xFF880E4F))
                    )
                    else Color(0xEE2B0A0A)
                )
                .border(
                    width = 1.5.dp,
                    brush = GoldBorderBrush,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 22.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text ?: "",
                color = if (isAlert) Color.White else IvoryTextPrimary,
                fontSize = 13.5.sp,
                fontFamily = VazirmatnFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Ornate Bottom Navigation Bar
 * - Dark burgundy panel with gold scroll/flourish edge decorations
 * - 5 circular gold icon buttons (فروشگاه، پروفایل، اتحاد، رده‌بندی، خانه)
 * - Active tab: bright gold + glow; inactive: dimmed bronze
 * - Labels in gold beneath icons in Lalezar
 */
@Composable
fun OrnateBottomNav(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        Triple(0, "خانه", Icons.Default.Home),
        Triple(1, "رده‌بندی", Icons.Default.Star),
        Triple(2, "اتحاد", Icons.Default.Shield),
        Triple(3, "فروشگاه", Icons.Default.ShoppingBag),
        Triple(4, "پروفایل", Icons.Default.Person)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .shadow(16.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF380E0E), BurgundyPanel, BurgundyBottom)
                )
            )
            .border(
                width = 1.5.dp,
                brush = GoldBorderBrush,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
    ) {
        // Gold scroll/flourish decoration top edge line
        Canvas(modifier = Modifier.fillMaxWidth().height(4.dp)) {
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, GoldLight, GoldBorder, GoldLight, Color.Transparent)
                ),
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 2.5f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { (index, title, icon) ->
                val isSelected = selectedIndex == index

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onSelect(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 38.dp else 32.dp)
                            .shadow(if (isSelected) 6.dp else 0.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) GoldLinearGradient
                                else Color(0x33000000)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Color(0xFFFFF3D0) else GoldDark.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isSelected) BurgundyBottom else GoldDark,
                            modifier = Modifier.size(if (isSelected) 22.dp else 18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = title,
                        color = if (isSelected) GoldLight else GoldDark,
                        fontSize = 10.5.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = if (isSelected) FontWeight.Normal else FontWeight.Normal
                    )
                }
            }
        }
    }
}
