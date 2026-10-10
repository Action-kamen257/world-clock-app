package com.example.worldclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.worldclock.theme.WorldClockTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Force 120Hz/high refresh rate mode if supported by hardware for buttery-smooth animations
    enableHighRefreshRate()

    enableEdgeToEdge()
    setContent {
      WorldClockTheme { Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { MainNavigation() } }
    }
  }

  private fun enableHighRefreshRate() {
    try {
      val window = window
      val layoutParams = window.attributes
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val display = display
        if (display != null) {
          val supportedModes = display.supportedModes
          val bestMode = supportedModes.maxByOrNull { it.refreshRate }
          if (bestMode != null && bestMode.refreshRate >= 90f) {
            layoutParams.preferredDisplayModeId = bestMode.modeId
          }
        }
      } else {
        layoutParams.preferredRefreshRate = 120f
      }
      window.attributes = layoutParams
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
