package com.yodgorbek.securetunnel.domain.repository

import com.yodgorbek.securetunnel.core.model.AiMessage
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    val conversation: Flow<List<AiMessage>>

    suspend fun sendMessage(userText: String): Result<AiMessage>
    suspend fun clearConversation()
}
