package ru.n08i40k.polytechnic.next.data.users.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.request.fcm.FcmSetToken
import ru.n08i40k.polytechnic.next.network.request.profile.ProfileMe
import ru.n08i40k.polytechnic.next.network.tryFuture

class RemoteProfileRepository(private val context: Context) : ProfileRepository {
    override suspend fun getProfile(): MyResult<Profile> =
        withContext(Dispatchers.IO) {
            tryFuture { ProfileMe(context, it, it) }
        }

    override suspend fun setFcmToken(token: String): MyResult<Unit> =
        withContext(Dispatchers.IO) {
            tryFuture { FcmSetToken(context, token, it, it) }
        }
}