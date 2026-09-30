package com.yodgorbek.securetunnel.core.vpn

import com.yodgorbek.securetunnel.core.model.VpnError
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Mock VPN Engine for development, unit testing, and UI preview environments.
 * Simulates realistic connection handshakes and dynamic traffic statistics.
 */
class MockVpnEngine(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val shouldSimulateFailure: Boolean = false
) : VpnEngine {

    private val _status = MutableStateFlow<VpnStatus>(VpnStatus.Disconnected)
    override val status: Flow<VpnStatus> = _status.asStateFlow()

    private val _statistics = MutableStateFlow(VpnStatistics())
    override val statistics: Flow<VpnStatistics> = _statistics.asStateFlow()

    private var statsJob: Job? = null
    private var activeServer: VpnServer? = null

    override suspend fun connect(server: VpnServer): Result<Unit> {
        activeServer = server
        _status.value = VpnStatus.Preparing
        delay(600)

        _status.value = VpnStatus.Connecting
        delay(1200)

        if (shouldSimulateFailure) {
            _status.value = VpnStatus.Failed(VpnError.ConnectionTimeout)
            return Result.failure(IllegalStateException("Simulated connection timeout"))
        }

        val connectedAt = kotlin.time.TimeSource.Monotonic.markNow().hashCode().toLong()
        _status.value = VpnStatus.Connected(
            serverId = server.id,
            connectedAt = connectedAt
        )

        startStatsSimulation()
        return Result.success(Unit)
    }

    override suspend fun disconnect(): Result<Unit> {
        _status.value = VpnStatus.Disconnecting
        stopStatsSimulation()
        delay(500)
        _status.value = VpnStatus.Disconnected
        _statistics.value = VpnStatistics()
        activeServer = null
        return Result.success(Unit)
    }

    private fun startStatsSimulation() {
        statsJob?.cancel()
        statsJob = scope.launch {
            var duration = 0L
            var totalDown = 0L
            var totalUp = 0L

            while (isActive) {
                delay(1000)
                duration += 1

                val downSpeed = Random.nextLong(250_000, 4_500_000) // 250 KB/s - 4.5 MB/s
                val upSpeed = Random.nextLong(50_000, 950_000)     // 50 KB/s - 950 KB/s

                totalDown += downSpeed
                totalUp += upSpeed

                _statistics.value = VpnStatistics(
                    bytesIn = totalDown,
                    bytesOut = totalUp,
                    downloadSpeedBps = downSpeed,
                    uploadSpeedBps = upSpeed,
                    durationSeconds = duration
                )
            }
        }
    }

    private fun stopStatsSimulation() {
        statsJob?.cancel()
        statsJob = null
    }
}
