package ru.n08i40k.polytechnic.next.ui.auth

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.ClientError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.google.firebase.logger.Logger
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignIn
import ru.n08i40k.polytechnic.next.network.unwrapException
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import java.util.concurrent.TimeoutException

internal enum class SignInError {
    INVALID_CREDENTIALS,
    TIMED_OUT,
    NO_CONNECTION,
    APPLICATION_TOO_OLD,
    UNKNOWN
}

internal fun trySignIn(
    context: Context,

    username: String,
    password: String,

    onError: (SignInError) -> Unit,
    onSuccess: () -> Unit,
) {
    AuthSignIn(AuthSignIn.RequestDto(username, password), context, {
        runBlocking {
            context.settingsDataStore.updateData { currentSettings ->
                currentSettings
                    .toBuilder()
                    .setUserId(it.id)
                    .setAccessToken(it.accessToken)
                    .setGroup(it.group)
                    .build()
            }
        }

        onSuccess()
    }, {
        val error = when (val exception = unwrapException(it)) {
            is TimeoutException -> SignInError.TIMED_OUT
            is TimeoutError -> SignInError.TIMED_OUT
            is NoConnectionError -> SignInError.NO_CONNECTION
            is AuthFailureError -> SignInError.INVALID_CREDENTIALS
            is ClientError -> {
                if (exception.networkResponse.statusCode == 400)
                    SignInError.APPLICATION_TOO_OLD
                else
                    SignInError.UNKNOWN
            }

            else -> SignInError.UNKNOWN
        }

        if (error == SignInError.UNKNOWN) {
            Logger.getLogger("tryLogin")
                .error("Unknown exception while trying to login!", it)
        }

        onError(error)
    }).send()
}
