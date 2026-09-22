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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.GoldFramePanel
import com.example.ui.components.OrnamentBackground
import com.example.ui.components.ResourcePill
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
import com.example.ui.theme.formatCoinsPersian
import com.example.ui.theme.toPersianDigits

/**
 * Profile Screen:
 * - Big avatar with ornate gold frame
 * - Stats in dark cards with gold trim
 * - Level progress bar with gold fill
 * - All numbers in Persian digits
 */
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val stats = user?.stats

    val gamesPlayed = stats?.gamesPlayed ?: 24
    val wins = stats?.wins ?: 16
    val losses = stats?.losses ?: 8
    val winRate = if (gamesPlayed > 0) (wins * 100) / gamesPlayed else 0

    OrnamentBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenRoute.Home) },
                        modifier = Modifier.testTag("btn_back_profile")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "شناسنامه و افتخارات",
                        color = TitleGold,
                        fontSize = 20.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )
                }

                ResourcePill(
                    iconText = "🪙",
                    valueText = formatCoinsPersian(user?.coins ?: 1500)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main User Royal Card
                GoldFramePanel(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Big Avatar with Thick Ornate Gold Frame
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .shadow(16.dp, CircleShape, spotColor = GoldLight)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF8B1228), Color(0xFF4A001F))
                                        )
                                    )
                                    .border(3.2.dp, GoldBorderBrush, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.size(56.dp)
                                )
                            }

                            // Hakem Crown Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-10).dp)
                                    .shadow(6.dp, RoundedCornerShape(10.dp))
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GoldLinearGradient)
                                    .border(1.2.dp, Color(0xFFFFFDE7), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "👑 حاکم اعظم",
                                    color = BurgundyBottom,
                                    fontSize = 10.sp,
                                    fontFamily = LalezarFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user?.username ?: "سلطان حکم",
                            color = TitleGold,
                            fontSize = 22.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Rating & League pill
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⭐ ریتینگ: ${(user?.rating ?: 1250).toPersianDigits()}",
                                color = GoldLight,
                                fontSize = 13.sp,
                                fontFamily = VazirmatnFamily,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• لیگ ${user?.league?.persianName ?: "طلایی"}",
                                color = Color(user?.league?.colorHex ?: 0xFFFFD700),
                                fontSize = 12.sp,
                                fontFamily = VazirmatnFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Level & XP Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "سطح ${(user?.level ?: 14).toPersianDigits()}",
                                color = GoldLight,
                                fontSize = 12.sp,
                                fontFamily = LalezarFamily,
                                fontWeight = FontWeight.Normal
                            )
                            Text(
                                text = "${(user?.xp ?: 680).toPersianDigits()} / ۱,۰۰۰ امتیاز",
                                color = IvoryTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = VazirmatnFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { 0.68f },
                            color = GoldLight,
                            trackColor = Color(0x33000000),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(0.8.dp, GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Section Header
                Text(
                    text = "کارنامه و آمار نبردها",
                    color = TitleGold,
                    fontSize = 16.sp,
                    fontFamily = LalezarFamily,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Played, Wins, Losses
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DarkStatCard(
                        title = "تعداد بازی‌ها",
                        value = gamesPlayed.toPersianDigits(),
                        valueColor = Color(0xFF90CAF9),
                        modifier = Modifier.weight(1f)
                    )
                    DarkStatCard(
                        title = "پیروزی‌ها",
                        value = wins.toPersianDigits(),
                        valueColor = Color(0xFFA5D6A7),
                        modifier = Modifier.weight(1f)
                    )
                    DarkStatCard(
                        title = "شکست‌ها",
                        value = losses.toPersianDigits(),
                        valueColor = Color(0xFFEF9A9A),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Win Rate, Streaks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DarkStatCard(
                        title = "درصد برد",
                        value = "${winRate.toPersianDigits()}٪",
                        valueColor = GoldLight,
                        modifier = Modifier.weight(1f)
                    )
                    DarkStatCard(
                        title = "برد متوالی",
                        value = "${(stats?.currentStreak ?: 4).toPersianDigits()} 🔥",
                        valueColor = Color(0xFFFFCC80),
                        modifier = Modifier.weight(1f)
                    )
                    DarkStatCard(
                        title = "بهترین رکورد",
                        value = "${(stats?.bestStreak ?: 8).toPersianDigits()} ⚡",
                        valueColor = Color(0xFFCE93D8),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 3: Kot, Hakem Kot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DarkStatCard(
                        title = "کوت کرده",
                        value = (stats?.kotsGiven ?: 6).toPersianDigits(),
                        valueColor = GoldLight,
                        modifier = Modifier.weight(1f)
                    )
                    DarkStatCard(
                        title = "حاکم‌کوت کرده",
                        value = (stats?.hakemKotsGiven ?: 3).toPersianDigits(),
                        valueColor = Color(0xFFFF5252),
                        modifier = Modifier.weight(1f)
                    )
                    DarkStatCard(
                        title = "امتیاز فصل",
                        value = ((user?.rating ?: 1200) * 2).toPersianDigits(),
                        valueColor = Color(0xFFFFE082),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Achievements Section
                Text(
                    text = "نشان‌ها و مدال‌ها",
                    color = TitleGold,
                    fontSize = 16.sp,
                    fontFamily = LalezarFamily,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DarkAchievementBadge(
                        title = "حاکم مطلق",
                        desc = "۵ برد با حاکم‌کوت",
                        icon = Icons.Default.EmojiEvents,
                        isUnlocked = true,
                        modifier = Modifier.weight(1f)
                    )
                    DarkAchievementBadge(
                        title = "آس پیک",
                        desc = "۱۰ دست بدون باخت",
                        icon = Icons.Default.LocalFireDepartment,
                        isUnlocked = true,
                        modifier = Modifier.weight(1f)
                    )
                    DarkAchievementBadge(
                        title = "سلطان لیگ",
                        desc = "رسیدن به گرندمستر",
                        icon = Icons.Default.Stars,
                        isUnlocked = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Match History Section
                Text(
                    text = "تاریخچه بازی‌های اخیر",
                    color = TitleGold,
                    fontSize = 16.sp,
                    fontFamily = LalezarFamily,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                DarkMatchHistoryRow(
                    opponent = "سهراب_حاکم و آرش",
                    score = "۷ - ۳",
                    isWin = true,
                    date = "امروز • ۱۲:۳۰"
                )
                Spacer(modifier = Modifier.height(8.dp))
                DarkMatchHistoryRow(
                    opponent = "کوروش و نگین",
                    score = "۵ - ۷",
                    isWin = false,
                    date = "دیروز • ۱۹:۱۵"
                )
                Spacer(modifier = Modifier.height(8.dp))
                DarkMatchHistoryRow(
                    opponent = "شاه_پاسور و مهمان",
                    score = "۷ - ۰ (کوت)",
                    isWin = true,
                    date = "۲ روز پیش"
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DarkStatCard(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A0606), Color(0xFF160303))
                )
            )
            .border(1.2.dp, GoldBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = IvoryTextSecondary,
                fontSize = 10.5.sp,
                fontFamily = VazirmatnFamily,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 16.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun DarkAchievementBadge(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A0606), Color(0xFF160303))
                )
            )
            .border(
                1.2.dp,
                if (isUnlocked) GoldBorderBrush else Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x11FFFFFF))),
                RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) GoldLinearGradient else Brush.verticalGradient(listOf(Color(0xFF333333), Color(0xFF1A1A1A)))
                    )
                    .border(1.dp, if (isUnlocked) GoldLight else Color(0x44FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isUnlocked) BurgundyBottom else Color(0x66FFFFFF),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                color = if (isUnlocked) TitleGold else Color(0x88FFFFFF),
                fontSize = 12.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = desc,
                color = IvoryTextSecondary,
                fontSize = 9.sp,
                fontFamily = VazirmatnFamily,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DarkMatchHistoryRow(
    opponent: String,
    score: String,
    isWin: Boolean,
    date: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BurgundyPanel),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .border(1.2.dp, GoldBorder.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2A0606), Color(0xFF1E0404))
                    )
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = opponent,
                        color = IvoryTextPrimary,
                        fontSize = 13.5.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = date,
                        color = IvoryTextSecondary,
                        fontSize = 10.5.sp,
                        fontFamily = VazirmatnFamily
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = score.toPersianDigits(),
                        color = TitleGold,
                        fontSize = 14.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .background(
                                if (isWin) Color(0xFF1B5E20) else Color(0xFF8B0000),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (isWin) Color(0xFF4CAF50) else Color(0xFFE53935),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isWin) "پیروزی" else "شکست",
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontFamily = VazirmatnFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
