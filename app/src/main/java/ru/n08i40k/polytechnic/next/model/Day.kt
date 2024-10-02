package ru.n08i40k.polytechnic.next.model

import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
class Day(
    val name: String,
    val nonNullIndices: ArrayList<Int>,
    val defaultIndices: ArrayList<Int>,
    val customIndices: ArrayList<Int>,
    val lessons: ArrayList<Lesson?>
)