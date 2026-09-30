package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

/**
 * Chat message structure for SecureTunnel AI conversations.
 */
@Serializable
data class AiMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val isTyping: Boolean = false,
    val isError: Boolean = false,
    val suggestedActions: List<String> = emptyList()
)
