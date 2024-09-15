package ru.n08i40k.polytechnic.next.network.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseData(val id: String, val accessToken: String)