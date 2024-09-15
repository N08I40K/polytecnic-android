package ru.n08i40k.polytechnic.next.data.users.impl

import android.content.Context
import com.android.volley.toolbox.RequestFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.data.profile.UsersMeRequest

class RemoteProfileRepository(private val context: Context) : ProfileRepository {
    override suspend fun getProfile(): MyResult<Profile> {
        return withContext(Dispatchers.IO) {
            val responseFuture = RequestFuture.newFuture<Profile>()
            UsersMeRequest(
                context,
                responseFuture,
                responseFuture
            ).send()

            try {
                MyResult.Success(responseFuture.get())
            } catch (exception: Exception) {
                MyResult.Failure(exception)
            }
        }
    }
}