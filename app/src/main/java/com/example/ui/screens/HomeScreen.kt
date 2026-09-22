package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.model.GameMode
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.CircularGoldButton
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.OrnamentBackground
import com.example.ui.components.OrnateBottomNav
import com.example.ui.components.ReconnectionBanner
import com.example.ui.components.ResourcePill
import com.example.ui.theme.BurgundyBottom
import com.example.ui.theme.BurgundyPanel
import com.example.ui.theme.CrimsonGameButtonGradient
import com.example.ui.theme.EmeraldGameButtonGradient
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldMid
import com.example.ui.theme.IvoryTextPrimary
import com.example.ui.theme.IvoryTextSecondary
import com.example.ui.theme.LalezarFamily
import com.example.ui.theme.PurpleGameButtonGradient
import com.example.ui.theme.TitleGold
import com.example.ui.theme.VazirmatnFamily
import com.example.ui.theme.formatCoinsPersian
import com.example.ui.theme.toPersianDigits

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val context = LocalContext.current

    var selectedNavIndex by remember { mutableIntStateOf(0) }
    var showJoinRoomDialog by remember { mutableStateOf(false) }
    var showRoomOptionsDialog by remember { mutableStateOf(false) }
    var joinRoomCodeInput by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            OrnateBottomNav(
                selectedIndex = selectedNavIndex,
                onSelect = { index ->
                    selectedNavIndex = index
                    when (index) {
                        0 -> { /* Home */ }
                        1 -> viewModel.navigateTo(ScreenRoute.Leaderboard)
                        2 -> viewModel.navigateTo(ScreenRoute.Friends)
                        3 -> viewModel.navigateTo(ScreenRoute.ThemeShop)
                        4 -> viewModel.navigateTo(ScreenRoute.Profile)
                    }
                }
            )
        },
        containerColor = BurgundyBottom
    ) { innerPadding ->
        OrnamentBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ReconnectionBanner(isOnline = isOnline)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ==========================================
                    // 1. TOP BAR (Lobby Header)
                    // ==========================================
                    LobbyTopBar(
                        username = user?.username ?: "سلطان حکم",
                        rating = user?.rating ?: 1200,
                        level = user?.level ?: 14,
                        coins = user?.coins ?: 2500,
                        tickets = 15,
                        onAvatarClick = { viewModel.navigateTo(ScreenRoute.Profile) },
                        onAddCoins = { viewModel.navigateTo(ScreenRoute.ThemeShop) },
                        onAddTickets = { Toast.makeText(context, "بخش بلیت‌ها به زودی فعال می‌شود", Toast.LENGTH_SHORT).show() },
                        onNotificationClick = { Toast.makeText(context, "هیچ اعلان جدیدی وجود ندارد", Toast.LENGTH_SHORT).show() },
                        onSettingsClick = { viewModel.navigateTo(ScreenRoute.Settings) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // ==========================================
                    // 2. HERO 3D GOLDEN "حکمچی" WORDMARK
                    // ==========================================
                    HokmChiHeroWordmark()

                    Spacer(modifier = Modifier.height(18.dp))

                    // ==========================================
                    // 3. MAIN MENU BUTTONS (Large Stacked Buttons)
                    // ==========================================
                    Column(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. بازی سریع (رقابتی) - Crimson/Ruby
                        CommercialMenuButton(
                            title = "بازی سریع (رقابتی)",
                            subtitle = "ورود به میز مسابقه رتبه‌ای آنلاین",
                            illustrationEmoji = "🃏",
                            gradient = CrimsonGameButtonGradient,
                            badge = "امتیازی",
                            testTag = "btn_quick_play_ranked",
                            onClick = { viewModel.startQuickMatch(GameMode.RANKED) }
                        )

                        // 2. دورهمی - Royal Purple
                        CommercialMenuButton(
                            title = "دورهمی",
                            subtitle = "بازی تفننی و دوستانه بدون کسر کاپ",
                            illustrationEmoji = "👥",
                            gradient = PurpleGameButtonGradient,
                            badge = "آزاد",
                            testTag = "btn_play_casual",
                            onClick = { viewModel.startQuickMatch(GameMode.CASUAL) }
                        )

                        // 3. بازی اختصاصی - Emerald Green
                        CommercialMenuButton(
                            title = "بازی اختصاصی",
                            subtitle = "ایجاد اتاق خصوصی یا ورود با کد دعوت",
                            illustrationEmoji = "⚔️",
                            gradient = EmeraldGameButtonGradient,
                            badge = "اتاق دوستان",
                            testTag = "btn_private_game_menu",
                            onClick = { showRoomOptionsDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ==========================================
                    // 4. SECONDARY ACTIONS (Circular Gold Buttons)
                    // ==========================================
                    Row(
                        modifier = Modifier.fillMaxWidth(0.92f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularGoldButton(
                            icon = Icons.Default.EmojiEvents,
                            label = "تالار افتخار",
                            onClick = { viewModel.navigateTo(ScreenRoute.Leaderboard) },
                            testTag = "btn_circular_leaderboard"
                        )

                        CircularGoldButton(
                            icon = Icons.Default.Group,
                            label = "دوستان",
                            onClick = { viewModel.navigateTo(ScreenRoute.Friends) },
                            testTag = "btn_circular_friends"
                        )

                        CircularGoldButton(
                            icon = Icons.Default.PersonAdd,
                            label = "دعوت دوستان",
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "بیا با هم حکم‌چی بازی کنیم! نصب رایگان بازی حکم آنلاین: https://hokmchi.ir")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "دعوت دوستان به حکم‌چی"))
                            },
                            testTag = "btn_circular_invite"
                        )

                        CircularGoldButton(
                            icon = Icons.Default.Settings,
                            label = "تنظیمات",
                            onClick = { viewModel.navigateTo(ScreenRoute.Settings) },
                            testTag = "btn_circular_settings"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ==========================================
                    // 5. DAILY REWARD BANNER (Ornate with Flourishes)
                    // ==========================================
                    DailyRewardOrnateBanner(
                        onClaimClick = {
                            viewModel.claimDaily(1)
                            Toast.makeText(context, "سکه روزانه با موفقیت دریافت شد!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // ==========================================
    // DIALOG: Join Room by Code
    // ==========================================
    if (showJoinRoomDialog) {
        Dialog(onDismissRequest = { showJoinRoomDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BurgundyPanel),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, GoldBorderBrush, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF380E0E), BurgundyPanel, Color(0xFF1E0505))
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ورود به اتاق خصوصی",
                            color = TitleGold,
                            fontSize = 20.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "کد دعوت ۶ حرفی اتاق را وارد نمایید:",
                            color = IvoryTextSecondary,
                            fontSize = 12.5.sp,
                            fontFamily = VazirmatnFamily
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = joinRoomCodeInput,
                            onValueChange = { joinRoomCodeInput = it.uppercase() },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GoldLight,
                                unfocusedBorderColor = GoldBorder.copy(alpha = 0.5f),
                                focusedContainerColor = Color(0x33000000),
                                unfocusedContainerColor = Color(0x22000000)
                            ),
                            placeholder = { Text("مثال: HOKM99", color = Color(0x66FFFFFF)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GameButton(
                                text = "انصراف",
                                onClick = { showJoinRoomDialog = false },
                                colorStyle = GameButtonColor.VELVET,
                                modifier = Modifier.weight(1f),
                                height = 48.dp
                            )

                            GameButton(
                                text = "پیوستن",
                                onClick = {
                                    if (joinRoomCodeInput.isNotBlank()) {
                                        showJoinRoomDialog = false
                                        viewModel.joinPrivateRoom(joinRoomCodeInput.trim())
                                    }
                                },
                                colorStyle = GameButtonColor.GOLD,
                                modifier = Modifier.weight(1f),
                                height = 48.dp,
                                testTag = "btn_confirm_join_room"
                            )
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOG: Private Room Options (Create / Join)
    // ==========================================
    if (showRoomOptionsDialog) {
        Dialog(onDismissRequest = { showRoomOptionsDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BurgundyPanel),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, GoldBorderBrush, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF380E0E), BurgundyPanel, Color(0xFF1E0505))
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "بازی اختصاصی با دوستان",
                            color = TitleGold,
                            fontSize = 20.sp,
                            fontFamily = LalezarFamily
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "اتاق دلخواه خود را بسازید یا با کد وارد شوید:",
                            color = IvoryTextSecondary,
                            fontSize = 12.5.sp,
                            fontFamily = VazirmatnFamily
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        GameButton(
                            text = "ساخت اتاق جدید",
                            subtitle = "تولید کد اختصاصی برای ۴ بازیکن",
                            onClick = {
                                showRoomOptionsDialog = false
                                viewModel.createPrivateRoom()
                            },
                            colorStyle = GameButtonColor.GOLD,
                            modifier = Modifier.fillMaxWidth(),
                            height = 54.dp,
                            testTag = "btn_create_new_room_action"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GameButton(
                            text = "ورود با کد اتاق",
                            subtitle = "پیوستن به اتاق دوستان با کد ۶ رقمی",
                            onClick = {
                                showRoomOptionsDialog = false
                                showJoinRoomDialog = true
                            },
                            colorStyle = GameButtonColor.GREEN,
                            modifier = Modifier.fillMaxWidth(),
                            height = 54.dp,
                            testTag = "btn_join_with_code_action"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        GameButton(
                            text = "بستن",
                            onClick = { showRoomOptionsDialog = false },
                            colorStyle = GameButtonColor.VELVET,
                            modifier = Modifier.fillMaxWidth(),
                            height = 42.dp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Lobby Top Bar:
 * - Circular avatar with thick ornate gold frame + subtle glow
 * - Username in gold (Lalezar) + ⭐ rating below it + level badge
 * - Two resource pills (top-left in RTL): dark translucent capsule, gold border, coin/ticket + green "+"
 * - Notification bell + settings gear as gold circular icon buttons
 */
@Composable
private fun LobbyTopBar(
    username: String,
    rating: Int,
    level: Int,
    coins: Long,
    tickets: Int,
    onAvatarClick: () -> Unit,
    onAddCoins: () -> Unit,
    onAddTickets: () -> Unit,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right side (in RTL): Avatar + Username + Rating + Level
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onAvatarClick)
        ) {
            // Circular avatar with thick ornate gold frame + subtle glow
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(10.dp, CircleShape, ambientColor = GoldLight.copy(alpha = 0.5f), spotColor = GoldLight)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF8B1228), Color(0xFF4A001F))
                        )
                    )
                    .border(2.8.dp, GoldBorderBrush, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👑", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = username,
                        color = TitleGold,
                        fontSize = 16.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Level Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldBorderBrush)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "سطح ${level.toPersianDigits()}",
                            color = BurgundyBottom,
                            fontSize = 9.5.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "⭐ ریتینگ: ${rating.toPersianDigits()}",
                    color = IvoryTextSecondary,
                    fontSize = 11.5.sp,
                    fontFamily = VazirmatnFamily
                )
            }
        }

        // Left side (in RTL): Two Resource Pills + Icon Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Coin Pill
            ResourcePill(
                iconText = "🪙",
                valueText = formatCoinsPersian(coins),
                onAddClick = onAddCoins
            )

            // Ticket Pill
            ResourcePill(
                iconText = "🎟️",
                valueText = tickets.toPersianDigits(),
                onAddClick = onAddTickets
            )

            // Notification Bell
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xCC2A0606))
                    .border(1.2.dp, GoldBorderBrush, CircleShape)
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "اعلان‌ها",
                    tint = GoldLight,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Settings Gear
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xCC2A0606))
                    .border(1.2.dp, GoldBorderBrush, CircleShape)
                    .clickable(onClick = onSettingsClick)
                    .testTag("btn_top_settings"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "تنظیمات",
                    tint = GoldLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Original 3D Golden "حکمچی" Wordmark with Warm Glow, Drop Shadow & Crown Accent
 */
@Composable
private fun HokmChiHeroWordmark() {
    val infiniteTransition = rememberInfiniteTransition(label = "crownPulse")
    val crownGlow by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crownScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        // Small Crown Accent with Glow
        Text(
            text = "👑",
            fontSize = 24.sp,
            modifier = Modifier.scale(crownGlow)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(contentAlignment = Alignment.Center) {
            // Deep Warm Shadow Layer
            Text(
                text = "حـُـکـم‌چـی",
                color = Color(0xDD0A0000),
                fontSize = 42.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.offset(x = 3.dp, y = 4.dp)
            )

            // Warm Gold Underlayer
            Text(
                text = "حـُـکـم‌چـی",
                color = GoldDark,
                fontSize = 42.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.offset(x = 1.dp, y = 2.dp)
            )

            // Sharp Metallic Gold Forefront
            Text(
                text = "حـُـکـم‌چـی",
                color = TitleGold,
                fontSize = 42.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal
            )
        }

        // Subtitle flourish
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier.width(32.dp).height(1.5.dp)) {
                drawLine(
                    brush = Brush.horizontalGradient(listOf(Color.Transparent, GoldBorder)),
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5f
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "بازی آنلاین چهار نفره حکم",
                color = IvoryTextSecondary,
                fontSize = 11.5.sp,
                fontFamily = VazirmatnFamily
            )

            Spacer(modifier = Modifier.width(8.dp))

            Canvas(modifier = Modifier.width(32.dp).height(1.5.dp)) {
                drawLine(
                    brush = Brush.horizontalGradient(listOf(GoldBorder, Color.Transparent)),
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5f
                )
            }
        }
    }
}

/**
 * Large Stacked Commercial Menu Button:
 * Height: 68dp, Rounded: 20dp
 * 2.5dp metallic gold border (gradient gold, beveled look)
 * 1dp inner light highlight on top edge
 * Drop shadow below, pressed state = scale 0.97 + darker
 */
@Composable
private fun CommercialMenuButton(
    title: String,
    subtitle: String,
    illustrationEmoji: String,
    gradient: Brush,
    badge: String,
    testTag: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(90),
        label = "menuBtnScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .height(68.dp)
            .shadow(
                elevation = if (isPressed) 4.dp else 12.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x88000000),
                spotColor = GoldMid.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .border(
                width = 2.5.dp,
                brush = GoldBorderBrush,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1dp inner light highlight on top edge
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.White.copy(alpha = 0.35f),
                start = Offset(16f, 2f),
                end = Offset(size.width - 16f, 2f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Illustration Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0x33000000))
                        .border(1.2.dp, GoldBorder.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = illustrationEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = LalezarFamily,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = subtitle,
                        color = IvoryTextPrimary.copy(alpha = 0.8f),
                        fontSize = 11.5.sp,
                        fontFamily = VazirmatnFamily
                    )
                }
            }

            // Right Badge (RTL)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x44000000))
                    .border(1.2.dp, GoldBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badge,
                    color = GoldLight,
                    fontSize = 11.5.sp,
                    fontFamily = VazirmatnFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Ornate Horizontal Daily Reward Banner with Gold Flourish Decorations
 */
@Composable
private fun DailyRewardOrnateBanner(
    onClaimClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .shadow(10.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF380E0E), Color(0xFF240606), Color(0xFF380E0E))
                )
            )
            .border(1.5.dp, GoldBorderBrush, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎁", fontSize = 26.sp)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "جایزه و سکه روزانه",
                        color = TitleGold,
                        fontSize = 14.sp,
                        fontFamily = LalezarFamily
                    )
                    Text(
                        text = "هر ۲۴ ساعت ۱۰۰۰ سکه رایگان",
                        color = IvoryTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = VazirmatnFamily
                    )
                }
            }

            GameButton(
                text = "سکه روزانه",
                onClick = onClaimClick,
                colorStyle = GameButtonColor.GOLD,
                height = 38.dp,
                fontSize = 12.sp,
                icon = Icons.Default.CardGiftcard,
                testTag = "btn_claim_daily_gift"
            )
        }
    }
}
