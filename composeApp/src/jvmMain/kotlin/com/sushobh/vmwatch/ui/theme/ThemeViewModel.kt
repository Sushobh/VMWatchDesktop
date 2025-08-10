package com.sushobh.vmwatch.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel {
    private val _themes = MutableStateFlow(AppTheme.values().toList())
    val themes: StateFlow<List<AppTheme>> = _themes

    private val _currentTheme = MutableStateFlow(AppTheme.DEFAULT_LIGHT)
    val currentTheme: StateFlow<AppTheme> = _currentTheme

    fun onThemeSelected(theme: AppTheme) {
        _currentTheme.value = theme
    }
}
