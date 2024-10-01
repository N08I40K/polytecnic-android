package ru.n08i40k.polytechnic.next.data.users.impl

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.data.profile.UsersMeRequest
import ru.n08i40k.polytechnic.next.network.tryFuture

class RemoteProfileRepository(private val context: Context) : ProfileRepository {
    override suspend fun getProfile(): MyResult<Profile> {
        return withContext(Dispatchers.IO) {
            tryFuture {
                UsersMeRequest(
                    context,
                    it,
                    it
                )
            }
        }
    }
}