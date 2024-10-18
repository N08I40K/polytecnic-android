package ru.n08i40k.polytechnic.next.data.users.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.users.ProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.model.UserRole

class FakeProfileRepository : ProfileRepository {
    private var counter = 0

    companion object {
        val exampleProfile =
            Profile(
                "66db32d24030a07e02d974c5",
                "128735612876",
                "n08i40k",
                "ИС-214/23",
                UserRole.STUDENT
            )
    }

    override suspend fun getProfile(): MyResult<Profile> {
        return withContext(Dispatchers.IO) {
            delay(1500)

            if (counter++ % 3 == 0)
                MyResult.Failure(Exception())
            else
                MyResult.Success(exampleProfile)
        }
    }

    override suspend fun setFcmToken(token: String): MyResult<Unit> {
        return MyResult.Success(Unit)
    }
}