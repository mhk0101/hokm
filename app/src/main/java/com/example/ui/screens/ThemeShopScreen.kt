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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.CardBack
import com.example.core.model.GameTheme
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.CardBackView
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.OrnamentBackground
import com.example.ui.components.ResourcePill
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
import com.example.ui.theme.formatCoinsPersian
import com.example.ui.theme.toPersianDigits

/**
 * Royal Persian Theme & Card Shop Screen:
 * - Categorized items (tables, card backs, avatars, frames, coins) in gold-framed cards
 * - Price tags with Persian digits
 * - Equip / Buy action buttons
 */
@Composable
fun ThemeShopScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val settings by viewModel.settings.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val categories = listOf("میزهای بازی", "پشت کارت‌ها", "آواتارها", "قاب‌های حاکم", "بسته‌های سکه")

    OrnamentBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header with Coin Balance
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
                        modifier = Modifier.testTag("btn_back_shop")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = GoldLight
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "دکان و بازارچه سلطنتی",
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

            // Shop Categories Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = BurgundyPanel,
                contentColor = GoldLight,
                edgePadding = 6.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.2.dp, GoldBorderBrush, RoundedCornerShape(14.dp))
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.5.sp,
                                fontFamily = if (selectedTab == index) LalezarFamily else VazirmatnFamily,
                                color = if (selectedTab == index) TitleGold else IvoryTextSecondary
                            )
                        },
                        modifier = Modifier.testTag("tab_shop_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Table Themes List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(GameTheme.ALL_THEMES) { theme ->
                            val isActive = settings.activeThemeId == theme.id

                            ShopItemCard(
                                title = theme.name,
                                subtitle = if (theme.priceCoins == 0L) "رایگان برای همه" else "${theme.priceCoins.toInt().toPersianDigits()} سکه",
                                isActive = isActive,
                                isOwned = theme.priceCoins == 0L || isActive,
                                price = theme.priceCoins.toInt(),
                                previewContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .shadow(6.dp, CircleShape)
                                            .clip(CircleShape)
                                            .background(Color(theme.accentColor))
                                            .border(2.dp, GoldBorderBrush, CircleShape)
                                    )
                                },
                                onEquipOrBuy = {
                                    if (theme.priceCoins == 0L || (user?.coins ?: 0) >= theme.priceCoins) {
                                        viewModel.selectTheme(theme.id)
                                    }
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // Card Backs List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(CardBack.ALL_CARD_BACKS) { cardBack ->
                            val isActive = settings.activeCardBackId == cardBack.id

                            ShopItemCard(
                                title = cardBack.name,
                                subtitle = if (cardBack.priceCoins == 0L) "رایگان برای همه" else "${cardBack.priceCoins.toInt().toPersianDigits()} سکه",
                                isActive = isActive,
                                isOwned = cardBack.priceCoins == 0L || isActive,
                                price = cardBack.priceCoins.toInt(),
                                previewContent = {
                                    CardBackView(
                                        style = cardBack.style,
                                        width = 38.dp,
                                        height = 54.dp
                                    )
                                },
                                onEquipOrBuy = {
                                    if (cardBack.priceCoins == 0L || (user?.coins ?: 0) >= cardBack.priceCoins) {
                                        viewModel.selectCardBack(cardBack.id)
                                    }
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Avatars
                    val sampleAvatars = listOf(
                        Triple("شاهزاده پارسی", 0, Color(0xFFC2185B)),
                        Triple("سردار ساسانی", 500, Color(0xFF1976D2)),
                        Triple("حاکم اعظم", 1000, Color(0xFFFFB300)),
                        Triple("وزیر دربار", 1500, Color(0xFF7B1FA2))
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(sampleAvatars) { (name, price, color) ->
                            ShopItemCard(
                                title = name,
                                subtitle = if (price == 0) "پیش‌فرض" else "${price.toPersianDigits()} سکه",
                                isActive = price == 0,
                                isOwned = price == 0,
                                price = price,
                                previewContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(2.dp, GoldBorderBrush, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = name,
                                            tint = Color.White,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                onEquipOrBuy = { }
                            )
                        }
                    }
                }
                3 -> {
                    // Frames
                    val sampleFrames = listOf(
                        Triple("قاب زرین سلطنتی", 0, GoldLight),
                        Triple("قاب یاقوت سرخ", 800, Color(0xFFE91E63)),
                        Triple("قاب فیروزه نیشابور", 1200, Color(0xFF00E5FF)),
                        Triple("قاب کهکشانی", 2000, Color(0xFF7C4DFF))
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(sampleFrames) { (name, price, frameColor) ->
                            ShopItemCard(
                                title = name,
                                subtitle = if (price == 0) "پیش‌فرض" else "${price.toPersianDigits()} سکه",
                                isActive = price == 0,
                                isOwned = price == 0,
                                price = price,
                                previewContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2A0606))
                                            .border(3.dp, frameColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "👑", fontSize = 18.sp)
                                    }
                                },
                                onEquipOrBuy = { }
                            )
                        }
                    }
                }
                4 -> {
                    // Coin Packs
                    val coinPacks = listOf(
                        Triple("کیسه زر کوچک (۱,۰۰۰ سکه)", 1000, "۱۰,۰۰۰ تومان"),
                        Triple("کیسه زر سلطنتی (۵,۰۰۰ سکه)", 5000, "۴۵,۰۰۰ تومان"),
                        Triple("صندوقچه جواهرات (۱۵,۰۰۰ سکه)", 15000, "۱۲۰,۰۰۰ تومان"),
                        Triple("خزانه دربار (۵۰,۰۰۰ سکه)", 50000, "۳۵۰,۰۰۰ تومان")
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(coinPacks) { (name, coins, priceToman) ->
                            ShopItemCard(
                                title = name,
                                subtitle = priceToman.toPersianDigits(),
                                isActive = false,
                                isOwned = false,
                                price = coins,
                                previewContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(GoldLinearGradient)
                                            .border(2.dp, Color(0xFFFFFDE7), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🪙", fontSize = 22.sp)
                                    }
                                },
                                onEquipOrBuy = { }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Gold-Framed Shop Item Card with Price Tag & Persian Typography
 */
@Composable
private fun ShopItemCard(
    title: String,
    subtitle: String,
    isActive: Boolean,
    isOwned: Boolean,
    price: Int,
    previewContent: @Composable () -> Unit,
    onEquipOrBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BurgundyPanel),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .border(
                width = if (isActive) 2.2.dp else 1.2.dp,
                brush = if (isActive) GoldBorderBrush else Brush.linearGradient(
                    listOf(GoldBorder.copy(alpha = 0.45f), GoldBorder.copy(alpha = 0.15f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2A0606), Color(0xFF1E0404))
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    previewContent()

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = title,
                            color = IvoryTextPrimary,
                            fontSize = 14.5.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            color = TitleGold,
                            fontSize = 11.5.sp,
                            fontFamily = VazirmatnFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isActive) {
                    Box(
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldLinearGradient)
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "مجهز شده ✓",
                            color = BurgundyBottom,
                            fontSize = 11.5.sp,
                            fontFamily = LalezarFamily,
                            fontWeight = FontWeight.Normal
                        )
                    }
                } else {
                    GameButton(
                        text = if (isOwned || price == 0) "انتخاب" else "خرید",
                        onClick = onEquipOrBuy,
                        colorStyle = if (isOwned || price == 0) GameButtonColor.GREEN else GameButtonColor.GOLD,
                        height = 38.dp,
                        fontSize = 12.sp,
                        testTag = "btn_shop_${title}"
                    )
                }
            }
        }
    }
}
