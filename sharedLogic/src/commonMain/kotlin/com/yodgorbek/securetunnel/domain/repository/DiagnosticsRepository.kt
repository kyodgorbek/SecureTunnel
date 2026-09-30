package com.yodgorbek.securetunnel.domain.repository

import com.yodgorbek.securetunnel.core.model.DiagnosticCheck
import com.yodgorbek.securetunnel.core.model.VpnServer
import kotlinx.coroutines.flow.Flow

interface DiagnosticsRepository {
    val diagnosticResults: Flow<List<DiagnosticCheck>>

    suspend fun runFullDiagnostics(targetServer: VpnServer?): List<DiagnosticCheck>
}
