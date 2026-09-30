package com.yodgorbek.securetunnel.feature.diagnostics

import androidx.lifecycle.viewModelScope
import com.yodgorbek.securetunnel.core.common.MviViewModel
import com.yodgorbek.securetunnel.domain.usecase.GetActiveServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetDiagnosticResultsUseCase
import com.yodgorbek.securetunnel.domain.usecase.RunDiagnosticsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DiagnosticsViewModel(
    private val runDiagnosticsUseCase: RunDiagnosticsUseCase,
    private val getDiagnosticResultsUseCase: GetDiagnosticResultsUseCase,
    private val getActiveServerUseCase: GetActiveServerUseCase
) : MviViewModel<DiagnosticsIntent, DiagnosticsState, DiagnosticsEffect>(DiagnosticsState()) {

    init {
        // Observe diagnostic checklist status
        getDiagnosticResultsUseCase().onEach { results ->
            updateState { copy(checks = results) }
        }.launchIn(viewModelScope)

        // Observe target server
        getActiveServerUseCase().onEach { server ->
            updateState { copy(targetServer = server) }
        }.launchIn(viewModelScope)

        // Trigger initial diagnostic check
        onIntent(DiagnosticsIntent.RunDiagnostics)
    }

    override fun onIntent(intent: DiagnosticsIntent) {
        when (intent) {
            is DiagnosticsIntent.RunDiagnostics -> {
                viewModelScope.launch {
                    updateState { copy(isRunning = true) }
                    val results = runDiagnosticsUseCase(currentState.targetServer)
                    updateState { copy(isRunning = false, checks = results) }
                }
            }
        }
    }
}
