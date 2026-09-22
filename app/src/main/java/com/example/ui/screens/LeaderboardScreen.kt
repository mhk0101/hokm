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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.LeaderboardEntry
import com.example.core.model.League
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.OrnamentBackground
import com.example.ui.theme.BurgundyBottom
import com.example.ui.theme.BurgundyPanel
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.IvoryTextPrimary
import com.example.ui.theme.IvoryTextSecondary
import com.example.ui.theme.LalezarFamily
import com.example.ui.theme.TitleGold
import com.example.ui.theme.VazirmatnFamily
import com.example.ui.theme.toPersianDigits

/**
 * Leaderboard Screen:
 * - Top 3 as gold/silver/bronze pedestals with crowns
 * - List below with rank badges
 * - Dark cards with gold trim
 * - Persian numbers throughout
 */
@Composable
fun LeaderboardScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(3) }
    val leaderboard by viewModel.leaderboard.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLeaderboard()
    }

    val displayList = if (leaderboard.isNotEmpty()) leaderboard else listOf(
        LeaderboardEntry(1, "u1", "سهراب_حاکم", 2420, League.GRAND_MASTER, 412),
        LeaderboardEntry(2, "u2", "کوروش_بزرگ", 2315, League.MASTER, 380),
        LeaderboardEntry(3, "u3", "شاه_پاسور", 2190, League.DIAMOND, 340),
        LeaderboardEntry(4, "u4", "آرش_کمانگیر", 2040, League.PLATINUM, 290),
        LeaderboardEntry(5, "u5", "امیر_حکم‌چی", 1920, League.GOLD, 250),
        LeaderboardEntry(6, "u6", "نگین_پیک", 1780, League.SILVER, 190),
        LeaderboardEntry(7, "u7", "رستم_پاسور", 1650, League.SILVER, 160),
        LeaderboardEntry(8, "u8", "فرزاد_دل‌دار", 1520, League.BRONZE, 130)
    )

    val top1 = displayList.getOrNull(0)
    val top2 = displayList.getOrNull(1)
    val top3 = displayList.getOrNull(2)
    val rest = if (displayList.size > 3) displayList.subList(3, displayList.size) else emptyList()

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.Home) },
                    modifier = Modifier.testTag("btn_back_leaderboard")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = GoldLight
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "🏆 تالار افتخار و رده‌بندی",
                    color = TitleGold,
                    fontSize = 20.sp,
                    fontFamily = LalezarFamily,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timeframe Tabs with Gold Borders
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BurgundyPanel,
                contentColor = GoldLight,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.2.dp, GoldBorderBrush, RoundedCornerShape(14.dp))
            ) {
                listOf("روزانه", "هفتگی", "ماهانه", "فصل جاری").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontFamily = if (selectedTab == index) LalezarFamily else VazirmatnFamily,
                                color = if (selectedTab == index) TitleGold else IvoryTextSecondary
                            )
                        },
                        modifier = Modifier.testTag("tab_leaderboard_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TOP 3 PODIUM SECTION (2nd on Right in RTL, 1st in Center elevated, 3rd on Left)
            if (top1 != null && top2 != null && top3 != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place (Silver)
                    PodiumPlayerCard(
                        entry = top2,
                        rankColor = Color(0xFFE0E0E0),
                        podiumHeight = 90.dp,
                        avatarSize = 54.dp,
                        crownEmoji = "🥈",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // 1st Place (Gold, Tallest)
                    PodiumPlayerCard(
                        entry = top1,
                        rankColor = GoldLight,
                        podiumHeight = 118.dp,
                        avatarSize = 64.dp,
                        crownEmoji = "👑",
                        modifier = Modifier.weight(1.15f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // 3rd Place (Bronze)
                    PodiumPlayerCard(
                        entry = top3,
                        rankColor = Color(0xFFCD7F32),
                        podiumHeight = 76.dp,
                        avatarSize = 50.dp,
                        crownEmoji = "🥉",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // REST OF PLAYERS (Rank 4+)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rest) { entry ->
                    LeaderboardRow(entry = entry)
                }
            }
        }
    }
}

/**
 * Podium Player Card for 1st, 2nd, and 3rd rank with royal pedestals
 */
@Composable
private fun PodiumPlayerCard(
    entry: LeaderboardEntry,
    rankColor: Color,
    podiumHeight: Dp,
    avatarSize: Dp,
    crownEmoji: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = crownEmoji, fontSize = 22.sp)

        // Avatar
        Box(
            modifier = Modifier
                .size(avatarSize)
                .shadow(10.dp, CircleShape, spotColor = rankColor)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF380E0E), Color(0xFF1E0505))
                    )
                )
                .border(2.5.dp, rankColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = entry.username,
                tint = Color.White,
                modifier = Modifier.size(avatarSize * 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.username,
            color = IvoryTextPrimary,
            fontSize = 11.5.sp,
            fontFamily = LalezarFamily,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "⭐ ${entry.rating.toPersianDigits()}",
            color = GoldLight,
            fontSize = 10.5.sp,
            fontFamily = VazirmatnFamily,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal Box with Gold/Silver/Bronze Trim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(rankColor.copy(alpha = 0.35f), Color(0xFF1E0505))
                    )
                )
                .border(
                    width = 1.4.dp,
                    color = rankColor.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = entry.rank.toPersianDigits(),
                    color = rankColor,
                    fontSize = 26.sp,
                    fontFamily = LalezarFamily,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "${entry.wins.toPersianDigits()} برد",
                    color = IvoryTextSecondary,
                    fontSize = 10.sp,
                    fontFamily = VazirmatnFamily
                )
            }
        }
    }
}

/**
 * Dark Leaderboard Row with Rank Badge & Gold Borders
 */
@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
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
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rank Badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x55000000))
                            .border(1.2.dp, GoldBorderBrush, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = entry.rank.toPersianDigits(),
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(Color(0xFF8B1228), Color(0xFF4A001F)))
                            )
                            .border(1.dp, GoldBorder.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = entry.username,
                            color = IvoryTextPrimary,
                            fontSize = 13.5.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "${entry.league.persianName} • ${entry.wins.toPersianDigits()} پیروزی",
                            color = IvoryTextSecondary,
                            fontSize = 10.5.sp,
                            fontFamily = VazirmatnFamily
                        )
                    }
                }

                // Rating Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x44000000))
                        .border(1.dp, GoldBorderBrush, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⭐ ${entry.rating.toPersianDigits()}",
                        color = TitleGold,
                        fontSize = 12.5.sp,
                        fontFamily = VazirmatnFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
