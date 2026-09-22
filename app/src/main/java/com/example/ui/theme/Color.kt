package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// ROYAL BURGUNDY & METALLIC GOLD COLOR SYSTEM
// ==========================================

// Burgundy Theme Tokens (NOT purple)
val BurgundyTop = Color(0xFF4A1010)
val BurgundyBottom = Color(0xFF200404)
val BurgundyPanel = Color(0xFF2B0A0A)
val BurgundyPanelDark = Color(0xFF1E0606)
val BurgundyCard = Color(0xFF380E0E)
val BurgundyBorder = Color(0xFF5A1616)

// Gold Accents
val GoldLight = Color(0xFFF5C542)
val GoldMid = Color(0xFFD4A017)
val GoldDark = Color(0xFF8B6914)
val GoldBorder = Color(0xFFC9A227)
val TitleGold = Color(0xFFE8C766)
val GoldBevelHighlight = Color(0xFFFFF4D0)

// Ivory Text & Neutrals
val IvoryTextPrimary = Color(0xFFF5E6C8)
val IvoryTextSecondary = Color(0xFFD8C7AA)
val IvoryMuted = Color(0xFFA89578)

// Success Green (glossy gradient for buttons)
val SuccessGreenLight = Color(0xFF7CB342)
val SuccessGreenDark = Color(0xFF33691E)

// Backwards compatibility mappings
val PersianGold = GoldMid
val PersianGoldLight = GoldLight
val PersianGoldDark = GoldDark
val PersianGoldDeep = Color(0xFF5E4506)

val VelvetNight = BurgundyBottom
val VelvetSurface = BurgundyPanel
val VelvetSurfaceVariant = BurgundyCard
val VelvetCard = BurgundyCard
val VelvetBorder = BurgundyBorder

val PersianCrimson = Color(0xFF900C3F)
val PersianCrimsonLight = Color(0xFFC71556)
val PersianCrimsonDark = Color(0xFF4A001F)

val PersianTurquoise = Color(0xFF00ADB5)
val PersianTurquoiseLight = Color(0xFF4DD0E1)
val PersianTurquoiseDark = Color(0xFF006978)

val PersianEmerald = Color(0xFF1B5E20)
val FeltGreenLight = Color(0xFF23864C)
val FeltGreenMid = Color(0xFF165B33)
val FeltGreenDark = Color(0xFF0D3B20)
val FeltGreenDeep = Color(0xFF061E10)

val WoodBorderLight = Color(0xFF8D6E63)
val WoodBorderMid = Color(0xFF5D4037)
val WoodBorderDark = Color(0xFF3E2723)

val CardBorderGold = GoldBorder
val CardBorderShine = GoldBevelHighlight

val HokmDarkColorScheme = BurgundyBottom
val HokmTextPrimary = IvoryTextPrimary
val HokmTextSecondary = IvoryTextSecondary
val HokmTextGold = TitleGold

// Premium Gradients
val BurgundyBackgroundGradient = Brush.verticalGradient(
    colors = listOf(BurgundyTop, BurgundyBottom)
)

val PanelBackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF380E0E), BurgundyPanel, BurgundyPanelDark)
)

val GoldLinearGradient = Brush.verticalGradient(
    colors = listOf(GoldLight, GoldMid, GoldDark)
)

val GoldBevelGradient = Brush.linearGradient(
    colors = listOf(GoldBevelHighlight, GoldLight, GoldMid, GoldDark)
)

val GoldBorderBrush = Brush.linearGradient(
    colors = listOf(GoldBevelHighlight, GoldLight, GoldBorder, GoldDark, GoldLight)
)

val SuccessGreenGradient = Brush.verticalGradient(
    colors = listOf(SuccessGreenLight, SuccessGreenDark)
)

val CrimsonGameButtonGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFD32F2F), Color(0xFF9A0007), Color(0xFF5F0004))
)

val PurpleGameButtonGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF7B1FA2), Color(0xFF4A148C), Color(0xFF2E0854))
)

val EmeraldGameButtonGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20), Color(0xFF0D3B20))
)

val BlueGameButtonGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF1976D2), Color(0xFF0D47A1), Color(0xFF082B66))
)

val GreenGameButtonGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32), Color(0xFF1B5E20))
)

val VelvetCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF380E0E), Color(0xFF2A0808), Color(0xFF1C0505))
)
