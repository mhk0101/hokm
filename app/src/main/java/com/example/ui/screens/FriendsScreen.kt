package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.FriendUser
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.PersianBackground
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldLight
import com.example.ui.theme.VelvetCardGradient
import com.example.ui.theme.VelvetNight

@Composable
fun FriendsScreen(viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val sampleFriends = listOf(
        FriendUser("f1", "سهراب_حاکم", "avatar_1", 2420, isOnline = true, inGame = false),
        FriendUser("f2", "کوروش_بزرگ", "avatar_2", 2315, isOnline = true, inGame = true),
        FriendUser("f3", "آرش_کمانگیر", "avatar_3", 2040, isOnline = false, inGame = false),
        FriendUser("f4", "شیرین_بانو", "avatar_4", 1980, isOnline = true, inGame = false)
    )

    PersianBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.Home) },
                    modifier = Modifier.testTag("btn_back_friends")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "هم‌رزمان و دوستان",
                    color = PersianGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "جستجوی نام کاربری برای دعوت یا افزودن...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجو",
                        tint = PersianGold
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PersianGold,
                    unfocusedBorderColor = PersianGold.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = PersianGold
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "لیست دوستان (${sampleFriends.size}):",
                color = PersianGoldLight,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sampleFriends) { friend ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = VelvetNight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                PersianGold.copy(alpha = 0.25f),
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VelvetCardGradient)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .shadow(4.dp, CircleShape)
                                            .clip(CircleShape)
                                            .background(Color(0xFF381F54))
                                            .border(1.5.dp, GoldBorderBrush, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Friend",
                                            tint = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = friend.username,
                                            color = Color.White,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (friend.isOnline) Color(0xFF00E676)
                                                        else Color(0xFF9E9E9E)
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = if (friend.inGame) "در حال بازی ⚔️"
                                                else if (friend.isOnline) "آماده بازی ✓"
                                                else "آفلاین",
                                                color = Color.White.copy(alpha = 0.65f),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                GameButton(
                                    text = "دعوت به بازی",
                                    onClick = {
                                        viewModel.createPrivateRoom()
                                        Toast.makeText(
                                            context,
                                            "دعوت به اتاق برای ${friend.username} ارسال شد",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    enabled = friend.isOnline && !friend.inGame,
                                    icon = Icons.Default.MeetingRoom,
                                    colorStyle = GameButtonColor.GOLD,
                                    height = 36.dp,
                                    fontSize = 11.5.sp,
                                    testTag = "btn_invite_${friend.userId}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
