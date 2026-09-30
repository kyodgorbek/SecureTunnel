package com.yodgorbek.securetunnel

import com.yodgorbek.securetunnel.data.remote.VpnGateParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VpnGateParserTest {

    private val sampleCsv = """
        *vpn_servers
        #HostName,IP,Score,Ping,Speed,CountryLong,CountryShort,NumVpnSessions,Uptime,TotalUsers,TotalTraffic,LogType,Operator,Message,OpenVPN_ConfigData_Base64
        vg-jp-01.opengw.net,219.100.37.240,110000,24,68000000,Japan,JP,18,510000,12000,9999999,2Weeks,TsukubaRelay,Welcome,ZHVtbXlfb3BlbnZwbl9kYXRh
        vg-nl-01.opengw.net,185.220.101.5,85000,31,45000000,Netherlands,NL,12,120000,4500,888888,2Weeks,AmstRelay,Fast,ZHVtbXlfb3BlbnZwbl9kYXRh
        vg-jp-01.opengw.net,219.100.37.240,110000,24,68000000,Japan,JP,18,510000,12000,9999999,2Weeks,Duplicate,,ZHVtbXlfb3BlbnZwbl9kYXRh
        malformed_row_with_few_commas,1.2.3.4
        ,,invalid,not_a_number,speed,Invalid,XX,0,0,0,0,none,,,
    """.trimIndent()

    @Test
    fun testParseCsv_ParsesValidRows() {
        val servers = VpnGateParser.parseCsv(sampleCsv)

        // Should parse 2 unique valid servers and ignore duplicates and malformed lines
        assertEquals(2, servers.size)

        val japanServer = servers.find { it.countryCode == "JP" }
        assertNotNull(japanServer)
        assertEquals("vg-jp-01.opengw.net", japanServer.hostname)
        assertEquals("219.100.37.240", japanServer.ipAddress)
        assertEquals("Japan", japanServer.country)
        assertEquals(24L, japanServer.ping)
        assertEquals(68_000_000L, japanServer.speed)
        assertTrue(japanServer.openVpnSupported)
        assertEquals("ZHVtbXlfb3BlbnZwbl9kYXRh", japanServer.openVpnConfigDataBase64)

        val nlServer = servers.find { it.countryCode == "NL" }
        assertNotNull(nlServer)
        assertEquals("Netherlands", nlServer.country)
        assertEquals(31L, nlServer.ping)
    }

    @Test
    fun testParseCsv_EmptyOrBlankReturnsEmptyList() {
        assertEquals(0, VpnGateParser.parseCsv("").size)
        assertEquals(0, VpnGateParser.parseCsv("   \n\n  ").size)
    }

    @Test
    fun testCountryEmojiHelper() {
        val servers = VpnGateParser.parseCsv(sampleCsv)
        val jp = servers.find { it.countryCode == "JP" }
        assertNotNull(jp)
        assertEquals("🇯🇵", jp.flagEmoji)

        val nl = servers.find { it.countryCode == "NL" }
        assertNotNull(nl)
        assertEquals("🇳🇱", nl.flagEmoji)
    }
}
