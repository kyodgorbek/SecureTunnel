package com.yodgorbek.securetunnel.feature.home

import com.yodgorbek.securetunnel.core.common.MviIntent
import com.yodgorbek.securetunnel.core.model.VpnServer

sealed interface HomeIntent : MviIntent {
    data object ToggleConnection : HomeIntent
    data object Disconnect : HomeIntent
    data class SelectServer(val server: VpnServer) : HomeIntent
    data object RefreshServers : HomeIntent
    data object DismissError : HomeIntent
}
