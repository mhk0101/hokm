package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.GamePanel
import com.example.ui.components.PersianBackground
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldLight
import com.example.ui.theme.VelvetCardGradient
import com.example.ui.theme.VelvetNight
import com.example.ui.theme.VelvetSurface

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    var showRulesDialog by remember { mutableStateOf(false) }

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
                    modifier = Modifier.testTag("btn_back_settings")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "تنظیمات و سفارشی‌سازی",
                    color = PersianGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Sound & Feedback Card
                Text(
                    text = "صدا و بازخورد حسی:",
                    color = PersianGoldLight,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                GamePanel(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        SettingToggleRow(
                            title = "جلوه‌های صوتی بازی",
                            subtitle = "صدای پخش کارت، کوت و اعلام حکم",
                            isChecked = settings.soundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) }
                        )

                        SettingToggleRow(
                            title = "لرزش هپتیک (ویبره)",
                            subtitle = "ویبره ظریف هنگام لمس و انداختن کارت",
                            isChecked = settings.vibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration(it) }
                        )

                        SettingToggleRow(
                            title = "انیمیشن‌های فیزیکی کارت‌ها",
                            subtitle = "حرکت سه‌بعدی و جلوه کشیدن روی نمد میز",
                            isChecked = settings.animationsEnabled,
                            onCheckedChange = { viewModel.toggleAnimations(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Rules & Support
                Text(
                    text = "راهنما و قوانین:",
                    color = PersianGoldLight,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                GamePanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showRulesDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "قوانین",
                                tint = PersianGold,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "آموزش کامل و قوانین بازی حکم",
                                    color = Color.White,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "نحوه تعیین خال، کوت، حاکم‌کوت و استراتژی‌های بازی",
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Moderation Panel Link
                GamePanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(ScreenRoute.Admin) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "مدیریت",
                            tint = Color(0xFF00ADB5),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "پنل نظارت و گزارش‌های کاربران",
                                color = Color.White,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "بررسی تخلفات و گزارش رفتار غیرورزشی",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Logout Button
                GameButton(
                    text = "خروج از حساب کاربری",
                    onClick = { viewModel.signOut() },
                    colorStyle = GameButtonColor.CRIMSON,
                    icon = Icons.Default.Logout,
                    height = 50.dp,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_logout"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "سلطان حکم • نسخه ۱.۰.۰",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Rules Dialog
    if (showRulesDialog) {
        Dialog(onDismissRequest = { showRulesDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VelvetNight),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .border(2.dp, GoldBorderBrush, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VelvetCardGradient)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "📖 آموزش و قوانین رسمی حکم ۴ نفره",
                            color = PersianGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "• بازی با ۴ بازیکن در دو تیم روبه‌رو (یار) انجام می‌پذیرد.\n\n" +
                                    "• حاکم با دریافت ۵ کارت نخست، خال حکم دست را معین می‌کند.\n\n" +
                                    "• اولین نفری که کارت بازی می‌کند، خال زمینه را تعیین می‌کند و همه بازیکنان در صورت داشتن آن خال موظف به بازی کردن همان خال هستند (تبعیت از خال زمینه).\n\n" +
                                    "• هر تیمی که زودتر ۷ دست بگیرد، برنده آن دور مسابقه می‌شود.\n\n" +
                                    "• کوت (Kot): اگر تیمی موفق شود ۷ دست متوالی بدون امتیاز به حریف ببرد، ۲ امتیاز کسب می‌کند.\n\n" +
                                    "• حاکم‌کوت: اگر تیم حریف، حاکم را کوت کند، ۳ امتیاز ویژه محاسبه خواهد شد.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        GameButton(
                            text = "متوجه شدم",
                            onClick = { showRulesDialog = false },
                            colorStyle = GameButtonColor.GOLD,
                            height = 44.dp,
                            fontSize = 13.5.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VelvetNight,
                checkedTrackColor = PersianGold,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0x44FFFFFF)
            )
        )
    }
}
