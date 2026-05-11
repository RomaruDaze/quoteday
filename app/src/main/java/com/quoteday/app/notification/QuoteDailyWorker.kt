package com.quoteday.app.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quoteday.app.SettingsPrefs
import com.quoteday.app.data.FirestoreRepository
import java.time.LocalDate

class QuoteDailyWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = SettingsPrefs.getUid(context) ?: return Result.success()
        val quotes = FirestoreRepository.fetchAll(uid)
        val quote = quotes.randomOrNull() ?: return Result.success()
        // Save first so the main screen shows the same quote as the notification
        SettingsPrefs.setTodayQuote(context, LocalDate.now().toString(), quote.firestoreId)
        NotificationHelper.showQuoteNotification(context, quote.text)
        return Result.success()
    }
}
