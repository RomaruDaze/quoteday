package com.quoteday.app.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.quoteday.app.data.QuoteDatabase

class QuoteDailyWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = QuoteDatabase.getDatabase(context)
        val quote = db.quoteDao().getRandom() ?: return Result.success()
        NotificationHelper.showQuoteNotification(context, quote.text)
        return Result.success()
    }
}
