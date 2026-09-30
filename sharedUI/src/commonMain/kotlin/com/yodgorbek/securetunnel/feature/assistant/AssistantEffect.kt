package com.yodgorbek.securetunnel.feature.assistant

import com.yodgorbek.securetunnel.core.common.MviEffect

sealed interface AssistantEffect : MviEffect {
    data class ShowToast(val message: String) : AssistantEffect
}
