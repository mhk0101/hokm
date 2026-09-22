package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MainViewModel
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
fun AuthScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register, 2: Guest

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    PersianBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Royal Game Emblem
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF3B1527), Color(0xFF1E0A12))
                            )
                        )
                        .border(2.dp, GoldBorderBrush, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_hokmchi_logo),
                        contentDescription = "HokmChi Logo",
                        modifier = Modifier.size(70.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "سلطان حکم",
                    color = PersianGold,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "ورود به دربار آنلاین بازی حکم ایرانی",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.5.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Royal Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = VelvetSurface,
                    contentColor = PersianGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.2.dp, PersianGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "ورود",
                                fontWeight = if (selectedTab == 0) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_login")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "عضویت جدید",
                                fontWeight = if (selectedTab == 1) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_register")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "مهمان سریع",
                                fontWeight = if (selectedTab == 2) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_guest")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                GamePanel(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        when (selectedTab) {
                            0 -> {
                                // Login Form
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("ایمیل ثبت شده") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = textFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_email")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("رمز عبور") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = textFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_password")
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                GameButton(
                                    text = "ورود به حساب کاربری",
                                    onClick = { viewModel.login(email, password) },
                                    colorStyle = GameButtonColor.GOLD,
                                    height = 48.dp,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "btn_login_submit"
                                )
                            }
                            1 -> {
                                // Register Form
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("نام نمایشی در بازی (حداقل ۳ حرف)") },
                                    colors = textFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_reg_username")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("ایمیل معتبر") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = textFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_reg_email")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("رمز عبور (حداقل ۶ نویسه)") },
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = textFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_reg_password")
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                GameButton(
                                    text = "ایجاد حساب و ورود",
                                    onClick = { viewModel.register(email, password, username) },
                                    colorStyle = GameButtonColor.GOLD,
                                    height = 48.dp,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "btn_register_submit"
                                )
                            }
                            2 -> {
                                // Guest Play Form
                                Text(
                                    text = "ورود بدون نیاز به ثبت ایمیل جهت تجربه بازی و ارزیابی سرور:",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.5.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("نام مستعار دلخواه (اختیاری)") },
                                    colors = textFieldColors(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_guest_username")
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                GameButton(
                                    text = "ورود سریع به عنوان مهمان",
                                    onClick = { viewModel.signInAsGuest(username) },
                                    colorStyle = GameButtonColor.GREEN,
                                    height = 48.dp,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "btn_guest_submit"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PersianGold,
    unfocusedBorderColor = Color(0x44FFFFFF),
    focusedLabelColor = PersianGold,
    unfocusedLabelColor = Color(0xAAFFFFFF),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = PersianGold
)
