package com.yodgorbek.securetunnel.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.yodgorbek.securetunnel.core.model.VpnError
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus
import com.yodgorbek.securetunnel.core.vpn.VpnEngine
import kotlinx.coroutines.flow.Flow

/**
 * Android implementation of VpnEngine driving SecureTunnelVpnService.
 */
class AndroidVpnEngine(
    private val context: Context
) : VpnEngine {

    override val status: Flow<VpnStatus> = SecureTunnelVpnService.serviceStatus
    override val statistics: Flow<VpnStatistics> = SecureTunnelVpnService.serviceStatistics

    override suspend fun connect(server: VpnServer): Result<Unit> {
        // Check if Android VPN permission is prepared
        val prepareIntent = VpnService.prepare(context)
        if (prepareIntent != null) {
            SecureTunnelVpnService.serviceStatus.value = VpnStatus.Failed(VpnError.PermissionRequired)
            return Result.failure(IllegalStateException("VPN Permission required"))
        }

        val intent = Intent(context, SecureTunnelVpnService::class.java).apply {
            action = SecureTunnelVpnService.ACTION_CONNECT
            putExtra(SecureTunnelVpnService.EXTRA_SERVER_HOSTNAME, server.hostname)
            putExtra(SecureTunnelVpnService.EXTRA_SERVER_IP, server.ipAddress)
            putExtra(SecureTunnelVpnService.EXTRA_SERVER_COUNTRY, server.country)
            putExtra(SecureTunnelVpnService.EXTRA_SERVER_ID, server.id)
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            SecureTunnelVpnService.serviceStatus.value = VpnStatus.Failed(VpnError.Unknown(e.message ?: "Service failed to start"))
            return Result.failure(e)
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        val intent = Intent(context, SecureTunnelVpnService::class.java).apply {
            action = SecureTunnelVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
        return Result.success(Unit)
    }
}
