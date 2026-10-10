package com.example.worldclock.data

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object TimeProvider {

    private val timeFormatter12 = DateTimeFormatter.ofPattern("h:mm", Locale.ENGLISH)
    private val timeFormatterSeconds = DateTimeFormatter.ofPattern("ss", Locale.ENGLISH)
    private val amPmFormatter = DateTimeFormatter.ofPattern("a", Locale.ENGLISH)
    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH)
    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

    fun getCurrentTime(zoneId: String): ZonedDateTime {
        return ZonedDateTime.now(ZoneId.of(zoneId))
    }

    fun formatTime(zdt: ZonedDateTime): String {
        return zdt.format(timeFormatter12)
    }

    fun formatSeconds(zdt: ZonedDateTime): String {
        return zdt.format(timeFormatterSeconds)
    }

    fun formatAmPm(zdt: ZonedDateTime): String {
        return zdt.format(amPmFormatter)
    }

    fun formatDate(zdt: ZonedDateTime): String {
        return zdt.format(dateFormatter)
    }

    fun formatFullDate(zdt: ZonedDateTime): String {
        return zdt.format(fullDateFormatter)
    }

    fun getUtcOffset(zdt: ZonedDateTime): String {
        val offset = zdt.offset
        val totalSeconds = offset.totalSeconds
        val hours = totalSeconds / 3600
        val minutes = Math.abs((totalSeconds % 3600) / 60)
        return if (minutes == 0) {
            "UTC${if (hours >= 0) "+" else ""}$hours"
        } else {
            "UTC${if (hours >= 0) "+" else ""}$hours:${String.format("%02d", minutes)}"
        }
    }

    fun getTimeDifference(fromZoneId: String, toZoneId: String): String {
        val fromTime = ZonedDateTime.now(ZoneId.of(fromZoneId))
        val toTime = ZonedDateTime.now(ZoneId.of(toZoneId))
        val diffMinutes = ChronoUnit.MINUTES.between(
            fromTime.toLocalDateTime(),
            toTime.toLocalDateTime()
        )
        val hours = diffMinutes / 60
        val mins = Math.abs(diffMinutes % 60)
        return when {
            diffMinutes == 0L -> "Same time"
            mins == 0L -> "${if (hours > 0) "+" else ""}${hours}h"
            else -> "${if (hours >= 0) "+" else ""}${hours}h ${mins}m"
        }
    }

    fun isDaytime(zdt: ZonedDateTime): Boolean {
        val hour = zdt.hour
        return hour in 6..18
    }

    fun getDayPeriod(zdt: ZonedDateTime): DayPeriod {
        return when (zdt.hour) {
            in 5..6 -> DayPeriod.DAWN
            in 7..17 -> DayPeriod.DAY
            in 18..19 -> DayPeriod.DUSK
            else -> DayPeriod.NIGHT
        }
    }
}

enum class DayPeriod {
    DAWN, DAY, DUSK, NIGHT
}
