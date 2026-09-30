package com.yodgorbek.securetunnel.data.remote

import com.yodgorbek.securetunnel.core.model.VpnServer

/**
 * High-performance, fault-tolerant parser for VPN Gate public CSV server feeds.
 * Handles missing fields, invalid numbers, comment headers, and base64 config extraction.
 */
object VpnGateParser {

    /**
     * Parses raw CSV string data from VPN Gate endpoint into a sanitized list of [VpnServer] models.
     */
    fun parseCsv(csvContent: String): List<VpnServer> {
        if (csvContent.isBlank()) return emptyList()

        val lines = csvContent.lines()
        val servers = mutableListOf<VpnServer>()
        val seenHostnames = mutableSetOf<String>()

        var isDataSection = false

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("*")) continue

            // Detect header line
            if (line.startsWith("#HostName", ignoreCase = true) || line.startsWith("HostName,", ignoreCase = true)) {
                isDataSection = true
                continue
            }

            if (!isDataSection && !line.contains(",")) continue

            val parts = parseCsvLine(line)
            if (parts.size < 14) continue

            val hostname = parts.getOrNull(0)?.trim() ?: continue
            val ip = parts.getOrNull(1)?.trim().orEmpty()
            if (hostname.isBlank() && ip.isBlank()) continue

            // Deduplicate by unique hostname/IP combo
            val uniqueKey = if (ip.isNotBlank()) ip else hostname
            if (seenHostnames.contains(uniqueKey)) continue
            seenHostnames.add(uniqueKey)

            val score = parts.getOrNull(2)?.toLongOrNull()
            val ping = parts.getOrNull(3)?.toLongOrNull()
            val speed = parts.getOrNull(4)?.toLongOrNull()
            val countryLong = parts.getOrNull(5)?.trim() ?: "Unknown"
            val countryShort = parts.getOrNull(6)?.trim() ?: "XX"
            val numSessions = parts.getOrNull(7)?.toIntOrNull() ?: 0
            val uptime = parts.getOrNull(8)?.toLongOrNull()
            val operator = parts.getOrNull(12)?.trim()
            val openVpnConfigDataBase64 = parts.getOrNull(14)?.trim()

            val hasConfig = !openVpnConfigDataBase64.isNullOrBlank()

            val server = VpnServer(
                id = "${countryShort.lowercase()}_${uniqueKey.replace(".", "_")}",
                hostname = hostname,
                ipAddress = ip.ifBlank { null },
                country = countryLong,
                countryCode = countryShort.uppercase(),
                city = extractCityFromHostnameOrOperator(hostname, operator),
                sessions = numSessions,
                uptime = uptime,
                speed = speed,
                ping = ping,
                score = score,
                openVpnSupported = hasConfig,
                configurationUrl = null,
                openVpnConfigDataBase64 = openVpnConfigDataBase64,
                isVolunteer = true,
                operatorName = operator?.takeIf { it.isNotBlank() }
            )

            servers.add(server)
        }

        return servers
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false

        for (i in line.indices) {
            val c = line[i]
            if (c == '\"') {
                inQuotes = !inQuotes
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString())
                sb.clear()
            } else {
                sb.append(c)
            }
        }
        tokens.add(sb.toString())
        return tokens
    }

    private fun extractCityFromHostnameOrOperator(hostname: String, operator: String?): String? {
        val op = operator?.trim().orEmpty()
        if (op.isNotBlank() && !op.startsWith("http", ignoreCase = true) && op.length < 30) {
            return op
        }
        val parts = hostname.split(".")
        if (parts.isNotEmpty()) {
            val prefix = parts[0]
            if (prefix.length in 3..25 && !prefix.all { it.isDigit() }) {
                return prefix.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
        return null
    }
}
