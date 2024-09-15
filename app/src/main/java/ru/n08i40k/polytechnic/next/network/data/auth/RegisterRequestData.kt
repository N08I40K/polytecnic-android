package ru.n08i40k.polytechnic.next.network.data.auth

import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.model.UserRole

@Serializable
data class RegisterRequestData(
    val username: String,
    val password: String,
    val group: String,
    val role: UserRole
)