package com.yodgorbek.securetunnel.feature.diagnostics

import com.yodgorbek.securetunnel.core.common.MviIntent

sealed interface DiagnosticsIntent : MviIntent {
    data object RunDiagnostics : DiagnosticsIntent
}
