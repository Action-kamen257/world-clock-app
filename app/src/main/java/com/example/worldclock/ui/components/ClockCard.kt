package com.example.worldclock.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.worldclock.theme.Amber
import com.example.worldclock.theme.DarkBorder
import com.example.worldclock.theme.DarkCardBackground
import com.example.worldclock.theme.DawnDuskColor
import com.example.worldclock.theme.DayColor
import com.example.worldclock.theme.NightColor
import com.example.worldclock.theme.TextPrimary
import com.example.worldclock.theme.TextSecondary
import com.example.worldclock.theme.TextTertiary
import com.example.worldclock.theme.Teal
import com.example.worldclock.data.City
import com.example.worldclock.data.DayPeriod
import com.example.worldclock.data.TimeProvider
import kotlinx.coroutines.delay

@Composable
fun ClockCard(
    city: City,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    // Local synchronized clock ticking
    var currentTime by remember { mutableStateOf(TimeProvider.getCurrentTime(city.zoneId)) }
    LaunchedEffect(city.zoneId) {
        while (true) {
            // Align delay with the exact start of the next second
            val nextTickDelay = 1000L - (System.currentTimeMillis() % 1000L)
            delay(nextTickDelay)
            currentTime = TimeProvider.getCurrentTime(city.zoneId)
        }
    }

    val dayPeriod = TimeProvider.getDayPeriod(currentTime)
    val indicatorColor by animateColorAsState(
        targetValue = when (dayPeriod) {
            DayPeriod.DAY -> DayColor
            DayPeriod.NIGHT -> NightColor
            DayPeriod.DAWN, DayPeriod.DUSK -> DawnDuskColor
        },
        animationSpec = tween(500),
        label = "dayIndicator",
    )

    val dayIcon = when (dayPeriod) {
        DayPeriod.DAY -> "☀️"
        DayPeriod.NIGHT -> "🌙"
        DayPeriod.DAWN -> "🌅"
        DayPeriod.DUSK -> "🌇"
    }

    val formattedTime = TimeProvider.formatTime(currentTime)
    val seconds = TimeProvider.formatSeconds(currentTime)
    val amPm = TimeProvider.formatAmPm(currentTime)
    val date = TimeProvider.formatDate(currentTime)
    val utcOffset = TimeProvider.getUtcOffset(currentTime)
    
    val localZoneId = remember { java.time.ZoneId.systemDefault().id }
    val timeDiffFromLocal = TimeProvider.getTimeDifference(localZoneId, city.zoneId)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick { isExpanded = !isExpanded }
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCardBackground)
            .border(
                width = 1.dp,
                color = DarkBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.65f, // Bouncy spring expand
                    stiffness = 180f
                )
            )
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Left side: city info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = city.emoji,
                            fontSize = 16.sp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = city.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = city.country,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•",
                            color = TextTertiary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = utcOffset,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextTertiary,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dayIcon,
                            fontSize = 12.sp,
                        )
                    }
                }

                // Right side: time display
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Light,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Column(
                            modifier = Modifier.padding(bottom = 4.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Text(
                                text = seconds,
                                style = MaterialTheme.typography.labelMedium,
                                color = Amber.copy(alpha = 0.8f),
                            )
                            Text(
                                text = amPm,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(indicatorColor),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = timeDiffFromLocal,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }
            }

            // Expanded details block
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = DarkBorder.copy(alpha = 0.3f),
                    thickness = 1.dp,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DetailRow(label = "Timezone ID", value = city.zoneId, color = Teal)
                    DetailRow(label = "Date (Full)", value = TimeProvider.formatFullDate(currentTime), color = TextPrimary)
                    DetailRow(
                        label = "Day/Night Cycle",
                        value = when (dayPeriod) {
                            DayPeriod.DAY -> "☀️ Daytime (Light)"
                            DayPeriod.NIGHT -> "🌙 Nighttime (Dark)"
                            DayPeriod.DAWN -> "🌅 Dawn Transition"
                            DayPeriod.DUSK -> "🌇 Dusk Transition"
                        },
                        color = when (dayPeriod) {
                            DayPeriod.DAY -> DayColor
                            DayPeriod.NIGHT -> NightColor
                            DayPeriod.DAWN, DayPeriod.DUSK -> DawnDuskColor
                        }
                    )
                    DetailRow(
                        label = "Local Offset Difference",
                        value = if (timeDiffFromLocal == "Same time") "Identical to your phone clock" else "$timeDiffFromLocal relative to local time",
                        color = Amber,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    color: Color = TextPrimary,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium,
        )
    }
}
