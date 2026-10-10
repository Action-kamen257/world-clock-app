package com.example.worldclock.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.worldclock.theme.Amber
import com.example.worldclock.theme.AmberDark
import com.example.worldclock.theme.DarkBorder
import com.example.worldclock.theme.DarkCardBackground
import com.example.worldclock.theme.TextSecondary

@Composable
fun RegionSelector(
    regions: List<String>,
    selectedRegion: String,
    onRegionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        regions.forEach { region ->
            val isSelected = region == selectedRegion
            val regionEmoji = getRegionEmoji(region)

            val chipBackground by animateColorAsState(
                targetValue = if (isSelected) AmberDark.copy(alpha = 0.2f) else DarkCardBackground,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 150f),
                label = "chipBg",
            )

            val chipBorderColor by animateColorAsState(
                targetValue = if (isSelected) Amber.copy(alpha = 0.6f) else DarkBorder.copy(alpha = 0.5f),
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 150f),
                label = "chipBorderColor",
            )

            // Custom bouncy chip component
            Box(
                modifier = Modifier
                    .bounceClick { onRegionSelected(region) }
                    .background(chipBackground, RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = chipBorderColor,
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "$regionEmoji $region",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) Amber else TextSecondary,
                )
            }
        }
    }
}

private fun getRegionEmoji(region: String): String {
    return when (region) {
        "All" -> "🌍"
        "Russia" -> "🇷🇺"
        "India" -> "🇮🇳"
        "America" -> "🇺🇸"
        "Europe" -> "🇪🇺"
        "Asia-Pacific" -> "🌏"
        "Oceania" -> "🌊"
        "Americas" -> "🌎"
        else -> "🌐"
    }
}
