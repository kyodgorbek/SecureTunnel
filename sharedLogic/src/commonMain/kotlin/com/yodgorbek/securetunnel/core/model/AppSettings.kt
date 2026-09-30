package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

/**
 * Application preferences and configuration state.
 */
@Serializable
data class AppSettings(
    val autoConnect: Boolean = false,
    val killSwitchEnabled: Boolean = false,
    val preferredCountryCode: String? = null,
    val preferredServerId: String? = null,
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val aiAssistantEnabled: Boolean = true,
    val groqApiKey: String = "",
    val groqModel: String = "llama-3.3-70b-versatile",
    val customDnsPrimary: String = "1.1.1.1",
    val customDnsSecondary: String = "1.0.0.1",
    val diagnosticsEnabled: Boolean = true,
    val historyEnabled: Boolean = true,
    val useMockVpnForDebug: Boolean = false,
    val hasSeenOnboarding: Boolean = false
)
