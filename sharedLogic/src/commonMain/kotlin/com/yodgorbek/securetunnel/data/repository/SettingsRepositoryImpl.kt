package com.yodgorbek.securetunnel.data.repository

import com.yodgorbek.securetunnel.core.model.AppSettings
import com.yodgorbek.securetunnel.data.local.VpnStorage
import com.yodgorbek.securetunnel.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val vpnStorage: VpnStorage
) : SettingsRepository {

    override val settings: Flow<AppSettings> = vpnStorage.settings

    override fun getSettings(): AppSettings {
        return vpnStorage.getSettings()
    }

    override suspend fun updateSettings(reducer: AppSettings.() -> AppSettings) {
        vpnStorage.updateSettings(reducer)
    }
}
