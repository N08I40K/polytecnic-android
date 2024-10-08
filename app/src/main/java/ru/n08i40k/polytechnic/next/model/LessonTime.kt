package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LessonTime(val start: Int, val end: Int) : Parcelable