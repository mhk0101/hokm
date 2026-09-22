package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.engine.HokmRulesEngine
import com.example.core.engine.NetworkMonitor
import com.example.core.engine.SoundManager
import com.example.core.model.Card
import com.example.core.model.EmoteType
import com.example.core.model.GameMode
import com.example.core.model.GamePhase
import com.example.core.model.HokmGameState
import com.example.core.model.LeaderboardEntry
import com.example.core.model.PrivateRoom
import com.example.core.model.Suit
import com.example.core.model.TableStyle
import com.example.core.model.UserProfile
import com.example.core.model.UserReport
import com.example.core.preferences.AppSettings
import com.example.core.preferences.UserPreferencesRepository
import com.example.data.firebase.FirebaseAuthDataSource
import com.example.data.firebase.FirebaseGameDataSource
import com.example.data.firebase.FirebaseManager
import com.example.data.firebase.FirebaseMatchmakingDataSource
import com.example.data.firebase.FirebaseProfileDataSource
import com.example.data.firebase.FirebaseRoomDataSource
import com.example.data.firebase.MatchmakingStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class ScreenRoute {
    object Splash : ScreenRoute()
    object Auth : ScreenRoute()
    object Home : ScreenRoute()
    object Matchmaking : ScreenRoute()
    data class PrivateRoom(val roomCode: String) : ScreenRoute()
    data class Game(val gameId: String) : ScreenRoute()
    data class GameResult(val gameId: String) : ScreenRoute()
    data class Replay(val gameId: String) : ScreenRoute()
    object Leaderboard : ScreenRoute()
    object Profile : ScreenRoute()
    object ThemeShop : ScreenRoute()
    object Friends : ScreenRoute()
    object Settings : ScreenRoute()
    object Admin : ScreenRoute()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepo = UserPreferencesRepository(application)
    val soundManager = SoundManager(application)
    val networkMonitor = NetworkMonitor(application)

    val authDataSource = FirebaseAuthDataSource()
    val gameDataSource = FirebaseGameDataSource()
    val matchmakingDataSource = FirebaseMatchmakingDataSource(gameDataSource)
    val roomDataSource = FirebaseRoomDataSource(gameDataSource)
    val profileDataSource = FirebaseProfileDataSource()

    val settings: StateFlow<AppSettings> = preferencesRepo.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val currentUser: StateFlow<UserProfile?> = authDataSource.currentUserProfile

    private val _currentScreen = MutableStateFlow<ScreenRoute>(ScreenRoute.Splash)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    // Active Game State
    private val _activeGame = MutableStateFlow<HokmGameState?>(null)
    val activeGame: StateFlow<HokmGameState?> = _activeGame.asStateFlow()

    private val _userHand = MutableStateFlow<List<Card>>(emptyList())
    val userHand: StateFlow<List<Card>> = _userHand.asStateFlow()

    private val _currentRoom = MutableStateFlow<PrivateRoom?>(null)
    val currentRoom: StateFlow<PrivateRoom?> = _currentRoom.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _activeEmotes = MutableStateFlow<Map<Int, String>>(emptyMap())
    val activeEmotes: StateFlow<Map<Int, String>> = _activeEmotes.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var gameObservationJob: Job? = null
    private var handObservationJob: Job? = null
    private var roomObservationJob: Job? = null

    init {
        FirebaseManager.checkOrInitialize(application)
        viewModelScope.launch {
            delay(1200) // Splash delay
            val user = authDataSource.currentUserProfile.value
            if (user != null) {
                _currentScreen.value = ScreenRoute.Home
            } else {
                _currentScreen.value = ScreenRoute.Auth
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        soundManager.playButtonClick(settings.value.soundEnabled)
        _currentScreen.value = route
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // --- Auth Actions ---
    fun register(email: String, pass: String, username: String) {
        viewModelScope.launch {
            val result = authDataSource.registerUser(email, pass, username)
            result.onSuccess {
                _currentScreen.value = ScreenRoute.Home
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "خطا در ثبت نام"
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val result = authDataSource.loginUser(email, pass)
            result.onSuccess {
                _currentScreen.value = ScreenRoute.Home
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "خطا در ورود"
            }
        }
    }

    fun signInAsGuest(username: String) {
        viewModelScope.launch {
            val result = authDataSource.signInAsGuest(username)
            result.onSuccess {
                _currentScreen.value = ScreenRoute.Home
            }
        }
    }

    fun signOut() {
        authDataSource.signOut()
        _currentScreen.value = ScreenRoute.Auth
    }

    // --- Matchmaking ---
    fun startQuickMatch(mode: GameMode = GameMode.RANKED) {
        val user = currentUser.value ?: return
        navigateTo(ScreenRoute.Matchmaking)
        viewModelScope.launch {
            matchmakingDataSource.enterQueue(user, mode)
            matchmakingDataSource.status.collect { status ->
                if (status is MatchmakingStatus.Matched) {
                    joinGameSession(status.gameId)
                } else if (status is MatchmakingStatus.Error) {
                    _errorMessage.value = status.message
                }
            }
        }
    }

    fun cancelMatchmaking() {
        viewModelScope.launch {
            matchmakingDataSource.leaveQueue()
            navigateTo(ScreenRoute.Home)
        }
    }

    // --- Private Room ---
    fun createPrivateRoom() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = roomDataSource.createRoom(user)
            result.onSuccess { room ->
                observeRoom(room.roomCode)
                navigateTo(ScreenRoute.PrivateRoom(room.roomCode))
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "خطا در ساخت اتاق"
            }
        }
    }

    fun joinPrivateRoom(code: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = roomDataSource.joinRoom(code, user)
            result.onSuccess { room ->
                observeRoom(room.roomCode)
                navigateTo(ScreenRoute.PrivateRoom(room.roomCode))
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "اتاق پیدا نشد یا پر است"
            }
        }
    }

    private fun observeRoom(code: String) {
        roomObservationJob?.cancel()
        roomObservationJob = viewModelScope.launch {
            roomDataSource.observeRoom(code).collect { room ->
                _currentRoom.value = room
                if (room?.gameId != null && room.status == com.example.core.model.RoomStatus.IN_GAME) {
                    joinGameSession(room.gameId)
                }
            }
        }
    }

    fun toggleRoomReady() {
        val room = currentRoom.value ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch {
            roomDataSource.toggleReady(room.roomCode, user.userId)
        }
    }

    fun startPrivateRoomGame() {
        val room = currentRoom.value ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = roomDataSource.startRoomGame(room.roomCode, user.userId)
            result.onSuccess { gameId ->
                joinGameSession(gameId)
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "خطا در شروع بازی"
            }
        }
    }

    fun leaveRoom() {
        val room = currentRoom.value ?: return
        val user = currentUser.value ?: return
        viewModelScope.launch {
            roomDataSource.leaveRoom(room.roomCode, user.userId)
            _currentRoom.value = null
            navigateTo(ScreenRoute.Home)
        }
    }

    // --- Game Session ---
    fun joinGameSession(gameId: String) {
        val user = currentUser.value ?: return
        navigateTo(ScreenRoute.Game(gameId))

        gameObservationJob?.cancel()
        gameObservationJob = viewModelScope.launch {
            gameDataSource.observeGameState(gameId).collect { state ->
                _activeGame.value = state
                if (state != null) {
                    if (state.phase == GamePhase.MATCH_COMPLETED) {
                        soundManager.playGameWon(settings.value.soundEnabled)
                        navigateTo(ScreenRoute.GameResult(gameId))
                    } else if (state.phase == GamePhase.TRICK_RESOLVED) {
                        soundManager.playTrickWon(settings.value.soundEnabled)
                        delay(1200)
                        gameDataSource.advanceToNextTrick(gameId)
                    }
                }
            }
        }

        handObservationJob?.cancel()
        handObservationJob = viewModelScope.launch {
            gameDataSource.observePlayerHand(gameId, user.userId).collect { hand ->
                _userHand.value = hand
            }
        }
    }

    fun selectTrump(suit: Suit) {
        val game = activeGame.value ?: return
        val user = currentUser.value ?: return
        soundManager.playTrumpSelected(settings.value.soundEnabled)
        viewModelScope.launch {
            gameDataSource.selectTrump(game.gameId, user.userId, suit, UUID.randomUUID().toString())
        }
    }

    fun playCard(card: Card) {
        val game = activeGame.value ?: return
        val user = currentUser.value ?: return
        val actionId = UUID.randomUUID().toString()

        soundManager.playCardSound(settings.value.soundEnabled)
        soundManager.vibrate(settings.value.vibrationEnabled, 30L)

        viewModelScope.launch {
            val result = gameDataSource.playCard(game.gameId, user.userId, card, actionId)
            result.onFailure {
                _errorMessage.value = it.localizedMessage ?: "حرکت غیرمجاز"
            }
        }
    }

    fun sendEmote(emote: EmoteType) {
        val game = activeGame.value ?: return
        val user = currentUser.value ?: return
        val player = game.players.find { it.userId == user.userId } ?: return
        val currentEmotes = _activeEmotes.value.toMutableMap()
        currentEmotes[player.seatIndex] = emote.symbol
        _activeEmotes.value = currentEmotes

        viewModelScope.launch {
            delay(3000)
            val updated = _activeEmotes.value.toMutableMap()
            updated.remove(player.seatIndex)
            _activeEmotes.value = updated
        }
    }

    // --- Cosmetics & Shop ---
    fun selectTheme(themeId: String) {
        viewModelScope.launch {
            preferencesRepo.setTheme(themeId)
        }
    }

    fun selectCardBack(cardBackId: String) {
        viewModelScope.launch {
            preferencesRepo.setCardBack(cardBackId)
        }
    }

    fun buyItem(itemId: String, cost: Long, isTheme: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = profileDataSource.purchaseCosmetic(user.userId, itemId, cost, isTheme)
            result.onSuccess {
                authDataSource.loadUserProfile(user.userId)
                if (isTheme) selectTheme(itemId) else selectCardBack(itemId)
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "خطا در خرید"
            }
        }
    }

    fun claimDaily(day: Int) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = profileDataSource.claimDailyReward(user.userId, day)
            result.onSuccess {
                authDataSource.loadUserProfile(user.userId)
            }.onFailure {
                _errorMessage.value = it.localizedMessage ?: "جایزه امروز قبلاً دریافت شده است."
            }
        }
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _leaderboard.value = profileDataSource.getLeaderboard()
        }
    }

    // --- Settings Toggles ---
    fun toggleSound(enabled: Boolean) = viewModelScope.launch { preferencesRepo.setSoundEnabled(enabled) }
    fun toggleVibration(enabled: Boolean) = viewModelScope.launch { preferencesRepo.setVibrationEnabled(enabled) }
    fun toggleAnimations(enabled: Boolean) = viewModelScope.launch { preferencesRepo.setAnimationsEnabled(enabled) }
    fun toggleNotifications(enabled: Boolean) = viewModelScope.launch { preferencesRepo.setNotificationsEnabled(enabled) }
}
