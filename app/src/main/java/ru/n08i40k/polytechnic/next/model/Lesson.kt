package ru.n08i40k.polytechnic.next.model

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.limit

@Parcelize
@Serializable
data class Lesson(
    val type: LessonType,
    val defaultRange: List<Int>?,
    val name: String?,
    val time: LessonTime,
    val subGroups: List<SubGroup>
) : Parcelable {
    val duration: Int
        get() {
            val startMinutes = time.start.dayMinutes
            val endMinutes = time.end.dayMinutes

            return endMinutes - startMinutes
        }

    fun getNameAndCabinetsShort(context: Context): String {
        val limitedName = name!! limit 15

        val cabinets = subGroups.map { it.cabinet }

        if (cabinets.isEmpty())
            return limitedName

        if (cabinets.size == 1 && cabinets[0] == "с/з")
            return buildString {
                append(limitedName)
                append(" ")
                append(context.getString(R.string.in_gym_lc))
            }

        return buildString {
            append(limitedName)
            append(" ")
            append(
                context.getString(
                    R.string.in_cabinets_short_lc,
                    cabinets.joinToString(", ")
                )
            )
        }
    }
}