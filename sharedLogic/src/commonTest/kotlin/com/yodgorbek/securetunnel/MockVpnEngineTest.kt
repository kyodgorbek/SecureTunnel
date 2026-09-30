package com.yodgorbek.securetunnel

import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatus
import com.yodgorbek.securetunnel.core.vpn.MockVpnEngine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockVpnEngineTest {

    private val sampleServer = VpnServer(
        id = "nl_test_01",
        hostname = "vg-nl-test.opengw.net",
        ipAddress = "185.220.101.5",
        country = "Netherlands",
        countryCode = "NL",
        city = "Amsterdam",
        sessions = 10,
        uptime = 3600L,
        speed = 50_000_000L,
        ping = 25L,
        score = 80000L,
        openVpnSupported = true
    )

    @Test
    fun testInitialStatusIsDisconnected() = runTest {
        val engine = MockVpnEngine()
        val initialStatus = engine.status.first()
        assertEquals(VpnStatus.Disconnected, initialStatus)
    }

    @Test
    fun testConnectAndDisconnectLifecycle() = runTest {
        val engine = MockVpnEngine()

        val connectResult = engine.connect(sampleServer)
        assertTrue(connectResult.isSuccess)

        val connectedStatus = engine.status.first()
        assertTrue(connectedStatus is VpnStatus.Connected)
        assertEquals("nl_test_01", (connectedStatus as VpnStatus.Connected).serverId)

        val disconnectResult = engine.disconnect()
        assertTrue(disconnectResult.isSuccess)

        val disconnectedStatus = engine.status.first()
        assertEquals(VpnStatus.Disconnected, disconnectedStatus)
    }

    @Test
    fun testFailureSimulation() = runTest {
        val failureEngine = MockVpnEngine(shouldSimulateFailure = true)
        val result = failureEngine.connect(sampleServer)

        assertTrue(result.isFailure)
        val failedStatus = failureEngine.status.first()
        assertTrue(failedStatus is VpnStatus.Failed)
    }
}
