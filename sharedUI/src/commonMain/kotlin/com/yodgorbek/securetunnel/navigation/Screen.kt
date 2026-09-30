package com.yodgorbek.securetunnel.navigation

/**
 * Type-safe navigation destinations for SecureTunnel.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: String,
    val isBottomBarTab: Boolean = true
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        icon = "🛡️",
        isBottomBarTab = true
    )

    data object Locations : Screen(
        route = "locations",
        title = "Locations",
        icon = "🌐",
        isBottomBarTab = true
    )

    data object Assistant : Screen(
        route = "assistant",
        title = "Assistant",
        icon = "🤖",
        isBottomBarTab = true
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = "⚙️",
        isBottomBarTab = true
    )

    data object Diagnostics : Screen(
        route = "diagnostics",
        title = "Diagnostics",
        icon = "🩺",
        isBottomBarTab = false
    )

    data object History : Screen(
        route = "history",
        title = "History",
        icon = "📜",
        isBottomBarTab = false
    )

    companion object {
        val bottomBarTabs = listOf(Home, Locations, Assistant, Settings)
    }
}
