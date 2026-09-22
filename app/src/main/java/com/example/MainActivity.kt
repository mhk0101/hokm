package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.ScreenRoute
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.GameResultScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchmakingScreen
import com.example.ui.screens.PrivateRoomScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReplayScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.ThemeShopScreen
import com.example.ui.theme.HokmChiTheme
import com.example.ui.theme.VelvetNight

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HokmChiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VelvetNight
                ) {
                    HokmChiApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HokmChiApp(viewModel: MainViewModel) {
    val currentRoute by viewModel.currentScreen.collectAsState()

    // Handle back button logically
    BackHandler(enabled = currentRoute !is ScreenRoute.Home && currentRoute !is ScreenRoute.Splash && currentRoute !is ScreenRoute.Auth) {
        when (currentRoute) {
            is ScreenRoute.Game -> {
                // Confirm or navigate to home
                viewModel.navigateTo(ScreenRoute.Home)
            }
            is ScreenRoute.Matchmaking -> viewModel.cancelMatchmaking()
            is ScreenRoute.PrivateRoom -> viewModel.leaveRoom()
            else -> viewModel.navigateTo(ScreenRoute.Home)
        }
    }

    Crossfade(targetState = currentRoute, label = "screenTransition") { route ->
        when (route) {
            is ScreenRoute.Splash -> SplashScreen()
            is ScreenRoute.Auth -> AuthScreen(viewModel = viewModel)
            is ScreenRoute.Home -> HomeScreen(viewModel = viewModel)
            is ScreenRoute.Matchmaking -> MatchmakingScreen(viewModel = viewModel)
            is ScreenRoute.PrivateRoom -> PrivateRoomScreen(roomCode = route.roomCode, viewModel = viewModel)
            is ScreenRoute.Game -> GameScreen(gameId = route.gameId, viewModel = viewModel)
            is ScreenRoute.GameResult -> GameResultScreen(gameId = route.gameId, viewModel = viewModel)
            is ScreenRoute.Replay -> ReplayScreen(gameId = route.gameId, viewModel = viewModel)
            is ScreenRoute.Leaderboard -> LeaderboardScreen(viewModel = viewModel)
            is ScreenRoute.Profile -> ProfileScreen(viewModel = viewModel)
            is ScreenRoute.ThemeShop -> ThemeShopScreen(viewModel = viewModel)
            is ScreenRoute.Friends -> FriendsScreen(viewModel = viewModel)
            is ScreenRoute.Settings -> SettingsScreen(viewModel = viewModel)
            is ScreenRoute.Admin -> AdminScreen(viewModel = viewModel)
        }
    }
}

