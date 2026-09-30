package com.yodgorbek.securetunnel.feature.locations

import com.yodgorbek.securetunnel.core.common.MviState
import com.yodgorbek.securetunnel.core.model.ServerFilter
import com.yodgorbek.securetunnel.core.model.VpnServer

data class LocationsState(
    val rawServers: List<VpnServer> = emptyList(),
    val displayedServers: List<VpnServer> = emptyList(),
    val recommendedServer: VpnServer? = null,
    val selectedServer: VpnServer? = null,
    val filter: ServerFilter = ServerFilter(),
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
) : MviState
