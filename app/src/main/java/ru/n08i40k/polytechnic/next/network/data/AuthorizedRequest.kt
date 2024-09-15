package ru.n08i40k.polytechnic.next.network.data

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.network.RequestBase
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import ru.n08i40k.polytechnic.next.ui.model.profileViewModel

open class AuthorizedRequest(
    context: Context,
    method: Int,
    url: String?,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener?,
    private val canBeUnauthorized: Boolean = false
) : RequestBase(
    context,
    method,
    url,
    listener,
    Response.ErrorListener {
        if (!canBeUnauthorized && it is AuthFailureError) {
            runBlocking {
                context.settingsDataStore.updateData { currentSettings ->
                    currentSettings.toBuilder().setUserId("")
                        .setAccessToken("").build()
                }
            }
            context.profileViewModel!!.onUnauthorized()
        }

        errorListener?.onErrorResponse(it)
    }) {
    override fun getHeaders(): MutableMap<String, String> {
        val accessToken = runBlocking {
            context.settingsDataStore.data.map { settings -> settings.accessToken }.first()
        }

        if (accessToken.isEmpty())
            context.profileViewModel!!.onUnauthorized()

        val headers = super.getHeaders()
        headers["Authorization"] = "Bearer $accessToken"

        return headers
    }
}