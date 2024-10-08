package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Calendar

@Parcelize
@Serializable
data class Group(
    val name: String,
    val days: ArrayList<Day?>
) : Parcelable {
    fun getCurrentDay(): Map.Entry<Int, Day?>? {
        val currentDay = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2)

        if (currentDay < 0 || currentDay > days.size - 1)
            return null

        return object : Map.Entry<Int, Day?> {
            override val key: Int
                get() = currentDay
            override val value: Day?
                get() = days[currentDay]
        }
    }
}