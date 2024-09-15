package ru.n08i40k.polytechnic.next.network.data.profile

import kotlinx.serialization.Serializable

@Serializable
data class ChangeUsernameRequestData(val username: String)