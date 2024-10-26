package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.utils.InstantAsLongSerializer

@Parcelize
@Serializable
data class LessonTime(
    @Serializable(with = InstantAsLongSerializer::class)
    @SerialName("start")
    private val startMillis: Long,
    @Serializable(with = InstantAsLongSerializer::class)
    @SerialName("end")
    private val endMillis: Long
) : Parcelable {
    constructor(start: Instant, end: Instant) : this(
        start.toEpochMilliseconds(),
        end.toEpochMilliseconds()
    )

    val start: Instant
        get() = Instant.fromEpochMilliseconds(startMillis)
    val end: Instant
        get() = Instant.fromEpochMilliseconds(endMillis)

    companion object {
        fun fromLocalDateTime(start: LocalDateTime, end: LocalDateTime): LessonTime {
            val timeZone = TimeZone.currentSystemDefault()
            return LessonTime(
                start.toInstant(timeZone),
                end.toInstant(timeZone)
            )
        }
    }
}