package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

/**
 * Real-time traffic statistics and connection duration.
 */
@Serializable
data class VpnStatistics(
    val bytesIn: Long = 0L,
    val bytesOut: Long = 0L,
    val downloadSpeedBps: Long = 0L,
    val uploadSpeedBps: Long = 0L,
    val durationSeconds: Long = 0L
) {
    val formattedDuration: String
        get() {
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val seconds = durationSeconds % 60
            return if (hours > 0) {
                "${hours.padZero(2)}:${minutes.padZero(2)}:${seconds.padZero(2)}"
            } else {
                "${minutes.padZero(2)}:${seconds.padZero(2)}"
            }
        }

    val formattedDownload: String
        get() = formatBytes(bytesIn)

    val formattedUpload: String
        get() = formatBytes(bytesOut)

    val formattedDownloadSpeed: String
        get() = "${formatBytes(downloadSpeedBps)}/s"

    val formattedUploadSpeed: String
        get() = "${formatBytes(uploadSpeedBps)}/s"

    companion object {
        fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 B"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> "${(gb * 10).toLong() / 10.0} GB"
                mb >= 1.0 -> "${(mb * 10).toLong() / 10.0} MB"
                kb >= 1.0 -> "${(kb * 10).toLong() / 10.0} KB"
                else -> "$bytes B"
            }
        }

        private fun Long.padZero(length: Int): String {
            val s = this.toString()
            return if (s.length >= length) s else "0".repeat(length - s.length) + s
        }
    }
}
