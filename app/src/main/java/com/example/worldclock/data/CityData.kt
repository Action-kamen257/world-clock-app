package com.example.worldclock.data

import androidx.compose.runtime.Immutable

@Immutable
data class City(
    val name: String,
    val country: String,
    val region: String,
    val zoneId: String,
    val emoji: String,
    val isFeatured: Boolean = false,
)



data class Region(
    val name: String,
    val emoji: String,
    val cities: List<City>,
)

object CityRepository {

    private val allCities = listOf(
        // Russia — Tomsk is featured/hero
        City("Tomsk", "Russia", "Russia", "Asia/Tomsk", "🇷🇺", isFeatured = true),
        City("Moscow", "Russia", "Russia", "Europe/Moscow", "🇷🇺"),
        City("St. Petersburg", "Russia", "Russia", "Europe/Moscow", "🇷🇺"),
        City("Novosibirsk", "Russia", "Russia", "Asia/Novosibirsk", "🇷🇺"),
        City("Vladivostok", "Russia", "Russia", "Asia/Vladivostok", "🇷🇺"),
        City("Yekaterinburg", "Russia", "Russia", "Asia/Yekaterinburg", "🇷🇺"),
        City("Krasnoyarsk", "Russia", "Russia", "Asia/Krasnoyarsk", "🇷🇺"),

        // India
        City("Mumbai", "India", "India", "Asia/Kolkata", "🇮🇳"),
        City("Delhi", "India", "India", "Asia/Kolkata", "🇮🇳"),
        City("Kolkata", "India", "India", "Asia/Kolkata", "🇮🇳"),
        City("Bangalore", "India", "India", "Asia/Kolkata", "🇮🇳"),
        City("Chennai", "India", "India", "Asia/Kolkata", "🇮🇳"),
        City("Hyderabad", "India", "India", "Asia/Kolkata", "🇮🇳"),

        // USA
        City("New York", "USA", "America", "America/New_York", "🇺🇸"),
        City("Los Angeles", "USA", "America", "America/Los_Angeles", "🇺🇸"),
        City("Chicago", "USA", "America", "America/Chicago", "🇺🇸"),
        City("Houston", "USA", "America", "America/Chicago", "🇺🇸"),
        City("Denver", "USA", "America", "America/Denver", "🇺🇸"),
        City("Phoenix", "USA", "America", "America/Phoenix", "🇺🇸"),
        City("Anchorage", "USA", "America", "America/Anchorage", "🇺🇸"),
        City("Honolulu", "USA", "America", "Pacific/Honolulu", "🇺🇸"),

        // Europe
        City("London", "UK", "Europe", "Europe/London", "🇬🇧"),
        City("Paris", "France", "Europe", "Europe/Paris", "🇫🇷"),
        City("Berlin", "Germany", "Europe", "Europe/Berlin", "🇩🇪"),
        City("Madrid", "Spain", "Europe", "Europe/Madrid", "🇪🇸"),
        City("Rome", "Italy", "Europe", "Europe/Rome", "🇮🇹"),
        City("Amsterdam", "Netherlands", "Europe", "Europe/Amsterdam", "🇳🇱"),
        City("Istanbul", "Turkey", "Europe", "Europe/Istanbul", "🇹🇷"),

        // Asia-Pacific
        City("Tokyo", "Japan", "Asia-Pacific", "Asia/Tokyo", "🇯🇵"),
        City("Beijing", "China", "Asia-Pacific", "Asia/Shanghai", "🇨🇳"),
        City("Shanghai", "China", "Asia-Pacific", "Asia/Shanghai", "🇨🇳"),
        City("Seoul", "South Korea", "Asia-Pacific", "Asia/Seoul", "🇰🇷"),
        City("Singapore", "Singapore", "Asia-Pacific", "Asia/Singapore", "🇸🇬"),
        City("Bangkok", "Thailand", "Asia-Pacific", "Asia/Bangkok", "🇹🇭"),
        City("Dubai", "UAE", "Asia-Pacific", "Asia/Dubai", "🇦🇪"),
        City("Hong Kong", "China", "Asia-Pacific", "Asia/Hong_Kong", "🇭🇰"),

        // Oceania
        City("Sydney", "Australia", "Oceania", "Australia/Sydney", "🇦🇺"),
        City("Melbourne", "Australia", "Oceania", "Australia/Melbourne", "🇦🇺"),
        City("Auckland", "New Zealand", "Oceania", "Pacific/Auckland", "🇳🇿"),
        City("Perth", "Australia", "Oceania", "Australia/Perth", "🇦🇺"),

        // Americas (South)
        City("São Paulo", "Brazil", "Americas", "America/Sao_Paulo", "🇧🇷"),
        City("Buenos Aires", "Argentina", "Americas", "America/Argentina/Buenos_Aires", "🇦🇷"),
        City("Mexico City", "Mexico", "Americas", "America/Mexico_City", "🇲🇽"),
        City("Toronto", "Canada", "Americas", "America/Toronto", "🇨🇦"),
        City("Vancouver", "Canada", "Americas", "America/Vancouver", "🇨🇦"),
    )

    fun getAllRegions(): List<String> {
        return listOf("All", "Russia", "India", "America", "Europe", "Asia-Pacific", "Oceania", "Americas")
    }

    fun getCitiesByRegion(region: String): List<City> {
        if (region == "All") return allCities
        return allCities.filter { it.region == region }
    }

    fun getFeaturedCity(): City {
        return allCities.first { it.isFeatured }
    }

    fun getAllCities(): List<City> = allCities

    fun searchCities(query: String): List<City> {
        if (query.isBlank()) return allCities
        val lowerQuery = query.lowercase()
        return allCities.filter {
            it.name.lowercase().contains(lowerQuery) ||
                it.country.lowercase().contains(lowerQuery) ||
                it.region.lowercase().contains(lowerQuery)
        }
    }
}
