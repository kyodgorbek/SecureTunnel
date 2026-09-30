package com.yodgorbek.securetunnel.feature.home

import com.yodgorbek.securetunnel.core.common.MviState
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus

data class HomeState(
    val status: VpnStatus = VpnStatus.Disconnected,
    val selectedServer: VpnServer? = null,
    val statistics: VpnStatistics = VpnStatistics(),
    val recentServers: List<VpnServer> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : MviState
