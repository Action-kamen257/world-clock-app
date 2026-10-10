package com.example.worldclock.widget

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import com.example.worldclock.R
import com.example.worldclock.data.CityRepository
import com.example.worldclock.data.TimeProvider

class WorldClockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WorldClockWidgetContent()
        }
    }

    @Composable
    private fun WorldClockWidgetContent() {
        // Load exactly two cities: Tomsk (featured) and Mumbai
        val tomsk = CityRepository.getFeaturedCity()
        val mumbai = CityRepository.getAllCities().firstOrNull { it.name == "Mumbai" }
            ?: CityRepository.getCitiesByRegion("India").first()
            
        val widgetCities = listOf(tomsk, mumbai)

        val widgetBgColor = Color(0xFF0D1117) // Dark Navy Background
        val amberColor = 0xFFF0B429.toInt()
        val textPrimary = 0xFFE6EDF3.toInt()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(widgetBgColor))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Minimal 2-column layout
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                widgetCities.forEachIndexed { index, city ->
                    val context = LocalContext.current
                    val zdt = TimeProvider.getCurrentTime(city.zoneId)
                    val utcOffset = TimeProvider.getUtcOffset(zdt)
                    
                    val remoteViews = RemoteViews(context.packageName, R.layout.widget_clock_item).apply {
                        // Set City Name
                        setTextViewText(R.id.cityName, "${city.emoji} ${city.name}")
                        setTextColor(R.id.cityName, if (city.isFeatured) amberColor else textPrimary)
                        
                        // Set TextClock TimeZones
                        setString(R.id.timeClock, "setTimeZone", city.zoneId)
                        setString(R.id.ampmClock, "setTimeZone", city.zoneId)
                        
                        // Set UTC Offset
                        setTextViewText(R.id.offsetText, utcOffset)
                    }

                    AndroidRemoteViews(
                        remoteViews = remoteViews,
                        modifier = GlanceModifier
                            .defaultWeight()
                            .padding(horizontal = 4.dp)
                    )

                    // Divider spacing
                    if (index == 0) {
                        Spacer(modifier = GlanceModifier.width(16.dp))
                    }
                }
            }
        }
    }
}

class WorldClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WorldClockWidget()
}
