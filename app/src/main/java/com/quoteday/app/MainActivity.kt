package com.quoteday.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.quoteday.app.notification.NotificationHelper
import com.quoteday.app.ui.QuoteScreen
import com.quoteday.app.ui.QuoteViewModel
import com.quoteday.app.ui.SettingsScreen
import com.quoteday.app.ui.SettingsViewModel
import com.quoteday.app.ui.SignInScreen
import com.quoteday.app.ui.SplashScreen
import com.quoteday.app.ui.UpgradeDialog
import com.quoteday.app.ui.theme.QuoteDayTheme

private enum class Screen { Quote, Settings }

class MainActivity : ComponentActivity() {
    private val viewModel: QuoteViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* handled silently */ }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            account.idToken?.let { viewModel.onGoogleIdToken(it) }
        } catch (_: Exception) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createChannel(this)
        scheduleDailyQuote()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            QuoteDayTheme {
                val currentUser by viewModel.currentUser.collectAsState()
                val isPremium by viewModel.isPremium.collectAsState()
                val productPrice by viewModel.productPrice.collectAsState()
                val showUpgradePrompt by viewModel.showUpgradePrompt.collectAsState()
                var splashVisible by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf(Screen.Quote) }

                if (splashVisible) {
                    SplashScreen { splashVisible = false }
                } else if (currentUser == null) {
                    SignInScreen(onSignInClick = { launchGoogleSignIn() })
                } else {
                    when (currentScreen) {
                        Screen.Quote -> QuoteScreen(
                            viewModel = viewModel,
                            onSettingsClick = { currentScreen = Screen.Settings }
                        )
                        Screen.Settings -> SettingsScreen(
                            viewModel = settingsViewModel,
                            onBack = { currentScreen = Screen.Quote },
                            onRestorePurchases = { viewModel.restorePurchases() },
                            isPremium = isPremium,
                            productPrice = productPrice,
                            onUpgradeClick = { viewModel.triggerUpgradePrompt() },
                        )
                    }

                    if (showUpgradePrompt) {
                        val activity = LocalContext.current as android.app.Activity
                        UpgradeDialog(
                            productPrice = productPrice,
                            onUpgradeClick = {
                                viewModel.dismissUpgradePrompt()
                                viewModel.launchPurchase(activity)
                            },
                            onDismiss = { viewModel.dismissUpgradePrompt() }
                        )
                    }
                }
            }
        }
    }

    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInLauncher.launch(GoogleSignIn.getClient(this, gso).signInIntent)
    }
}
