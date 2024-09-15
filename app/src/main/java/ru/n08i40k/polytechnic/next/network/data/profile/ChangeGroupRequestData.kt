package ru.n08i40k.polytechnic.next.network.data.profile

import kotlinx.serialization.Serializable

@Serializable
data class ChangeGroupRequestData(val group: String)