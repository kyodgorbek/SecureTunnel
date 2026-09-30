package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class DiagnosticStatus {
    PENDING,
    RUNNING,
    PASSED,
    FAILED,
    WARNING
}

/**
 * Diagnostic test model representing a single network or tunnel verification check.
 */
@Serializable
data class DiagnosticCheck(
    val id: String,
    val title: String,
    val description: String,
    val status: DiagnosticStatus = DiagnosticStatus.PENDING,
    val detail: String? = null,
    val durationMs: Long? = null
)
