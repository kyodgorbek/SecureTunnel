package com.yodgorbek.securetunnel.feature.history

import com.yodgorbek.securetunnel.core.common.MviState
import com.yodgorbek.securetunnel.core.model.ConnectionHistoryItem

data class HistoryState(
    val historyItems: List<ConnectionHistoryItem> = emptyList(),
    val totalSessions: Int = 0,
    val totalDataTransferred: Long = 0L,
    val totalDurationSeconds: Long = 0L
) : MviState
