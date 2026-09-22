package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.core.model.TableStyle
import com.example.ui.theme.BurgundyBottom
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight

/**
 * Premium Royal Persian Hokm Table Surface
 * - Dark wood-tone border with thin gold inlay line, rounded corners
 * - Deep green felt with radial gradient (lighter center) + procedural fabric weave texture via Canvas
 * - Gold ornamental corner accents on the table frame
 * - Center trick area: slightly darker felt oval where played cards land
 */
@Composable
fun TableSurface(
    tableStyle: TableStyle,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BurgundyBottom)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Burgundy luxury ambient room background
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF380E0E), Color(0xFF220505), BurgundyBottom),
                    center = Offset(width / 2, height / 2),
                    radius = width.coerceAtLeast(height) * 0.85f
                )
            )

            // Table geometry
            val marginX = width * 0.035f
            val marginTop = height * 0.07f
            val marginBottom = height * 0.13f
            val tableW = width - (marginX * 2)
            val tableH = height - marginTop - marginBottom
            val tableTopLeft = Offset(marginX, marginTop)
            val cornerRadius = CornerRadius(tableW * 0.38f, tableH * 0.32f)

            // 2. Drop Shadow under table
            drawRoundRect(
                color = Color(0x99000000),
                topLeft = Offset(tableTopLeft.x + 4f, tableTopLeft.y + 12f),
                size = Size(tableW, tableH),
                cornerRadius = cornerRadius
            )

            // 3. Dark Wood-Tone Outer Armrest Rail
            val (feltColors, woodRailColors, goldInlayColor) = when (tableStyle) {
                TableStyle.PERSIAN_CARPET -> Triple(
                    listOf(Color(0xFF8B1228), Color(0xFF5E0B1A), Color(0xFF35040D)),
                    listOf(Color(0xFF4A101C), Color(0xFF2A080F), Color(0xFF160306)),
                    GoldLight
                )
                TableStyle.CLASSIC_GREEN, TableStyle.WOODEN_TABLE, TableStyle.MINIMAL, TableStyle.DARK_ELEGANT, TableStyle.ROYAL_BLUE, TableStyle.NIGHT -> Triple(
                    listOf(Color(0xFF1E703E), Color(0xFF12522B), Color(0xFF093319)),
                    listOf(Color(0xFF42281D), Color(0xFF2A170F), Color(0xFF1A0C06)),
                    GoldLight
                )
            }

            drawRoundRect(
                brush = Brush.verticalGradient(woodRailColors),
                topLeft = tableTopLeft,
                size = Size(tableW, tableH),
                cornerRadius = cornerRadius
            )

            // 4. Thin Metallic Gold Inlay Line on Outer Rail
            drawRoundRect(
                brush = Brush.linearGradient(
                    listOf(goldInlayColor, GoldBorder, goldInlayColor.copy(alpha = 0.4f))
                ),
                topLeft = Offset(tableTopLeft.x + 3f, tableTopLeft.y + 3f),
                size = Size(tableW - 6f, tableH - 6f),
                cornerRadius = CornerRadius(cornerRadius.x - 3f, cornerRadius.y - 3f),
                style = Stroke(width = 2.2f)
            )

            // 5. Inset Deep Felt Surface
            val railInset = 16f
            val feltTopLeft = Offset(tableTopLeft.x + railInset, tableTopLeft.y + railInset)
            val feltSize = Size(tableW - (railInset * 2), tableH - (railInset * 2))
            val feltCornerRadius = CornerRadius(cornerRadius.x - railInset, cornerRadius.y - railInset)
            val tableCenter = Offset(width / 2, marginTop + (tableH * 0.48f))

            // Radial Felt fill (lighter center spotlight)
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = feltColors,
                    center = tableCenter,
                    radius = tableW * 0.65f
                ),
                topLeft = feltTopLeft,
                size = feltSize,
                cornerRadius = feltCornerRadius
            )

            // 6. Procedural Felt Weave Texture (subtle micro-lattice overlay)
            val weaveStep = 18f
            var wy = feltTopLeft.y
            while (wy < feltTopLeft.y + feltSize.height) {
                var wx = feltTopLeft.x
                while (wx < feltTopLeft.x + feltSize.width) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.015f),
                        center = Offset(wx, wy),
                        radius = 1.2f
                    )
                    wx += weaveStep
                }
                wy += weaveStep
            }

            // 7. Inner Gold Trim on Felt Edge
            drawRoundRect(
                color = goldInlayColor.copy(alpha = 0.65f),
                topLeft = Offset(feltTopLeft.x + 6f, feltTopLeft.y + 6f),
                size = Size(feltSize.width - 12f, feltSize.height - 12f),
                cornerRadius = CornerRadius(feltCornerRadius.x - 6f, feltCornerRadius.y - 6f),
                style = Stroke(width = 1.4f)
            )

            // 8. Gold Ornamental Corner Accents on the table frame
            val cornerSize = 24f
            val corners = listOf(
                tableTopLeft to Pair(1f, 1f),
                Offset(tableTopLeft.x + tableW, tableTopLeft.y) to Pair(-1f, 1f),
                Offset(tableTopLeft.x, tableTopLeft.y + tableH) to Pair(1f, -1f),
                Offset(tableTopLeft.x + tableW, tableTopLeft.y + tableH) to Pair(-1f, -1f)
            )
            corners.forEach { (pos, dir) ->
                val (dx, dy) = dir
                val p = Path().apply {
                    moveTo(pos.x + (dx * 18f), pos.y + (dy * 36f))
                    lineTo(pos.x + (dx * 18f), pos.y + (dy * 18f))
                    lineTo(pos.x + (dx * 36f), pos.y + (dy * 18f))
                }
                drawPath(p, goldInlayColor.copy(alpha = 0.7f), style = Stroke(width = 2f))
            }

            // 9. Center Trick Area: Slightly darker felt oval where played cards land
            val trickWidth = tableW * 0.44f
            val trickHeight = tableH * 0.32f
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55000000), Color(0x22000000), Color.Transparent),
                    center = tableCenter,
                    radius = trickWidth * 0.55f
                ),
                topLeft = Offset(tableCenter.x - (trickWidth / 2), tableCenter.y - (trickHeight / 2)),
                size = Size(trickWidth, trickHeight)
            )

            // Delicate gold oval guideline around trick area
            drawOval(
                color = goldInlayColor.copy(alpha = 0.22f),
                topLeft = Offset(tableCenter.x - (trickWidth / 2), tableCenter.y - (trickHeight / 2)),
                size = Size(trickWidth, trickHeight),
                style = Stroke(width = 1.2f)
            )

            // Central Sunburst / Medallion Accent
            drawCircle(
                color = goldInlayColor.copy(alpha = 0.08f),
                center = tableCenter,
                radius = tableW * 0.12f,
                style = Stroke(width = 1.5f)
            )
        }

        content()
    }
}
