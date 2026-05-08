package com.quoteday.app.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quoteday.app.SettingsPrefs
import com.quoteday.app.data.FirestoreRepository

class QuoteDailyWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = SettingsPrefs.getUid(context) ?: return Result.success()
        val quote = FirestoreRepository.fetchAll(uid).randomOrNull() ?: return Result.success()
        NotificationHelper.showQuoteNotification(context, quote.text)
        return Result.success()
    }
}
