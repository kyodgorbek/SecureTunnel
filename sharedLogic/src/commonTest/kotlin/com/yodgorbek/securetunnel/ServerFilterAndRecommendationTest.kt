package com.yodgorbek.securetunnel

import com.yodgorbek.securetunnel.core.model.ServerFilter
import com.yodgorbek.securetunnel.core.model.ServerSortOption
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.domain.usecase.FilterAndSortServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetRecommendedServerUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ServerFilterAndRecommendationTest {

    private val testServers = listOf(
        VpnServer(
            id = "jp_1",
            hostname = "vg-jp-01.opengw.net",
            ipAddress = "219.100.37.240",
            country = "Japan",
            countryCode = "JP",
            city = "Tokyo",
            sessions = 15,
            uptime = 500000L,
            speed = 80_000_000L,
            ping = 22L,
            score = 120000L,
            openVpnSupported = true
        ),
        VpnServer(
            id = "nl_1",
            hostname = "vg-nl-01.opengw.net",
            ipAddress = "185.220.101.5",
            country = "Netherlands",
            countryCode = "NL",
            city = "Amsterdam",
            sessions = 45,
            uptime = 120000L,
            speed = 25_000_000L,
            ping = 65L,
            score = 65000L,
            openVpnSupported = true
        ),
        VpnServer(
            id = "us_1",
            hostname = "vg-us-01.opengw.net",
            ipAddress = "198.51.100.44",
            country = "United States",
            countryCode = "US",
            city = "New York",
            sessions = 80,
            uptime = 50000L,
            speed = 10_000_000L,
            ping = 180L,
            score = 25000L,
            openVpnSupported = false
        )
    )

    private val filterAndSortUseCase = FilterAndSortServersUseCase()

    @Test
    fun testRecommendedServerCalculation() {
        val fakeRepo = object : com.yodgorbek.securetunnel.domain.repository.VpnRepository {
            override val vpnStatus = kotlinx.coroutines.flow.emptyFlow<com.yodgorbek.securetunnel.core.model.VpnStatus>()
            override val vpnStatistics = kotlinx.coroutines.flow.emptyFlow<com.yodgorbek.securetunnel.core.model.VpnStatistics>()
            override val activeServer = kotlinx.coroutines.flow.emptyFlow<VpnServer?>()
            override val servers = kotlinx.coroutines.flow.flowOf(testServers)
            override val favoriteServers = kotlinx.coroutines.flow.emptyFlow<List<VpnServer>>()
            override val recentServers = kotlinx.coroutines.flow.emptyFlow<List<VpnServer>>()
            override val connectionHistory = kotlinx.coroutines.flow.emptyFlow<List<com.yodgorbek.securetunnel.core.model.ConnectionHistoryItem>>()
            override suspend fun refreshServers() = Result.success(testServers)
            override suspend fun connect(server: VpnServer) = Result.success(Unit)
            override suspend fun disconnect() = Result.success(Unit)
            override suspend fun toggleFavorite(serverId: String) {}
            override suspend fun clearHistory() {}
            override fun getSelectedServer(): VpnServer? = null
            override fun setSelectedServer(server: VpnServer) {}
        }
        val recommender = GetRecommendedServerUseCase(fakeRepo)
        val best = recommender.calculateRecommended(testServers)

        assertNotNull(best)
        assertEquals("jp_1", best.id)
    }

    @Test
    fun testFilterBySearchQuery() {
        val filter = ServerFilter(searchQuery = "amsterdam")
        val results = filterAndSortUseCase(testServers, filter)

        assertEquals(1, results.size)
        assertEquals("nl_1", results.first().id)
    }

    @Test
    fun testSortByLowestPing() {
        val filter = ServerFilter(openVpnOnly = false, sortOption = ServerSortOption.LOWEST_PING)
        val results = filterAndSortUseCase(testServers, filter)

        assertEquals(3, results.size)
        assertEquals("jp_1", results[0].id)
        assertEquals("nl_1", results[1].id)
        assertEquals("us_1", results[2].id)
    }

    @Test
    fun testFilterOpenVpnOnly() {
        val filter = ServerFilter(openVpnOnly = true)
        val results = filterAndSortUseCase(testServers, filter)

        assertEquals(2, results.size)
        assertEquals(listOf("jp_1", "nl_1"), results.map { it.id })
    }
}
