package com.yodgorbek.securetunnel.feature.settings

import com.yodgorbek.securetunnel.core.common.MviState
import com.yodgorbek.securetunnel.core.model.AppSettings

data class SettingsState(
    val settings: AppSettings = AppSettings()
) : MviState
