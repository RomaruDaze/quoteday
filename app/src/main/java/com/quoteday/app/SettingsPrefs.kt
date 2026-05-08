package com.quoteday.app

import android.content.Context

object SettingsPrefs {
    private const val NAME = "settings"
    private const val KEY_NOTIF_ENABLED = "notif_enabled"
    private const val KEY_NOTIF_HOUR = "notif_hour"
    private const val KEY_NOTIF_MINUTE = "notif_minute"
    private const val KEY_UID = "uid"

    fun isNotificationEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIF_ENABLED, true)

    fun setNotificationEnabled(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()

    fun getNotificationHour(context: Context): Int =
        prefs(context).getInt(KEY_NOTIF_HOUR, 8)

    fun getNotificationMinute(context: Context): Int =
        prefs(context).getInt(KEY_NOTIF_MINUTE, 0)

    fun setNotificationTime(context: Context, hour: Int, minute: Int) =
        prefs(context).edit()
            .putInt(KEY_NOTIF_HOUR, hour)
            .putInt(KEY_NOTIF_MINUTE, minute)
            .apply()

    fun getUid(context: Context): String? =
        prefs(context).getString(KEY_UID, null)

    fun setUid(context: Context, uid: String?) =
        prefs(context).edit().apply {
            if (uid == null) remove(KEY_UID) else putString(KEY_UID, uid)
        }.apply()

    private fun prefs(context: Context) =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
}
