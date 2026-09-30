package com.yodgorbek.securetunnel.feature.settings

import com.yodgorbek.securetunnel.core.common.MviIntent
import com.yodgorbek.securetunnel.core.model.AppThemeMode

sealed interface SettingsIntent : MviIntent {
    data class ToggleAutoConnect(val enabled: Boolean) : SettingsIntent
    data class ToggleKillSwitch(val enabled: Boolean) : SettingsIntent
    data class SetThemeMode(val mode: AppThemeMode) : SettingsIntent
    data class UpdateGroqApiKey(val key: String) : SettingsIntent
    data class UpdateCustomDns(val primary: String, val secondary: String) : SettingsIntent
}
