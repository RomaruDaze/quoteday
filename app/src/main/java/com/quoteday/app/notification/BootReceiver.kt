package com.quoteday.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.quoteday.app.scheduleDailyQuote

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.scheduleDailyQuote()
        }
    }
}
