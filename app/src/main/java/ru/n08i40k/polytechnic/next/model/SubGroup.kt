package ru.n08i40k.polytechnic.next.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class SubGroup(
    val number: Int,
    val cabinet: String,
    val teacher: String
) : Parcelable
