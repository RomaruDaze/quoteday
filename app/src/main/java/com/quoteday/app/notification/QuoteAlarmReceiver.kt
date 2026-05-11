package com.quoteday.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.quoteday.app.scheduleDailyQuote

class QuoteAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<QuoteDailyWorker>().build())
        // Reschedule for the same time tomorrow
        context.scheduleDailyQuote()
    }
}
