@file:Suppress("unused")

package ru.n08i40k.polytechnic.next.model

import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.utils.EnumAsIntSerializer

@Serializable
data class LessonTime(val start: Int, val end: Int)

private class LessonTypeIntSerializer : EnumAsIntSerializer<LessonType>(
    "LessonType",
    { it.value },
    { v -> LessonType.entries.first { it.value == v } }
)

@Serializable(with = LessonTypeIntSerializer::class)
enum class LessonType(val value: Int) {
    DEFAULT(0), CUSTOM(1)
}

@Serializable
data class Lesson(
    val type: LessonType,
    val defaultIndex: Int,
    val name: String,
    val time: LessonTime?,
    val cabinets: ArrayList<String>,
    val teacherNames: ArrayList<String>
)

@Serializable
class Day(
    val name: String,
    val nonNullIndices: ArrayList<Int>,
    val defaultIndices: ArrayList<Int>,
    val customIndices: ArrayList<Int>,
    val lessons: ArrayList<Lesson?>
)

@Serializable
class Group(
    val name: String,
    val days: ArrayList<Day?>
)