package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LessonTime(
    val start: @RawValue Instant,
    val end: @RawValue Instant
) : Parcelable {
    companion object {
        fun fromLocalDateTime(start: LocalDateTime, end: LocalDateTime): LessonTime {
            val timeZone = TimeZone.currentSystemDefault()
            return LessonTime(start.toInstant(timeZone), end.toInstant(timeZone))
        }
    }
}