package com.yodgorbek.securetunnel.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VpnServerDto(
    val hostname: String,
    val ip: String,
    val score: Long? = null,
    val ping: Long? = null,
    val speed: Long? = null,
    val countryLong: String,
    val countryShort: String,
    val numVpnSessions: Int = 0,
    val uptime: Long? = null,
    val totalUsers: Long? = null,
    val totalTraffic: Long? = null,
    val logType: String? = null,
    val operator: String? = null,
    val message: String? = null,
    val openVpnConfigDataBase64: String? = null
)
