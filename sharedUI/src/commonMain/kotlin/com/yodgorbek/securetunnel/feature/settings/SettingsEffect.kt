package com.yodgorbek.securetunnel.feature.settings

import com.yodgorbek.securetunnel.core.common.MviEffect

sealed interface SettingsEffect : MviEffect {
    data class ShowToast(val message: String) : SettingsEffect
}
