package com.yodgorbek.securetunnel.feature.diagnostics

import com.yodgorbek.securetunnel.core.common.MviEffect

sealed interface DiagnosticsEffect : MviEffect {
    data class ShowToast(val message: String) : DiagnosticsEffect
}
