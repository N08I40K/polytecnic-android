package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.utils.getDayMinutes
import java.util.Calendar

@Parcelize
@Suppress("unused", "MemberVisibilityCanBePrivate")
@Serializable
class Day(
    val name: String,
    val nonNullIndices: ArrayList<Int>,
    val defaultIndices: ArrayList<Int>,
    val customIndices: ArrayList<Int>,
    val lessons: ArrayList<Lesson?>
) : Parcelable {
    fun distanceToNextByMinutes(from: Int): Pair<Int, Int>? {
        val toIdx = lessons
            .map { if (it?.time == null) null else it.time.start }
            .indexOfFirst { if (it == null) false else it > from }

        if (toIdx == -1)
            return null

        return Pair(toIdx, lessons[toIdx]!!.time.start - from)
    }

    fun distanceToNextByIdx(from: Int? = null): Pair<Int, Int>? {
        val fromLesson = if (from != null) lessons[from] else null

        if (from != null && fromLesson == null)
            throw NullPointerException("Lesson (by given index) and it's time should be non-null!")

        val fromTime =
            if (from != null)
                fromLesson!!.time.end
            else
                Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
                    .get(Calendar.MINUTE)

        return distanceToNextByMinutes(fromTime)
    }

    // current
    val currentIdx: Int?
        get() {
            val minutes = Calendar.getInstance().getDayMinutes()

            for (lessonIdx in nonNullIndices) {
                val lesson = lessons[lessonIdx]!!

                if (lesson.time.start <= minutes && minutes < lesson.time.end)
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
            return Pair(idx, lessons[idx]!!)
        }

    // first
    val firstIdx: Int?
        get() = nonNullIndices.getOrNull(0)

    val first: Lesson?
        get() {
            return lessons[firstIdx ?: return null]!!
        }

    val firstKV: Pair<Int, Lesson>?
        get() {
            val idx = firstIdx ?: return null
            return Pair(idx, lessons[idx]!!)
        }

    // last
    val lastIdx: Int?
        get() = nonNullIndices.getOrNull(nonNullIndices.size - 1)

    val last: Lesson?
        get() {
            return lessons[lastIdx ?: return null]!!
        }

    val lastKV: Pair<Int, Lesson>?
        get() {
            val idx = lastIdx ?: return null
            return Pair(idx, lessons[idx]!!)
        }
}