package ru.n08i40k.polytechnic.next.model

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.utils.limit

@Parcelize
@Serializable
data class Lesson(
    val type: LessonType,
    val defaultIndex: Int,
    val name: String,
    val time: LessonTime,
    val cabinets: ArrayList<String>,
    val teacherNames: ArrayList<String>
) : Parcelable {
    val duration: Int
        get() {
            return time.end - time.start
        }

    fun getNameAndCabinetsShort(context: Context): String {
        val limitedName = name limit 15


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