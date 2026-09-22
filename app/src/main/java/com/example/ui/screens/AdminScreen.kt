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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.UserReport
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.PersianBackground
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldLight
import com.example.ui.theme.VelvetCardGradient
import com.example.ui.theme.VelvetNight

@Composable
fun AdminScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    val sampleReports = remember {
        mutableStateOf(
            listOf(
                UserReport("rep1", "u1", "u_troll", "استفاده بیش از حد از اموجی تمسخر", "EMOTE_SPAM"),
                UserReport("rep2", "u2", "u_afk", "ترک عمدی میز بازی در دست ششم", "DISCONNECT"),
                UserReport("rep3", "u3", "u_abuser", "پیام نامناسب در بازی", "INAPPROPRIATE")
            )
        )
    }

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
                    onClick = { viewModel.navigateTo(ScreenRoute.Settings) },
                    modifier = Modifier.testTag("btn_back_admin")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "دیوان نظارت و مدیریت تخلفات",
                    color = PersianGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "گزارش‌های تخلف ثبت شده (${sampleReports.value.size}):",
                color = PersianGoldLight,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sampleReports.value) { report ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = VelvetNight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.2.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VelvetCardGradient)
                                .padding(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Report,
                                            contentDescription = "تخلف",
                                            tint = Color(0xFFFF5252)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "کاربر متخلف: ${report.targetUserId}",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = report.reason,
                                        color = PersianGold,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "توضیحات: ${report.description}",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    GameButton(
                                        text = "مسدودسازی",
                                        onClick = {
                                            sampleReports.value = sampleReports.value.filter { it.reportId != report.reportId }
                                            Toast.makeText(context, "کاربر با موفقیت مسدود شد", Toast.LENGTH_SHORT).show()
                                        },
                                        icon = Icons.Default.Block,
                                        colorStyle = GameButtonColor.CRIMSON,
                                        height = 36.dp,
                                        fontSize = 11.5.sp
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    GameButton(
                                        text = "نادیده گرفتن",
                                        onClick = {
                                            sampleReports.value = sampleReports.value.filter { it.reportId != report.reportId }
                                            Toast.makeText(context, "گزارش بسته شد", Toast.LENGTH_SHORT).show()
                                        },
                                        icon = Icons.Default.CheckCircle,
                                        colorStyle = GameButtonColor.VELVET,
                                        height = 36.dp,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
