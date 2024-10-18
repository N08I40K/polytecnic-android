package ru.n08i40k.polytechnic.next.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val accessToken: String,
    val username: String,
    val group: String,
    val role: UserRole
)
