package com.yodgorbek.securetunnel.feature.history

import com.yodgorbek.securetunnel.core.common.MviIntent

sealed interface HistoryIntent : MviIntent {
    data object ClearAllHistory : HistoryIntent
}
