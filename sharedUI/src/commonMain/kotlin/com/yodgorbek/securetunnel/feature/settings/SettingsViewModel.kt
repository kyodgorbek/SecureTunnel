package com.yodgorbek.securetunnel.feature.settings

import androidx.lifecycle.viewModelScope
import com.yodgorbek.securetunnel.core.common.MviViewModel
import com.yodgorbek.securetunnel.domain.usecase.GetSettingsUseCase
import com.yodgorbek.securetunnel.domain.usecase.UpdateSettingsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : MviViewModel<SettingsIntent, SettingsState, SettingsEffect>(SettingsState()) {

    init {
        // Observe persistent app settings
        getSettingsUseCase().onEach { settings ->
            updateState { copy(settings = settings) }
        }.launchIn(viewModelScope)
    }

    override fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleAutoConnect -> {
                viewModelScope.launch {
                    updateSettingsUseCase { copy(autoConnect = intent.enabled) }
                }
            }
            is SettingsIntent.ToggleKillSwitch -> {
                viewModelScope.launch {
                    updateSettingsUseCase { copy(killSwitchEnabled = intent.enabled) }
                }
            }
            is SettingsIntent.SetThemeMode -> {
                viewModelScope.launch {
                    updateSettingsUseCase { copy(themeMode = intent.mode) }
                }
            }
            is SettingsIntent.UpdateGroqApiKey -> {
                viewModelScope.launch {
                    updateSettingsUseCase { copy(groqApiKey = intent.key) }
                }
            }
            is SettingsIntent.UpdateCustomDns -> {
                viewModelScope.launch {
                    updateSettingsUseCase { copy(customDnsPrimary = intent.primary, customDnsSecondary = intent.secondary) }
                }
            }
        }
    }
}
