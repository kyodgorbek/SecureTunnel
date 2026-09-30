package com.yodgorbek.securetunnel.feature.history

import androidx.lifecycle.viewModelScope
import com.yodgorbek.securetunnel.core.common.MviViewModel
import com.yodgorbek.securetunnel.domain.usecase.ClearHistoryUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetConnectionHistoryUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getConnectionHistoryUseCase: GetConnectionHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : MviViewModel<HistoryIntent, HistoryState, HistoryEffect>(HistoryState()) {

    init {
        getConnectionHistoryUseCase().onEach { items ->
            val totalBytes = items.sumOf { it.totalBytesIn + it.totalBytesOut }
            val totalSecs = items.sumOf { it.durationSeconds }
            updateState {
                copy(
                    historyItems = items,
                    totalSessions = items.size,
                    totalDataTransferred = totalBytes,
                    totalDurationSeconds = totalSecs
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.ClearAllHistory -> {
                viewModelScope.launch {
                    clearHistoryUseCase()
                    sendEffect(HistoryEffect.ShowToast("Connection history cleared"))
                }
            }
        }
    }
}
