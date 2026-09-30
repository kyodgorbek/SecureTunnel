package com.yodgorbek.securetunnel.domain.usecase

import com.yodgorbek.securetunnel.core.model.AiMessage
import com.yodgorbek.securetunnel.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow

class GetAiConversationUseCase(private val repository: AiRepository) {
    operator fun invoke(): Flow<List<AiMessage>> = repository.conversation
}

class SendAiMessageUseCase(private val repository: AiRepository) {
    suspend operator fun invoke(userMessage: String): Result<AiMessage> = repository.sendMessage(userMessage)
}

class ClearAiConversationUseCase(private val repository: AiRepository) {
    suspend operator fun invoke() = repository.clearConversation()
}
