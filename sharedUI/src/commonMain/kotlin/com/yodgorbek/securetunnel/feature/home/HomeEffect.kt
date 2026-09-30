package com.yodgorbek.securetunnel.feature.home

import com.yodgorbek.securetunnel.core.common.MviEffect

sealed interface HomeEffect : MviEffect {
    data class ShowToast(val message: String) : HomeEffect
    data object NavigateToLocations : HomeEffect
}
