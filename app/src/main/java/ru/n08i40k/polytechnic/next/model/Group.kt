package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Calendar

@Suppress("MemberVisibilityCanBePrivate")
@Parcelize
@Serializable
data class Group(
    val name: String,
    val days: List<Day>
) : Parcelable {
    val currentIdx: Int?
        get() {
            val currentDay = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2)

            if (currentDay < 0 || currentDay > days.size - 1)
                return null

            return currentDay
        }

    val current: Day?
        get() {
            return days.getOrNull(currentIdx ?: return null)
        }

    val currentKV: Pair<Int, Day>?
        get() {
            val idx = currentIdx ?: return null
            return Pair(idx, days[idx])
        }
}