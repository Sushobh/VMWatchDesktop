package com.sushobh.vmwatch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VmListViewModel {

    private val _viewModels = MutableStateFlow(
        listOf(
            "HomeViewModel",
            "SearchViewModel",
            "DetailsViewModel",
            "ProfileViewModel",
            "SettingsViewModel",
            "LoginViewModel",
            "AuthViewModel",
            "OnboardingViewModel"
        )
    )
    val viewModels: StateFlow<List<String>> = _viewModels

    private val _selectedViewModel = MutableStateFlow<String?>(null)
    val selectedViewModel: StateFlow<String?> = _selectedViewModel

    fun onViewModelSelected(vmName: String) {
        _selectedViewModel.value = vmName
    }
}