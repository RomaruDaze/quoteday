package com.quoteday.app

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.quoteday.app.notification.QuoteDailyWorker
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "daily_quote_work"

fun Context.scheduleDailyQuote() {
    val now = LocalDateTime.now()
    val target = now.toLocalDate().atTime(LocalTime.of(8, 0))

    // If 8 AM already passed today, schedule for tomorrow
    val next = if (now.toLocalTime().isAfter(LocalTime.of(8, 0))) {
        target.plusDays(1)
    } else {
        target
    }

    val initialDelay = Duration.between(now, next).toMinutes()

    val request = PeriodicWorkRequestBuilder<QuoteDailyWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        WORK_NAME,
        ExistingPeriodicWorkPolicy.UPDATE,
        request
    )
}
