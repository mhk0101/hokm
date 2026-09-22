package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.Card
import com.example.core.model.Rank
import com.example.core.model.Suit
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.CardView
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.GamePanel
import com.example.ui.components.PersianBackground
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldLight

@Composable
fun ReplayScreen(
    gameId: String,
    viewModel: MainViewModel
) {
    val state by viewModel.activeGame.collectAsState()
    var currentStep by remember { mutableIntStateOf(1) }
    val maxSteps = 13

    // Sample playback trick cards for review
    val sampleTrickCards = listOf(
        Pair("بازیکن ۱ (شما)", Card(Suit.SPADES, Rank.ACE)),
        Pair("بازیکن ۲", Card(Suit.SPADES, Rank.KING)),
        Pair("بازیکن ۳ (یار)", Card(Suit.SPADES, Rank.QUEEN)),
        Pair("بازیکن ۴", Card(Suit.SPADES, Rank.JACK))
    )

    PersianBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.Home) },
                    modifier = Modifier.testTag("btn_close_replay")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = PersianGold
                    )
                }

                Text(
                    text = "بازبینی دست $currentStep از $maxSteps",
                    color = PersianGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trick Display Panel
            GamePanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "برگ‌های بازی شده در این دور بازی:",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        sampleTrickCards.forEach { (player, card) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CardView(
                                    card = card,
                                    width = 68.dp,
                                    height = 96.dp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = player,
                                    color = PersianGoldLight,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1B5E20), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF66BB6A), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "👑 برنده دست: تیم ما (شما)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Replay Controls (Previous / Next)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameButton(
                    text = "دست قبلی",
                    onClick = { if (currentStep > 1) currentStep-- },
                    enabled = currentStep > 1,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    colorStyle = GameButtonColor.VELVET,
                    height = 42.dp,
                    fontSize = 12.sp
                )

                Text(
                    text = "$currentStep / $maxSteps",
                    color = PersianGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                GameButton(
                    text = "دست بعدی",
                    onClick = { if (currentStep < maxSteps) currentStep++ },
                    enabled = currentStep < maxSteps,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    colorStyle = GameButtonColor.GOLD,
                    height = 42.dp,
                    fontSize = 12.sp
                )
            }
        }
    }
}
