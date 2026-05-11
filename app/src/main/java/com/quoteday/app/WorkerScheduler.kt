package com.quoteday.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.quoteday.app.notification.QuoteAlarmReceiver
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun Context.scheduleDailyQuote() {
    val hour = SettingsPrefs.getNotificationHour(this)
    val minute = SettingsPrefs.getNotificationMinute(this)

    val now = LocalDateTime.now()
    val targetTime = LocalTime.of(hour, minute)
    var target = now.toLocalDate().atTime(targetTime)
    if (!now.toLocalTime().isBefore(targetTime)) {
        target = target.plusDays(1)
    }

    val triggerAtMillis = target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val pending = alarmPendingIntent(this)
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
    } else {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
    }
}

fun Context.cancelDailyQuote() {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(alarmPendingIntent(this))
}

private fun alarmPendingIntent(context: Context): PendingIntent =
    PendingIntent.getBroadcast(
        context, 0,
        Intent(context, QuoteAlarmReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
