package ru.n08i40k.polytechnic.next.ui.auth

import android.content.Context
import com.android.volley.ClientError
import com.android.volley.NoConnectionError
import com.android.volley.TimeoutError
import com.google.firebase.logger.Logger
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.request.auth.AuthSignUp
import ru.n08i40k.polytechnic.next.network.unwrapException
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import java.util.concurrent.TimeoutException

internal enum class SignUpError {
    ALREADY_EXISTS,
    GROUP_DOES_NOT_EXISTS,
    TIMED_OUT,
    NO_CONNECTION,
    APPLICATION_TOO_OLD,
    UNKNOWN
}

internal fun trySignUp(
    context: Context,

    username: String,
    password: String,
    group: String,
    role: UserRole,

    onError: (SignUpError) -> Unit,
    onSuccess: () -> Unit,
) {
    AuthSignUp(
        AuthSignUp.RequestDto(
            username,
            password,
            group,
            role
        ), context, {
            runBlocking {
                context.settingsDataStore.updateData { currentSettings ->
                    currentSettings
                        .toBuilder()
                        .setUserId(it.id)
                        .setAccessToken(it.accessToken)
                        .setGroup(group)
                        .build()
                }
            }

            onSuccess()
        }, {
            val error = when (val exception = unwrapException(it)) {
                is TimeoutException -> SignUpError.TIMED_OUT
                is NoConnectionError -> SignUpError.NO_CONNECTION
                is TimeoutError -> SignUpError.UNKNOWN
                is ClientError -> {
                    when (exception.networkResponse.statusCode) {
                        400 -> SignUpError.APPLICATION_TOO_OLD
                        404 -> SignUpError.GROUP_DOES_NOT_EXISTS
                        409 -> SignUpError.ALREADY_EXISTS
                        else -> SignUpError.UNKNOWN
                    }
                }

                else -> SignUpError.UNKNOWN
            }

            if (error == SignUpError.UNKNOWN) {
                Logger.getLogger("tryRegister")
                    .error("Unknown exception while trying to register!", it)
            }

            onError(error)
        }).send()
}