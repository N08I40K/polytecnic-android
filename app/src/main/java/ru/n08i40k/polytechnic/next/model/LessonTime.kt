package ru.n08i40k.polytechnic.next.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonTime(val start: Int, val end: Int)