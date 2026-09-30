package com.yodgorbek.securetunnel.domain.usecase

import com.yodgorbek.securetunnel.core.model.AppSettings
import com.yodgorbek.securetunnel.core.model.DiagnosticCheck
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.domain.repository.DiagnosticsRepository
import com.yodgorbek.securetunnel.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = repository.settings
}

class UpdateSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(reducer: AppSettings.() -> AppSettings) = repository.updateSettings(reducer)
}

class RunDiagnosticsUseCase(private val repository: DiagnosticsRepository) {
    suspend operator fun invoke(targetServer: VpnServer?): List<DiagnosticCheck> = repository.runFullDiagnostics(targetServer)
}

class GetDiagnosticResultsUseCase(private val repository: DiagnosticsRepository) {
    operator fun invoke(): Flow<List<DiagnosticCheck>> = repository.diagnosticResults
}
