package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Calendar

@Parcelize
@Suppress("unused")
@Serializable
class Day(
    val name: String,
    val nonNullIndices: ArrayList<Int>,
    val defaultIndices: ArrayList<Int>,
    val customIndices: ArrayList<Int>,
    val lessons: ArrayList<Lesson?>
) : Parcelable {
    fun getDistanceToNextByMinutes(from: Int): Map.Entry<Int, Int>? {
        val toIdx = lessons
            .map { if (it?.time == null) null else it.time.start }
            .indexOfFirst { if (it == null) false else it > from }

        if (toIdx == -1)
            return null

        return object : Map.Entry<Int, Int> {
            override val key: Int
                get() = toIdx
            override val value: Int
                get() = lessons[toIdx]!!.time!!.start - from
        }
    }

    fun getDistanceToNextByIdx(from: Int? = null): Map.Entry<Int, Int>? {
        val fromLesson = if (from != null) lessons[from] else null

        if (from != null && fromLesson?.time == null)
            throw NullPointerException("Lesson (by given index) and it's time should be non-null!")

        val fromTime =
            if (from != null)
                fromLesson!!.time!!.end
            else
                Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
                    .get(Calendar.MINUTE)

        return getDistanceToNextByMinutes(fromTime)
    }

    fun getCurrentLesson(): Map.Entry<Int, Lesson>? {
        val minutes = Calendar.getInstance()
            .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
            .get(Calendar.MINUTE)

        for (lessonIdx in 0..<lessons.size) {
            val lesson = lessons[lessonIdx] ?: continue

            if (lesson.time == null
                || minutes < lesson.time.start
                || minutes >= lesson.time.end
            )
                continue

            return object : Map.Entry<Int, Lesson> {
                override val key: Int
                    get() = lessonIdx
                override val value: Lesson
                    get() = lesson
            }
        }

        return null
    }
}