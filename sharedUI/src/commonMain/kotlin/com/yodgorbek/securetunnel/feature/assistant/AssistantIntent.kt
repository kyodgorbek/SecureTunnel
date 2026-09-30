package com.yodgorbek.securetunnel.feature.assistant

import com.yodgorbek.securetunnel.core.common.MviIntent

sealed interface AssistantIntent : MviIntent {
    data class UpdateInputText(val text: String) : AssistantIntent
    data class SendMessage(val text: String) : AssistantIntent
    data object ClearChat : AssistantIntent
}
