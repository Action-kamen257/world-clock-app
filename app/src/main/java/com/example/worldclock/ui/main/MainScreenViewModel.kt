package com.example.worldclock.ui.main

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.example.worldclock.data.City
import com.example.worldclock.data.CityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
data class WorldClockUiState(
    val selectedRegion: String = "All",
    val regions: List<String> = CityRepository.getAllRegions(),
    val heroCity: City? = null,
    val citiesForRegion: List<City> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
)

class MainScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorldClockUiState())
    val uiState: StateFlow<WorldClockUiState> = _uiState.asStateFlow()

    init {
        updateCities()
    }

    private fun updateCities() {
        val state = _uiState.value
        val featuredCity = CityRepository.getFeaturedCity()

        val cities = if (state.isSearchActive && state.searchQuery.isNotBlank()) {
            CityRepository.searchCities(state.searchQuery)
        } else {
            CityRepository.getCitiesByRegion(state.selectedRegion)
        }

        // Filter out featured city from list only if we are in "All" view to avoid duplicate display
        val filteredCities = cities
            .filter { it != featuredCity || state.selectedRegion != "All" }

        _uiState.update {
            it.copy(
                heroCity = featuredCity,
                citiesForRegion = filteredCities,
            )
        }
    }

    fun selectRegion(region: String) {
        _uiState.update {
            it.copy(selectedRegion = region, isSearchActive = false, searchQuery = "")
        }
        updateCities()
    }

    fun setSearchQuery(query: String) {
        _uiState.update {
            it.copy(searchQuery = query, isSearchActive = query.isNotBlank())
        }
        updateCities()
    }

    fun toggleSearch() {
        _uiState.update {
            it.copy(
                isSearchActive = !it.isSearchActive,
                searchQuery = if (it.isSearchActive) "" else it.searchQuery,
            )
        }
    }
}
