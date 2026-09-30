package com.yodgorbek.securetunnel.feature.assistant

import androidx.lifecycle.viewModelScope
import com.yodgorbek.securetunnel.core.common.MviViewModel
import com.yodgorbek.securetunnel.domain.usecase.ClearAiConversationUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetAiConversationUseCase
import com.yodgorbek.securetunnel.domain.usecase.SendAiMessageUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AssistantViewModel(
    private val getAiConversationUseCase: GetAiConversationUseCase,
    private val sendAiMessageUseCase: SendAiMessageUseCase,
    private val clearAiConversationUseCase: ClearAiConversationUseCase
) : MviViewModel<AssistantIntent, AssistantState, AssistantEffect>(AssistantState()) {

    init {
        // Observe real-time message stream
        getAiConversationUseCase().onEach { msgs ->
            updateState {
                copy(
                    messages = msgs,
                    isLoading = msgs.any { it.isTyping }
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: AssistantIntent) {
        when (intent) {
            is AssistantIntent.UpdateInputText -> {
                updateState { copy(inputText = intent.text) }
            }
            is AssistantIntent.SendMessage -> {
                val query = intent.text.trim()
                if (query.isNotBlank()) {
                    updateState { copy(inputText = "", isLoading = true) }
                    viewModelScope.launch {
                        sendAiMessageUseCase(query)
                        updateState { copy(isLoading = false) }
                    }
                }
            }
            is AssistantIntent.ClearChat -> {
                viewModelScope.launch {
                    clearAiConversationUseCase()
                }
            }
        }
    }
}
