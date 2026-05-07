package com.quoteday.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quoteday.app.SettingsPrefs
import com.quoteday.app.cancelDailyQuote
import com.quoteday.app.data.QuoteDatabase
import com.quoteday.app.notification.NotificationHelper
import com.quoteday.app.scheduleDailyQuote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application

    private val _notificationEnabled = MutableStateFlow(SettingsPrefs.isNotificationEnabled(ctx))
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    private val _notificationHour = MutableStateFlow(SettingsPrefs.getNotificationHour(ctx))
    val notificationHour: StateFlow<Int> = _notificationHour.asStateFlow()

    private val _notificationMinute = MutableStateFlow(SettingsPrefs.getNotificationMinute(ctx))
    val notificationMinute: StateFlow<Int> = _notificationMinute.asStateFlow()

    fun setNotificationEnabled(enabled: Boolean) {
        SettingsPrefs.setNotificationEnabled(ctx, enabled)
        _notificationEnabled.value = enabled
        if (enabled) ctx.scheduleDailyQuote() else ctx.cancelDailyQuote()
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        SettingsPrefs.setNotificationTime(ctx, hour, minute)
        _notificationHour.value = hour
        _notificationMinute.value = minute
        if (_notificationEnabled.value) ctx.scheduleDailyQuote()
    }

    fun testNotification() {
        viewModelScope.launch {
            val quote = QuoteDatabase.getDatabase(ctx).quoteDao().getRandom() ?: return@launch
            NotificationHelper.showQuoteNotification(ctx, quote.text)
        }
    }
}
