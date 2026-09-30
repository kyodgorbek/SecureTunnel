package com.yodgorbek.securetunnel.feature.locations

import com.yodgorbek.securetunnel.core.common.MviEffect

sealed interface LocationsEffect : MviEffect {
    data class ShowToast(val message: String) : LocationsEffect
    data object NavigateBack : LocationsEffect
}
