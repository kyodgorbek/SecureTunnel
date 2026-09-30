package com.yodgorbek.securetunnel.feature.locations

import androidx.lifecycle.viewModelScope
import com.yodgorbek.securetunnel.core.common.MviViewModel
import com.yodgorbek.securetunnel.core.model.ServerFilter
import com.yodgorbek.securetunnel.domain.usecase.FilterAndSortServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetActiveServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetRecommendedServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetVpnServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.RefreshVpnServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.SetSelectedServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class LocationsViewModel(
    private val getVpnServersUseCase: GetVpnServersUseCase,
    private val getActiveServerUseCase: GetActiveServerUseCase,
    private val refreshVpnServersUseCase: RefreshVpnServersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val setSelectedServerUseCase: SetSelectedServerUseCase,
    private val filterAndSortServersUseCase: FilterAndSortServersUseCase,
    private val getRecommendedServerUseCase: GetRecommendedServerUseCase
) : MviViewModel<LocationsIntent, LocationsState, LocationsEffect>(LocationsState()) {

    init {
        // Observe raw servers and active server, and recompute filtered/recommended servers
        combine(
            getVpnServersUseCase(),
            getActiveServerUseCase()
        ) { servers, active ->
            val rec = getRecommendedServerUseCase.calculateRecommended(servers)
            val filtered = filterAndSortServersUseCase(servers, currentState.filter)
            updateState {
                copy(
                    rawServers = servers,
                    displayedServers = filtered,
                    recommendedServer = rec,
                    selectedServer = active
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: LocationsIntent) {
        when (intent) {
            is LocationsIntent.UpdateSearchQuery -> {
                val newFilter = currentState.filter.copy(searchQuery = intent.query)
                applyFilter(newFilter)
            }
            is LocationsIntent.ChangeSortOption -> {
                val newFilter = currentState.filter.copy(sortOption = intent.option)
                applyFilter(newFilter)
            }
            is LocationsIntent.ToggleFavoriteFilter -> {
                val newFilter = currentState.filter.copy(favoritesOnly = intent.enabled)
                applyFilter(newFilter)
            }
            is LocationsIntent.ToggleServerFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(intent.serverId)
                }
            }
            is LocationsIntent.SelectServer -> {
                setSelectedServerUseCase(intent.server)
                sendEffect(LocationsEffect.NavigateBack)
            }
            is LocationsIntent.Refresh -> {
                viewModelScope.launch {
                    updateState { copy(isRefreshing = true) }
                    val res = refreshVpnServersUseCase()
                    updateState { copy(isRefreshing = false) }
                    res.onFailure {
                        sendEffect(LocationsEffect.ShowToast("Could not reach VPN Gate feed. Showing cached list."))
                    }
                }
            }
            is LocationsIntent.ClearFilters -> {
                applyFilter(ServerFilter())
            }
        }
    }

    private fun applyFilter(filter: ServerFilter) {
        val filtered = filterAndSortServersUseCase(currentState.rawServers, filter)
        updateState {
            copy(
                filter = filter,
                displayedServers = filtered
            )
        }
    }
}
