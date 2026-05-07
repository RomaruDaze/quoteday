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
    val hour = SettingsPrefs.getNotificationHour(this)
    val minute = SettingsPrefs.getNotificationMinute(this)

    val now = LocalDateTime.now()
    val targetTime = LocalTime.of(hour, minute)
    val target = now.toLocalDate().atTime(targetTime)

    val next = if (now.toLocalTime().isAfter(targetTime)) target.plusDays(1) else target
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

fun Context.cancelDailyQuote() {
    WorkManager.getInstance(this).cancelUniqueWork(WORK_NAME)
}
