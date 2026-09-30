package com.yodgorbek.securetunnel.feature.diagnostics

import com.yodgorbek.securetunnel.core.common.MviState
import com.yodgorbek.securetunnel.core.model.DiagnosticCheck
import com.yodgorbek.securetunnel.core.model.VpnServer

data class DiagnosticsState(
    val checks: List<DiagnosticCheck> = emptyList(),
    val targetServer: VpnServer? = null,
    val isRunning: Boolean = false
) : MviState
