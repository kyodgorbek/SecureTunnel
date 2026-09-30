package com.yodgorbek.securetunnel.domain.repository

import com.yodgorbek.securetunnel.core.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    fun getSettings(): AppSettings
    suspend fun updateSettings(reducer: AppSettings.() -> AppSettings)
}
