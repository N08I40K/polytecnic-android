package ru.n08i40k.polytechnic.next.data.users

import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): MyResult<Profile>

    suspend fun setFcmToken(token: String): MyResult<Unit>
}