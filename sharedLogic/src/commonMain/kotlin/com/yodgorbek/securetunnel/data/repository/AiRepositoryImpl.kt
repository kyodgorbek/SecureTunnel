package com.yodgorbek.securetunnel.data.repository

import com.yodgorbek.securetunnel.core.model.AiMessage
import com.yodgorbek.securetunnel.core.model.VpnStatus
import com.yodgorbek.securetunnel.data.local.VpnStorage
import com.yodgorbek.securetunnel.data.remote.GroqApiClient
import com.yodgorbek.securetunnel.domain.repository.AiRepository
import com.yodgorbek.securetunnel.domain.repository.VpnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

class AiRepositoryImpl(
    private val groqApiClient: GroqApiClient,
    private val vpnRepository: VpnRepository,
    private val vpnStorage: VpnStorage
) : AiRepository {

    private val _conversation = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                id = "welcome_1",
                text = "Hello! I'm SecureTunnel AI, your VPN and network privacy assistant. How can I assist you today?",
                isFromUser = false,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                suggestedActions = listOf(
                    "Why is my VPN slow?",
                    "Which server should I choose?",
                    "What is DNS?",
                    "How does a VPN work?"
                )
            )
        )
    )
    override val conversation: Flow<List<AiMessage>> = _conversation.asStateFlow()

    override suspend fun sendMessage(userText: String): Result<AiMessage> {
        val now = Clock.System.now().toEpochMilliseconds()
        val userMsg = AiMessage(
            id = "user_$now",
            text = userText,
            isFromUser = true,
            timestamp = now
        )

        val typingMsg = AiMessage(
            id = "typing_$now",
            text = "Analyzing network...",
            isFromUser = false,
            timestamp = now + 1,
            isTyping = true
        )

        _conversation.update { it + userMsg + typingMsg }

        val settings = vpnStorage.getSettings()
        val currentStatus = vpnRepository.vpnStatus.first()
        val activeServer = vpnRepository.getSelectedServer()

        val aiResult = groqApiClient.sendChatPrompt(
            userMessage = userText,
            conversationHistory = _conversation.value.filter { !it.isTyping },
            apiKey = settings.groqApiKey,
            model = settings.groqModel,
            vpnStatus = currentStatus,
            serverCountry = activeServer?.country,
            serverCity = activeServer?.city,
            latency = activeServer?.ping
        )

        val responseText = aiResult.getOrElse { "SecureTunnel AI is temporarily unable to connect. Please check your network and try again." }
        val aiReply = AiMessage(
            id = "ai_${Clock.System.now().toEpochMilliseconds()}",
            text = responseText,
            isFromUser = false,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            isTyping = false,
            suggestedActions = getNextSuggestions(userText)
        )

        // Replace typing bubble with actual response
        _conversation.update { list ->
            list.filter { !it.isTyping } + aiReply
        }

        return Result.success(aiReply)
    }

    override suspend fun clearConversation() {
        _conversation.value = listOf(
            AiMessage(
                id = "welcome_${Clock.System.now().toEpochMilliseconds()}",
                text = "Chat cleared. What would you like to explore regarding your VPN or network connection?",
                isFromUser = false,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                suggestedActions = listOf(
                    "Why is my VPN slow?",
                    "Which server should I choose?",
                    "What is DNS?",
                    "How does a VPN work?"
                )
            )
        )
    }

    private fun getNextSuggestions(query: String): List<String> {
        val q = query.lowercase()
        return when {
            q.contains("slow") -> listOf("Which server has lowest ping?", "Switch to OpenVPN UDP", "Run network diagnostics")
            q.contains("dns") -> listOf("Is my DNS leaking?", "What is DNS over HTTPS?", "How to change DNS in Settings?")
            q.contains("server") -> listOf("Show servers with lowest ping", "What does server load mean?")
            else -> listOf("Why is my VPN slow?", "Which server should I choose?", "Run Diagnostics")
        }
    }
}
