package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.SafetyNumberDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BiometricLockScreen
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.theme.MusaffaTextTheme
import com.example.viewmodel.MusaffaViewModel
import com.example.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {

    private val viewModel: MusaffaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusaffaApp(viewModel = viewModel)
        }
    }
}

@Composable
fun MusaffaApp(viewModel: MusaffaViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemDark
    }

    MusaffaTextTheme(darkTheme = isDark) {
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()
        val isAppLocked by viewModel.isAppLocked.collectAsState()
        val activeChatId by viewModel.activeChatId.collectAsState()
        val activeCall by viewModel.activeCall.collectAsState()
        val safetyContact by viewModel.safetyContact.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = when {
                    !isLoggedIn -> ScreenState.AUTH
                    isAppLocked -> ScreenState.BIOMETRIC_LOCK
                    activeCall != null -> ScreenState.CALL
                    activeChatId != null -> ScreenState.CHAT
                    else -> ScreenState.DASHBOARD
                },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { state ->
                when (state) {
                    ScreenState.AUTH -> {
                        AuthScreen(
                            onLoginSuccess = { phone ->
                                viewModel.loginWithPhone(phone)
                            }
                        )
                    }
                    ScreenState.BIOMETRIC_LOCK -> {
                        BiometricLockScreen(
                            onUnlock = {
                                viewModel.unlockApp()
                            }
                        )
                    }
                    ScreenState.CALL -> {
                        CallScreen(viewModel = viewModel)
                    }
                    ScreenState.CHAT -> {
                        activeChatId?.let { id ->
                            ChatScreen(
                                chatId = id,
                                viewModel = viewModel,
                                onBack = { viewModel.closeChat() }
                            )
                        }
                    }
                    ScreenState.DASHBOARD -> {
                        MainDashboardScreen(viewModel = viewModel)
                    }
                }
            }

            // Safety Number Dialog Overlay
            safetyContact?.let { contact ->
                SafetyNumberDialog(
                    contact = contact,
                    isVerified = contact.isSafetyNumberVerified,
                    onToggleVerified = { verified ->
                        viewModel.toggleContactSafetyVerified(contact.id, verified)
                    },
                    onDismiss = { viewModel.dismissSafetyDialog() }
                )
            }

            // Monetization: Interstitial Ad Overlay
            val showInterstitialAd by viewModel.showInterstitialAd.collectAsState()
            if (showInterstitialAd) {
                com.example.ui.components.InterstitialAdDialog(
                    sponsorName = "Express Crypt VPN",
                    headline = "Protect your connection worldwide with high-speed zero-log encrypted servers.",
                    onDismiss = { viewModel.dismissInterstitialAd() },
                    onAdClick = { viewModel.dismissInterstitialAd() }
                )
            }
        }
    }
}

private enum class ScreenState {
    AUTH,
    BIOMETRIC_LOCK,
    DASHBOARD,
    CHAT,
    CALL
}
