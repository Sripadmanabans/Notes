package com.sripad.notes.utils

import org.joda.time.DateTime
import org.joda.time.LocalDateTime

/**
 * Assumes Monday as the start of the week based on ISO standard.
 * Formats text based on certain criteria.
 * - If time is between mid-night and current time returns "Today at {Time in 12hr format}". Eg: Today at 12:03 PM
 * - If time is within the same calendar week as current time returns "{Day of Week} at {Time in 12hr format}". Eg: Wednesday at 03:07 PM
 * - If it does not fall under the above mentioned criteria it returns "{Month of year} {Day of month} at {Time in 12hr format}". Eg: Dec
 * 31 at 06:30 AM
 */
internal fun LocalDateTime.getFormattedText(): String {
    val modifiedDateTime = toDateTime()
    val today = DateTime.now().withTimeAtStartOfDay()
    val time = toString("hh:mm a")
    return when {
        today <= modifiedDateTime -> "Today at $time"
        modifiedDateTime.weekOfWeekyear == today.weekOfWeekyear -> "${toString("EEEE")} at $time"
        else -> "${toString("MMM dd")} at $time"
    }
}