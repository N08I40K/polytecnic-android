package ru.n08i40k.polytechnic.next.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val type: LessonType,
    val defaultIndex: Int,
    val name: String,
    val time: LessonTime?,
    val cabinets: ArrayList<String>,
    val teacherNames: ArrayList<String>
)