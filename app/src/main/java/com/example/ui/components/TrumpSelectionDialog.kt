package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.model.Suit
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
import com.example.ui.theme.TitleGold
import com.example.ui.theme.VazirmatnFamily

/**
 * Royal Persian Trump Selection Dialog:
 * - 4 large glossy buttons (♠ پیک ♥ دل ♦ خشت ♣ گشنیز) with suit-colored gradients + gold frames
 * - "تأیید حکم" confirm button
 */
@Composable
fun TrumpSelectionDialog(
    onTrumpSelected: (Suit) -> Unit
) {
    var selectedSuit by remember { mutableStateOf<Suit?>(null) }

    Dialog(onDismissRequest = { /* Non-dismissible: Hakem must pick trump */ }) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = BurgundyPanel),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(22.dp))
                .border(2.2.dp, GoldBorderBrush, RoundedCornerShape(22.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF380E0E), BurgundyPanel, Color(0xFF1E0505))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Royal Crown Badge
                    Box(
                        modifier = Modifier
                            .shadow(6.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(GoldLinearGradient)
                            .border(1.2.dp, Color(0xFFFFFDE7), RoundedCornerShape(14.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "👑 فرمان حاکم",
                            color = BurgundyBottom,
                            fontSize = 14.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "تعیین خال حکم این مسابقه",
                        color = TitleGold,
                        fontSize = 20.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "با توجه به ۵ برگ اول خود، یکی از ۴ خال زیر را انتخاب نمایید:",
                        color = IvoryTextSecondary,
                        fontSize = 12.sp,
                        fontFamily = VazirmatnFamily
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 4 Large Glossy Suit Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Suit.entries.forEach { suit ->
                            val isSelected = selectedSuit == suit
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.08f else 1f,
                                animationSpec = tween(150),
                                label = "suitBtnScale"
                            )

                            // Suit gradient colors
                            val (cardBrush, symbolColor) = when (suit) {
                                Suit.HEARTS -> Pair(
                                    Brush.verticalGradient(listOf(Color(0xFFB71C1C), Color(0xFF7F0000))),
                                    Color.White
                                )
                                Suit.DIAMONDS -> Pair(
                                    Brush.verticalGradient(listOf(Color(0xFFD32F2F), Color(0xFF8E0000))),
                                    Color.White
                                )
                                Suit.SPADES -> Pair(
                                    Brush.verticalGradient(listOf(Color(0xFF263238), Color(0xFF102027))),
                                    Color(0xFFECEFF1)
                                )
                                Suit.CLUBS -> Pair(
                                    Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF003300))),
                                    Color(0xFFE8F5E9)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .scale(scale)
                                    .height(84.dp)
                                    .shadow(
                                        elevation = if (isSelected) 12.dp else 4.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        spotColor = if (isSelected) GoldLight else Color.Black
                                    )
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(cardBrush)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.2.dp,
                                        brush = if (isSelected) GoldBorderBrush else Brush.linearGradient(
                                            listOf(GoldBorder.copy(alpha = 0.6f), GoldBorder.copy(alpha = 0.2f))
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedSuit = suit }
                                    .testTag("select_suit_${suit.name}"),
                                contentAlignment = Alignment.Center
                            ) {
                                // Glossy top-edge reflection highlight
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.35f),
                                        start = Offset(8f, 2f),
                                        end = Offset(size.width - 8f, 2f),
                                        strokeWidth = 2f,
                                        cap = StrokeCap.Round
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = suit.symbol,
                                        color = symbolColor,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = suit.persianName,
                                        color = if (isSelected) GoldLight else IvoryTextPrimary,
                                        fontSize = 11.5.sp,
                                        fontFamily = LalezarFamily,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Confirm Button: "تأیید حکم"
                    GameButton(
                        text = if (selectedSuit != null) "تأیید حکم: ${selectedSuit?.persianName}" else "یک خال را برگزینید",
                        onClick = {
                            val suit = selectedSuit
                            if (suit != null) {
                                onTrumpSelected(suit)
                            }
                        },
                        enabled = selectedSuit != null,
                        colorStyle = GameButtonColor.GOLD,
                        modifier = Modifier.fillMaxWidth(),
                        height = 50.dp,
                        fontSize = 16.sp,
                        testTag = "confirm_trump_button"
                    )
                }
            }
        }
    }
}
