package com.yodgorbek.securetunnel.feature.history

import com.yodgorbek.securetunnel.core.common.MviEffect

sealed interface HistoryEffect : MviEffect {
    data class ShowToast(val message: String) : HistoryEffect
}
