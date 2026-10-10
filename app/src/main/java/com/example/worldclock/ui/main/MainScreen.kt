package com.example.worldclock.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worldclock.theme.Amber
import com.example.worldclock.theme.DarkBackground
import com.example.worldclock.theme.DarkCardBackground
import com.example.worldclock.theme.TextPrimary
import com.example.worldclock.theme.TextSecondary
import com.example.worldclock.theme.TextTertiary
import com.example.worldclock.theme.Teal
import com.example.worldclock.data.City
import com.example.worldclock.data.TimeProvider
import com.example.worldclock.ui.components.AnalogClock
import com.example.worldclock.ui.components.ClockCard
import com.example.worldclock.ui.components.RegionSelector
import com.example.worldclock.ui.components.bounceClick
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "World Clock",
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary,
                        )
                        Text(
                            text = "Track time across the globe",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary,
                        )
                    }
                    // Bouncy Search Toggle Button
                    Box(
                        modifier = Modifier
                            .bounceClick { viewModel.toggleSearch() }
                            .padding(8.dp),
                    ) {
                        Icon(
                            imageVector = if (uiState.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary,
                        )
                    }
                }
            }

            // Search bar with smooth spring entrance/exit physics
            item {
                AnimatedVisibility(
                    visible = uiState.isSearchActive,
                    enter = fadeIn(animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f)) +
                            slideInVertically(
                                initialOffsetY = { -it / 2 },
                                animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f)
                            ),
                    exit = fadeOut(animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f)) +
                           slideOutVertically(
                               targetOffsetY = { -it / 2 },
                               animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f)
                           ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCardBackground, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        if (uiState.searchQuery.isEmpty()) {
                            Text(
                                text = "Search cities, countries...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextTertiary,
                            )
                        }
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                            cursorBrush = SolidColor(Amber),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            // Hero clock section (Tomsk)
            uiState.heroCity?.let { hero ->
                item {
                    HeroClockSection(hero)
                }
            }

            // Region selector
            item {
                RegionSelector(
                    regions = uiState.regions,
                    selectedRegion = uiState.selectedRegion,
                    onRegionSelected = { viewModel.selectRegion(it) },
                )
            }

            // City label
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (uiState.isSearchActive) "Search Results" else uiState.selectedRegion,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                    )
                    Text(
                        text = "${uiState.citiesForRegion.size} cities",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                    )
                }
            }

            // City clocks list with custom bouncy spring item animation specs
            items(
                items = uiState.citiesForRegion,
                key = { it.name + it.country },
            ) { city ->
                ClockCard(
                    city = city,
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            dampingRatio = 0.62f, // Soft bouncy layout transitions
                            stiffness = 160f
                        ),
                        fadeInSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = 180f
                        ),
                        fadeOutSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = 180f
                        )
                    ),
                )
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HeroClockSection(hero: City) {
    // Local ticking implementation for Hero Clock
    var currentTime by remember { mutableStateOf(TimeProvider.getCurrentTime(hero.zoneId)) }
    LaunchedEffect(hero.zoneId) {
        while (true) {
            val nextTickDelay = 1000L - (System.currentTimeMillis() % 1000L)
            delay(nextTickDelay)
            currentTime = TimeProvider.getCurrentTime(hero.zoneId)
        }
    }

    val formattedTime = TimeProvider.formatTime(currentTime)
    val seconds = TimeProvider.formatSeconds(currentTime)
    val amPm = TimeProvider.formatAmPm(currentTime)
    val date = TimeProvider.formatDate(currentTime)
    val utcOffset = TimeProvider.getUtcOffset(currentTime)
    val isDaytime = TimeProvider.isDaytime(currentTime)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DarkCardBackground,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // City name with star
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "⭐",
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = hero.name,
                style = MaterialTheme.typography.headlineMedium,
                color = Amber,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = hero.emoji,
                fontSize = 16.sp,
            )
        }
        Text(
            text = hero.country,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Analog clock
        AnalogClock(
            time = currentTime,
            size = 180.dp,
            accentColor = Amber,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Digital time display
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Thin,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(
                modifier = Modifier.padding(bottom = 10.dp),
            ) {
                Text(
                    text = seconds,
                    style = MaterialTheme.typography.titleMedium,
                    color = Amber,
                )
                Text(
                    text = amPm,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date and UTC
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Text(
                text = "•",
                color = TextTertiary,
            )
            Text(
                text = utcOffset,
                style = MaterialTheme.typography.labelMedium,
                color = Teal,
            )
            Text(
                text = "•",
                color = TextTertiary,
            )
            Text(
                text = if (isDaytime) "☀️ Day" else "🌙 Night",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
        }
    }
}
