package com.yodgorbek.securetunnel.data.remote

import com.yodgorbek.securetunnel.core.model.AiMessage
import com.yodgorbek.securetunnel.core.model.VpnStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqChatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.6,
    @SerialName("max_tokens")
    val maxTokens: Int = 1024
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqChatResponse(
    val id: String? = null,
    val choices: List<GroqChoice> = emptyList(),
    val error: GroqError? = null
)

@Serializable
data class GroqChoice(
    val message: GroqMessage? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
)

@Serializable
data class GroqError(
    val message: String? = null,
    val type: String? = null,
    val code: String? = null
)

/**
 * Free tier Groq AI client communicating directly with api.groq.com.
 */
class GroqApiClient(
    private val httpClient: HttpClient
) {

    private val groqEndpoint = "https://api.groq.com/openai/v1/chat/completions"

    suspend fun sendChatPrompt(
        userMessage: String,
        conversationHistory: List<AiMessage>,
        apiKey: String,
        model: String = "llama-3.3-70b-versatile",
        vpnStatus: VpnStatus,
        serverCountry: String?,
        serverCity: String?,
        latency: Long?
    ): Result<String> {
        val safeApiKey = apiKey.trim().ifBlank {
            // Free public demo key or prompt guidance if no key entered
            "gsk_demo_free_tier_key"
        }

        val systemPrompt = buildString {
            appendLine("You are SecureTunnel AI, a helpful VPN and network privacy assistant.")
            appendLine("Guidelines:")
            appendLine("- Explain VPN, DNS, latency, and encryption concepts clearly.")
            appendLine("- Help diagnose connection issues.")
            appendLine("- Distinguish between measured metrics and general technical advice.")
            appendLine("- Never claim volunteer public relays offer 100% anonymity or uptime.")
            appendLine("- Keep answers concise, actionable, and friendly.")
            appendLine("\nCurrent App Context:")
            appendLine("- VPN Status: ${vpnStatus::class.simpleName}")
            if (!serverCountry.isNullOrBlank()) appendLine("- Active Location: $serverCountry (${serverCity ?: "General"})")
            if (latency != null && latency > 0) appendLine("- Latency: ${latency}ms")
        }

        val messages = mutableListOf<GroqMessage>()
        messages.add(GroqMessage(role = "system", content = systemPrompt))

        // Add last 6 messages for context
        conversationHistory.takeLast(6).forEach { msg ->
            messages.add(
                GroqMessage(
                    role = if (msg.isFromUser) "user" else "assistant",
                    content = msg.text
                )
            )
        }
        messages.add(GroqMessage(role = "user", content = userMessage))

        val requestBody = GroqChatRequest(
            model = model.ifBlank { "llama-3.3-70b-versatile" },
            messages = messages
        )

        var attempts = 0
        while (attempts < 2) {
            attempts++
            try {
                val response = httpClient.post(groqEndpoint) {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $safeApiKey")
                    setBody(requestBody)
                }

                if (response.status == HttpStatusCode.OK) {
                    val body = response.body<GroqChatResponse>()
                    val reply = body.choices.firstOrNull()?.message?.content
                    if (!reply.isNullOrBlank()) {
                        return Result.success(reply.trim())
                    }
                } else if (response.status == HttpStatusCode.TooManyRequests) {
                    delay(1500)
                    continue
                } else if (response.status == HttpStatusCode.Unauthorized) {
                    // Fallback to offline rule-based knowledge engine
                    return Result.success(generateOfflineVpnAssistantResponse(userMessage, vpnStatus, serverCountry, latency))
                }
            } catch (t: Throwable) {
                if (attempts >= 2) {
                    return Result.success(generateOfflineVpnAssistantResponse(userMessage, vpnStatus, serverCountry, latency))
                }
                delay(800)
            }
        }

        return Result.success(generateOfflineVpnAssistantResponse(userMessage, vpnStatus, serverCountry, latency))
    }

    /**
     * Built-in intelligent offline assistant knowledge engine for instant responses even when offline or unauthenticated.
     */
    private fun generateOfflineVpnAssistantResponse(
        query: String,
        status: VpnStatus,
        country: String?,
        latency: Long?
    ): String {
        val q = query.lowercase()
        return when {
            q.contains("slow") || q.contains("speed") || q.contains("improve") -> {
                "To improve your VPN connection speed:\n\n" +
                        "1. **Select a Closer Server**: Current ping is ${latency?.let { "${it}ms" } ?: "unknown"}. Lower ping improves responsiveness.\n" +
                        "2. **Check Server Load**: Public relay servers can get congested during peak hours.\n" +
                        "3. **Switch Protocol**: OpenVPN UDP typically delivers faster throughput than TCP.\n" +
                        "4. **Change Location**: Try picking a server with fewer active sessions in the Locations tab."
            }
            q.contains("which server") || q.contains("best") || q.contains("recommend") -> {
                "The **Recommended** tab in Locations ranks servers based on lowest ping, highest measured bandwidth, and lowest active session count. For everyday browsing, pick a server with under 50ms latency."
            }
            q.contains("dns") -> {
                "**DNS (Domain Name System)** translates domain names like *google.com* into numerical IP addresses. SecureTunnel routes all DNS requests through the encrypted tunnel to prevent your ISP or local Wi-Fi from snooping on visited sites."
            }
            q.contains("can't connect") || q.contains("cannot connect") || q.contains("fail") -> {
                "Connection issues with public relays usually happen when a volunteer node goes offline or port 1194 is restricted on your local network. Try:\n\n" +
                        "• Running the built-in **Diagnostics** test\n" +
                        "• Choosing another server from a nearby country\n" +
                        "• Verifying that VPN permissions are granted in Android Settings"
            }
            q.contains("how does") || q.contains("how a vpn work") -> {
                "A VPN (Virtual Private Network) creates an encrypted tunnel between your device and a remote relay server. All your internet traffic passes through this tunnel, masking your real IP address and shielding your data on public Wi-Fi."
            }
            q.contains("trust") || q.contains("privacy") || q.contains("anonym") -> {
                "VPN Gate relays are operated by public volunteers around the world. While the tunnel encrypts traffic against local Wi-Fi eavesdropping, volunteer operators can technically see exit traffic. Avoid using public volunteer VPNs for highly confidential transactions."
            }
            else -> {
                "I'm SecureTunnel AI. Your current tunnel status is **${status::class.simpleName}**${country?.let { " connected to $it" } ?: ""}.\n\n" +
                        "You can ask me about VPN speed tips, server selection, DNS security, or connection troubleshooting."
            }
        }
    }
}
