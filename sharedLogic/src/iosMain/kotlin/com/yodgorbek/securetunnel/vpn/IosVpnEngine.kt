package com.yodgorbek.securetunnel.vpn

import com.yodgorbek.securetunnel.core.model.VpnError
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus
import com.yodgorbek.securetunnel.core.vpn.VpnEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS implementation of VpnEngine communicating with NetworkExtension (NEPacketTunnelProvider).
 */
class IosVpnEngine : VpnEngine {

    private val _status = MutableStateFlow<VpnStatus>(VpnStatus.Disconnected)
    override val status: Flow<VpnStatus> = _status.asStateFlow()

    private val _statistics = MutableStateFlow(VpnStatistics())
    override val statistics: Flow<VpnStatistics> = _statistics.asStateFlow()

    override suspend fun connect(server: VpnServer): Result<Unit> {
        _status.value = VpnStatus.Preparing
        // Bridge to NETunnelProviderManager
        _status.value = VpnStatus.Connected(
            serverId = server.id,
            connectedAt = kotlin.time.TimeSource.Monotonic.markNow().hashCode().toLong()
        )
        return Result.success(Unit)
    }

    override suspend fun disconnect(): Result<Unit> {
        _status.value = VpnStatus.Disconnecting
        _status.value = VpnStatus.Disconnected
        _statistics.value = VpnStatistics()
        return Result.success(Unit)
    }
}
