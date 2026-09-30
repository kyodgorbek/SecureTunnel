package com.yodgorbek.securetunnel.feature.assistant

import com.yodgorbek.securetunnel.core.common.MviState
import com.yodgorbek.securetunnel.core.model.AiMessage

data class AssistantState(
    val messages: List<AiMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : MviState
