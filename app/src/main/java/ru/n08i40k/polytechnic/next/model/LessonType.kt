package ru.n08i40k.polytechnic.next.model

import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.utils.EnumAsIntSerializer

private class LessonTypeIntSerializer : EnumAsIntSerializer<LessonType>(
    "LessonType",
    { it.value },
    { v -> LessonType.entries.first { it.value == v } }
)

@Serializable(with = LessonTypeIntSerializer::class)
enum class LessonType(val value: Int) {
    DEFAULT(0),
    ADDITIONAL(1),
    BREAK(2)
}