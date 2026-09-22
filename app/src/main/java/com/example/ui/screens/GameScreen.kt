package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.engine.HokmRulesEngine
import com.example.core.model.Card
import com.example.core.model.CardBackStyle
import com.example.core.model.CurrentTrick
import com.example.core.model.GamePhase
import com.example.core.model.GameTheme
import com.example.core.model.PlayerSlot
import com.example.core.model.Suit
import com.example.ui.MainViewModel
import com.example.ui.components.CardBackView
import com.example.ui.components.CardView
import com.example.ui.components.EmoteSelector
import com.example.ui.components.FannedHandLayout
import com.example.ui.components.GameNotificationBanner
import com.example.ui.components.PlayerAvatarView
import com.example.ui.components.ReconnectionBanner
import com.example.ui.components.TableSurface
import com.example.ui.components.TrumpSelectionDialog
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
import com.example.ui.theme.toPersianDigits

/**
 * Redesigned Royal Persian Game Table Screen:
 * - Outer dark wood-tone frame with gold inlay line and ornamental corner accents
 * - Deep green felt with radial lighting & fabric texture
 * - Top-center premium Trump chip ("حکم") with gold frame & pulse
 * - Top-left & top-right compact score panels with gold frames & team color accents
 * - Player seats with circular avatars, gold rims, dark nameplates and Persian ratings
 * - Active turn glow ring & floating "نوبت شماست" pill
 * - Disconnected player dashed connection ring & "در حال اتصال مجدد"
 * - Darker felt oval for center trick area
 * - Fanned hand layout at bottom
 */
@Composable
fun GameScreen(
    gameId: String,
    viewModel: MainViewModel
) {
    val state by viewModel.activeGame.collectAsState()
    val hand by viewModel.userHand.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val activeEmotes by viewModel.activeEmotes.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    var showEmoteBar by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        val err = errorMessage
        if (err != null) {
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    val activeTheme = GameTheme.ALL_THEMES.find { it.id == settings.activeThemeId }
        ?: GameTheme.ALL_THEMES.first()

    val cardBackStyle = runCatching {
        CardBackStyle.valueOf(settings.activeCardBackId.uppercase())
    }.getOrDefault(CardBackStyle.PERSIAN)

    val mySlot = state?.players?.find { it.userId == currentUser?.userId }
        ?: PlayerSlot(seatIndex = 0, userId = currentUser?.userId ?: "")

    val mySeat = mySlot.seatIndex
    val isMyTurn = state?.currentTurnSeat == mySeat

    // Seats relative to current player:
    // Right = (mySeat + 1) % 4
    // Top (Partner) = (mySeat + 2) % 4
    // Left = (mySeat + 3) % 4
    val rightSlot = state?.players?.find { it.seatIndex == (mySeat + 1) % 4 }
        ?: PlayerSlot(seatIndex = (mySeat + 1) % 4)
    val topSlot = state?.players?.find { it.seatIndex == (mySeat + 2) % 4 }
        ?: PlayerSlot(seatIndex = (mySeat + 2) % 4)
    val leftSlot = state?.players?.find { it.seatIndex == (mySeat + 3) % 4 }
        ?: PlayerSlot(seatIndex = (mySeat + 3) % 4)

    // Hakem Trump Selection Phase
    val isTrumpSelectionPhase = state?.phase == GamePhase.SELECTING_TRUMP
    val amIHakem = state?.hakemSeatIndex == mySeat

    if (isTrumpSelectionPhase && amIHakem) {
        TrumpSelectionDialog(
            onTrumpSelected = { selectedSuit ->
                viewModel.selectTrump(selectedSuit)
            }
        )
    }

    val statusMessage = when {
        isTrumpSelectionPhase && amIHakem -> "حاکم گرامی، خال حکم را تعیین کنید"
        isTrumpSelectionPhase -> "در انتظار صدور حکم توسط حاکم..."
        isMyTurn -> "⚡ نوبت شماست — یک برگ بازی کنید"
        else -> null
    }

    TableSurface(tableStyle = activeTheme.style) {
        Column(modifier = Modifier.fillMaxSize()) {
            ReconnectionBanner(isOnline = isOnline)

            // ==========================================
            // 1. TOP HUD: Score Panels & Center Trump Chip
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Top-Left (RTL): Team A (Our Team)
                TeamScoreCompactPanel(
                    teamName = "تیم ما",
                    teamPoints = state?.teamAPoints ?: 0,
                    tricksCurrentHand = state?.teamATricksCurrentHand ?: 0,
                    targetPoints = state?.targetPoints ?: 7,
                    isTeamA = true
                )

                // Top-Center: Premium Trump Badge Chip
                PremiumTrumpChip(trumpSuit = state?.trumpSuit)

                // Top-Right (RTL): Team B (Opponent Team)
                TeamScoreCompactPanel(
                    teamName = "حریف",
                    teamPoints = state?.teamBPoints ?: 0,
                    tricksCurrentHand = state?.teamBTricksCurrentHand ?: 0,
                    targetPoints = state?.targetPoints ?: 7,
                    isTeamA = false
                )
            }

            // ==========================================
            // 2. CENTRAL PLAYING TABLE
            // ==========================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                // TOP PLAYER (Partner - Seat + 2)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PlayerAvatarView(
                            player = topSlot,
                            isCurrentTurn = state?.currentTurnSeat == topSlot.seatIndex,
                            activeEmote = activeEmotes[topSlot.seatIndex],
                            level = 16,
                            rating = 1450
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-18).dp),
                            modifier = Modifier.offset(y = (-4).dp)
                        ) {
                            repeat(3) {
                                CardBackView(
                                    style = cardBackStyle,
                                    width = 24.dp,
                                    height = 34.dp
                                )
                            }
                        }
                    }
                }

                // LEFT OPPONENT (Seat + 3)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PlayerAvatarView(
                            player = leftSlot,
                            isCurrentTurn = state?.currentTurnSeat == leftSlot.seatIndex,
                            activeEmote = activeEmotes[leftSlot.seatIndex],
                            level = 14,
                            rating = 1380
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-18).dp),
                            modifier = Modifier.offset(y = (-4).dp)
                        ) {
                            repeat(2) {
                                CardBackView(
                                    style = cardBackStyle,
                                    width = 24.dp,
                                    height = 34.dp
                                )
                            }
                        }
                    }
                }

                // RIGHT OPPONENT (Seat + 1)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PlayerAvatarView(
                            player = rightSlot,
                            isCurrentTurn = state?.currentTurnSeat == rightSlot.seatIndex,
                            activeEmote = activeEmotes[rightSlot.seatIndex],
                            level = 15,
                            rating = 1410
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-18).dp),
                            modifier = Modifier.offset(y = (-4).dp)
                        ) {
                            repeat(2) {
                                CardBackView(
                                    style = cardBackStyle,
                                    width = 24.dp,
                                    height = 34.dp
                                )
                            }
                        }
                    }
                }

                // CENTER TRICK AREA (Cards played land in felt oval)
                val currentTrick = state?.currentTrick ?: CurrentTrick()
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    currentTrick.cards.forEach { (seat, card) ->
                        val (offsetX, offsetY, rot) = when ((seat - mySeat + 4) % 4) {
                            0 -> Triple(0.dp, 28.dp, 0f)       // Bottom (User)
                            1 -> Triple(32.dp, 0.dp, 90f)      // Right
                            2 -> Triple(0.dp, (-28).dp, 180f)  // Top
                            3 -> Triple((-32).dp, 0.dp, (-90f)) // Left
                            else -> Triple(0.dp, 0.dp, 0f)
                        }

                        CardView(
                            card = card,
                            width = 50.dp,
                            height = 74.dp,
                            rotation = rot,
                            modifier = Modifier.offset(x = offsetX, y = offsetY)
                        )
                    }
                }

                // FLOATING TURN / STATUS NOTIFICATION
                GameNotificationBanner(
                    text = statusMessage,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 78.dp),
                    isAlert = isMyTurn
                )

                // BOTTOM PLAYER (USER AVATAR - Bottom-Start in RTL)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 118.dp, start = 8.dp)
                ) {
                    PlayerAvatarView(
                        player = mySlot,
                        isCurrentTurn = isMyTurn,
                        activeEmote = activeEmotes[mySeat],
                        level = currentUser?.level ?: 18,
                        rating = currentUser?.rating ?: 1500
                    )
                }

                // EMOTE SELECTOR TOGGLE BUTTON (Bottom-End in RTL)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 118.dp, end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF380E0E), BurgundyPanel)
                                )
                            )
                            .border(1.6.dp, GoldBorderBrush, CircleShape)
                            .clickable { showEmoteBar = !showEmoteBar }
                            .testTag("btn_toggle_emotes"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions,
                            contentDescription = "کل‌کل و اموجی",
                            tint = GoldLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // POPUP EMOTE DOCK
            AnimatedVisibility(
                visible = showEmoteBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmoteSelector(
                    onEmoteSelected = { emote ->
                        viewModel.sendEmote(emote)
                        showEmoteBar = false
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }

            // ==========================================
            // 3. USER'S HAND CONTAINER (FANNED / ARC CARDS)
            // ==========================================
            UserHandFannedSection(
                hand = hand,
                currentTrick = state?.currentTrick ?: CurrentTrick(),
                trumpSuit = state?.trumpSuit,
                isMyTurn = isMyTurn,
                onCardSelected = { card ->
                    viewModel.playCard(card)
                }
            )
        }
    }
}

/**
 * Compact Score Panel (Top Corners) with Gold Frame & Team Color Accent
 */
@Composable
private fun TeamScoreCompactPanel(
    teamName: String,
    teamPoints: Int,
    tricksCurrentHand: Int,
    targetPoints: Int,
    isTeamA: Boolean
) {
    val teamAccentColor = if (isTeamA) Color(0xFF00E5FF) else Color(0xFFFF5252)

    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A0606), Color(0xFF160303))
                )
            )
            .border(1.5.dp, GoldBorderBrush, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Team Accent Dot
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(teamAccentColor)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = teamName,
                    color = IvoryTextPrimary,
                    fontSize = 11.5.sp,
                    fontFamily = LalezarFamily,
                    fontWeight = FontWeight.Normal
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "دست: ${teamPoints.toPersianDigits()}/${targetPoints.toPersianDigits()}",
                        color = TitleGold,
                        fontSize = 10.5.sp,
                        fontFamily = VazirmatnFamily,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "برگ: ${tricksCurrentHand.toPersianDigits()}",
                        color = IvoryTextSecondary,
                        fontSize = 9.5.sp,
                        fontFamily = VazirmatnFamily
                    )
                }
            }
        }
    }
}

/**
 * Premium Trump Chip (Top-Center)
 * - 'حکم' + suit symbol
 * - Gold frame
 * - Animated pulse when selected
 */
@Composable
private fun PremiumTrumpChip(
    trumpSuit: Suit?
) {
    val infiniteTransition = rememberInfiniteTransition(label = "trumpGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trumpScale"
    )

    Box(
        modifier = Modifier
            .scale(if (trumpSuit != null) pulse else 1f)
            .shadow(
                elevation = if (trumpSuit != null) 10.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = GoldLight
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF380E0E), Color(0xFF1E0505))
                )
            )
            .border(1.8.dp, GoldBorderBrush, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "حکم",
                color = TitleGold,
                fontSize = 13.sp,
                fontFamily = LalezarFamily,
                fontWeight = FontWeight.Normal
            )

            if (trumpSuit != null) {
                Text(
                    text = "${trumpSuit.symbol} ${trumpSuit.persianName}",
                    color = if (trumpSuit.isRed) Color(0xFFFF5252) else GoldLight,
                    fontSize = 14.sp,
                    fontFamily = LalezarFamily,
                    fontWeight = FontWeight.Normal
                )
            } else {
                Text(
                    text = "تعیین‌نشده",
                    color = IvoryTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = VazirmatnFamily
                )
            }
        }
    }
}

/**
 * Bottom container for user's hand, fanning cards gracefully across an arc.
 */
@Composable
private fun UserHandFannedSection(
    hand: List<Card>,
    currentTrick: CurrentTrick,
    trumpSuit: Suit?,
    isMyTurn: Boolean,
    onCardSelected: (Card) -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = BurgundyBottom),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(
                width = 1.8.dp,
                brush = if (isMyTurn) GoldBorderBrush else Brush.linearGradient(
                    listOf(GoldBorder.copy(alpha = 0.4f), GoldBorder.copy(alpha = 0.15f))
                ),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2A0606), Color(0xFF180303))
                    )
                )
                .padding(top = 4.dp, bottom = 6.dp)
        ) {
            FannedHandLayout(
                hand = hand,
                isMyTurn = isMyTurn,
                isCardPlayable = { card ->
                    val result = HokmRulesEngine.validateCardPlay(hand, card, currentTrick, trumpSuit)
                    result is HokmRulesEngine.MoveValidationResult.Valid
                },
                onCardClick = onCardSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
