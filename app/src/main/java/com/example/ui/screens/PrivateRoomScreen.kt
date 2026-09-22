package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.RoomSeat
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.components.GameButton
import com.example.ui.components.GameButtonColor
import com.example.ui.components.GamePanel
import com.example.ui.components.PersianBackground
import com.example.ui.theme.GoldBorderBrush
import com.example.ui.theme.GoldLinearGradient
import com.example.ui.theme.PersianCrimson
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldLight
import com.example.ui.theme.VelvetCardGradient
import com.example.ui.theme.VelvetNight
import com.example.ui.theme.VelvetSurface

@Composable
fun PrivateRoomScreen(
    roomCode: String,
    viewModel: MainViewModel
) {
    val room by viewModel.currentRoom.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    val mySeat = room?.seats?.find { it.userId == currentUser?.userId }
    val isHost = mySeat?.isHost == true

    PersianBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.leaveRoom() },
                    modifier = Modifier.testTag("btn_leave_room")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "خروج",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "اتاق اختصاصی ۴ نفره",
                    color = PersianGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Room Code Card
            GamePanel(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "کد دعوت به اتاق:",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = roomCode,
                            color = PersianGoldLight,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp
                        )
                    }

                    GameButton(
                        text = "کپی کد",
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Room Code", roomCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "کد اتاق در حافظه کپی شد", Toast.LENGTH_SHORT).show()
                        },
                        icon = Icons.Default.ContentCopy,
                        colorStyle = GameButtonColor.GOLD,
                        height = 42.dp,
                        fontSize = 13.sp,
                        testTag = "btn_copy_room_code"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "صندلی‌های بازی (هم‌تیمی‌ها روبروی هم):",
                color = PersianGoldLight,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Seats List
            val seats = room?.seats ?: List(4) { RoomSeat(seatIndex = it) }
            seats.forEach { seat ->
                SeatCard(
                    seat = seat,
                    isMe = seat.userId == currentUser?.userId
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            if (isHost) {
                GameButton(
                    text = if (room?.occupiedCount == 4) "⚔️ آغاز بازی حکم" else "در انتظار تکمیل صندلی‌ها (${room?.occupiedCount ?: 1}/۴)",
                    onClick = { viewModel.startPrivateRoomGame() },
                    enabled = room?.occupiedCount == 4,
                    colorStyle = GameButtonColor.GOLD,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_start_room_game"
                )
            } else {
                GameButton(
                    text = if (mySeat?.isReady == true) "آماده بازی هستید ✓" else "اعلام آمادگی برای بازی",
                    onClick = { viewModel.toggleRoomReady() },
                    colorStyle = if (mySeat?.isReady == true) GameButtonColor.GREEN else GameButtonColor.GOLD,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_toggle_ready"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            GameButton(
                text = "خروج از اتاق",
                onClick = { viewModel.leaveRoom() },
                colorStyle = GameButtonColor.VELVET,
                height = 44.dp,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_leave_room"
            )
        }
    }
}

@Composable
private fun SeatCard(
    seat: RoomSeat,
    isMe: Boolean
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = VelvetNight),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .border(
                width = if (isMe) 2.dp else 1.dp,
                brush = if (isMe) GoldBorderBrush else Brush.linearGradient(
                    listOf(Color(0x33DFBA57), Color(0x11DFBA57))
                ),
                shape = RoundedCornerShape(14.dp)
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
                            .clip(CircleShape)
                            .background(if (seat.isOccupied) PersianCrimson else Color(0x22FFFFFF))
                            .border(1.2.dp, if (seat.isOccupied) PersianGold else Color(0x33FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (seat.isOccupied) Icons.Default.Person else Icons.Default.PersonAdd,
                            contentDescription = "Seat",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (seat.isOccupied) seat.username ?: "بازیکن" else "صندلی خالی ${seat.seatIndex + 1}",
                            color = if (seat.isOccupied) Color.White else Color(0x77FFFFFF),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "تیم ${if (seat.seatIndex % 2 == 0) "الف (اول)" else "ب (دوم)"}" +
                                    if (seat.isHost) " • میزبان اتاق" else "",
                            color = PersianGold,
                            fontSize = 11.5.sp
                        )
                    }
                }

                if (seat.isOccupied) {
                    val isReady = seat.isReady || seat.isHost
                    Box(
                        modifier = Modifier
                            .shadow(2.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isReady) Color(0xFF1B5E20) else Color(0xFFE65100))
                            .border(
                                1.dp,
                                if (isReady) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isReady) "آماده ✓" else "در انتظار",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
