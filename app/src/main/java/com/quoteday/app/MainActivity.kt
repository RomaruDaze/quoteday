package com.quoteday.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.quoteday.app.notification.NotificationHelper
import com.quoteday.app.ui.QuoteScreen
import com.quoteday.app.ui.QuoteViewModel
import com.quoteday.app.ui.SplashScreen
import com.quoteday.app.ui.theme.QuoteDayTheme

class MainActivity : ComponentActivity() {
    private val viewModel: QuoteViewModel by viewModels()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permission result handled silently */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        NotificationHelper.createChannel(this)
        scheduleDailyQuote()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            QuoteDayTheme {
                var splashVisible by remember { mutableStateOf(true) }
                if (splashVisible) {
                    SplashScreen { splashVisible = false }
                } else {
                    QuoteScreen(viewModel)
                }
            }
        }
    }
}
