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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.GamePanel
import com.example.ui.components.PersianBackground
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldLight
import com.example.ui.theme.VelvetCardGradient
import com.example.ui.theme.VelvetNight

@Composable
fun GameResultScreen(
    gameId: String,
    viewModel: MainViewModel
) {
    val state by viewModel.activeGame.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val mySlot = state?.players?.find { it.userId == currentUser?.userId }
    val myTeam = mySlot?.team ?: 0
    val winnerTeam = state?.winnerTeam ?: 0
    val didIWin = myTeam == winnerTeam

    PersianBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Trophy / Result Crest
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            if (didIWin) GoldLinearGradient
                            else Brush.verticalGradient(
                                listOf(Color(0xFFE53935), Color(0xFFB71C1C))
                            )
                        )
                        .border(
                            3.dp,
                            if (didIWin) Color(0xFFFFF9C4) else Color(0xFFFFCDD2),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Result",
                        tint = if (didIWin) VelvetNight else Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (didIWin) "🏆 پیروزی شکوهمند تیم شما!" else "💔 شکست در این نبرد",
                    color = if (didIWin) PersianGoldLight else Color(0xFFFF8A80),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "کارنامه و امتیازات ثبت شده در این مسابقه",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Score & Rewards Panel
                GamePanel(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Match Score (e.g. 7 - 4)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "تیم ما (الف)",
                                    color = PersianGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${state?.teamAPoints ?: 7}",
                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Text(
                                "—",
                                color = PersianGold.copy(alpha = 0.6f),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "تیم حریف (ب)",
                                    color = Color(0xFFFF8A80),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${state?.teamBPoints ?: 3}",
                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Rewards Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            RewardChip(
                                label = "امتیاز رتبه‌بندی",
                                value = if (didIWin) "+24 ⭐" else "-18 ⭐",
                                color = if (didIWin) Color(0xFF00E676) else Color(0xFFFF5252)
                            )
                            RewardChip(
                                label = "تجربه کاربر",
                                value = if (didIWin) "+150 XP" else "+40 XP",
                                color = PersianGold
                            )
                            RewardChip(
                                label = "پاداش سکه",
                                value = if (didIWin) "+50 🪙" else "+15 🪙",
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Replay Button
                GameButton(
                    text = "مشاهده مجدد دست‌های بازی (Replay)",
                    onClick = { viewModel.navigateTo(ScreenRoute.Replay(gameId)) },
                    icon = Icons.Default.Replay,
                    colorStyle = GameButtonColor.VELVET,
                    height = 48.dp,
                    fontSize = 13.5.sp,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_view_replay"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Return to Home Button
                GameButton(
                    text = "بازگشت به لابی اصلی",
                    onClick = { viewModel.navigateTo(ScreenRoute.Home) },
                    icon = Icons.Default.Home,
                    colorStyle = GameButtonColor.GOLD,
                    height = 54.dp,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_return_home"
                )
            }
        }
    }
}

@Composable
private fun RewardChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}
