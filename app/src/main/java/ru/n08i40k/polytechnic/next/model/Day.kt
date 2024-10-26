package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.utils.InstantAsLongSerializer
import ru.n08i40k.polytechnic.next.utils.dateTime
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.now


@Parcelize
@Suppress("unused", "MemberVisibilityCanBePrivate")
@Serializable
class Day(
    val name: String,
    @Serializable(with = InstantAsLongSerializer::class)
    @SerialName("date")
    private val dateMillis: Long,
    val lessons: List<Lesson>
) : Parcelable {
    constructor(name: String, date: Instant, lessons: List<Lesson>) : this(
        name, date.toEpochMilliseconds(), lessons
    )

    val date: Instant
        get() = Instant.fromEpochMilliseconds(dateMillis)

    fun distanceToNextByLocalDateTime(from: LocalDateTime): Pair<Int, Int>? {
        val toIdx = lessons
            .map { it.time.start }
            .indexOfFirst { it.dateTime > from }

        if (toIdx == -1)
            return null

        return Pair(toIdx, lessons[toIdx].time.start.dayMinutes - from.dayMinutes)
    }

    fun distanceToNextByIdx(from: Int? = null): Pair<Int, Int>? {
        val fromLesson = if (from != null) lessons[from] else null

        if (from != null && fromLesson == null)
            throw NullPointerException("Lesson (by given index) and it's time should be non-null!")

        val fromTime =
            if (from != null)
                fromLesson!!.time.end.dateTime
            else
                LocalDateTime.now()

        return distanceToNextByLocalDateTime(fromTime)
    }

    // current
    val currentIdx: Int?
        get() {
            val now = LocalDateTime.now()

            for (lessonIdx in lessons.indices) {
                val lesson = lessons[lessonIdx]

                if (lesson.time.start.dateTime <= now && now < lesson.time.end.dateTime)
                    return lessonIdx
            }

            return null
        }

    val current: Lesson?
        get() {
            return lessons[currentIdx ?: return null]
        }

    val currentKV: Pair<Int, Lesson>?
        get() {
            val idx = currentIdx ?: return null
            return Pair(idx, lessons[idx])
        }

    // first
    val firstIdx: Int?
        get() = if (lessons.isEmpty()) null else 0

    val first: Lesson?
        get() {
            return lessons[firstIdx ?: return null]
        }

    val firstKV: Pair<Int, Lesson>?
        get() {
            val idx = firstIdx ?: return null
            return Pair(idx, lessons[idx])
        }

    // last
    val lastIdx: Int?
        get() = if (lessons.isEmpty()) null else lessons.size - 1

    val last: Lesson?
        get() {
            return lessons[lastIdx ?: return null]
        }

    val lastKV: Pair<Int, Lesson>?
        get() {
            val idx = lastIdx ?: return null
            return Pair(idx, lessons[idx])
        }
}
