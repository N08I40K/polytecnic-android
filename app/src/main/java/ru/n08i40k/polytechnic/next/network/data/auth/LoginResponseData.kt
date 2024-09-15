package ru.n08i40k.polytechnic.next.network.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseData(val id: String, val accessToken: String)