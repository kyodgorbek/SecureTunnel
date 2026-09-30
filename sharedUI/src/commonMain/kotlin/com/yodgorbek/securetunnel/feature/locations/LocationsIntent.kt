package com.yodgorbek.securetunnel.feature.locations

import com.yodgorbek.securetunnel.core.common.MviIntent
import com.yodgorbek.securetunnel.core.model.ServerSortOption
import com.yodgorbek.securetunnel.core.model.VpnServer

sealed interface LocationsIntent : MviIntent {
    data class UpdateSearchQuery(val query: String) : LocationsIntent
    data class ChangeSortOption(val option: ServerSortOption) : LocationsIntent
    data class ToggleFavoriteFilter(val enabled: Boolean) : LocationsIntent
    data class ToggleServerFavorite(val serverId: String) : LocationsIntent
    data class SelectServer(val server: VpnServer) : LocationsIntent
    data object Refresh : LocationsIntent
    data object ClearFilters : LocationsIntent
}
