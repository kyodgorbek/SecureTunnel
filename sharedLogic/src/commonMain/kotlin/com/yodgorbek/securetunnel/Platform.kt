package com.yodgorbek.securetunnel

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform