package ru.n08i40k.polytechnic.next.ui.main.profile

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.android.volley.ClientError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.users.impl.FakeProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.request.profile.ProfileChangeUsername

private enum class ChangeUsernameError {
    INCORRECT_LENGTH,
    ALREADY_EXISTS
}

private fun tryChangeUsername(
    context: Context,
    username: String,
    onError: (ChangeUsernameError) -> Unit,
    onSuccess: () -> Unit
) {
    ProfileChangeUsername(ProfileChangeUsername.RequestDto(username), context, {
        onSuccess()
    }, {
        if (it is ClientError && it.networkResponse.statusCode == 409)
            onError(ChangeUsernameError.ALREADY_EXISTS)
        if (it is ClientError && it.networkResponse.statusCode == 400)
            onError(ChangeUsernameError.INCORRECT_LENGTH)
        else throw it
    }).send()
}

@Preview(showBackground = true)
@Composable
internal fun ChangeUsernameDialog(
    context: Context = LocalContext.current,
    profile: Profile = FakeProfileRepository.exampleProfile,
    onChange: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var username by remember { mutableStateOf("") }
            var usernameError by remember { mutableStateOf(false) }

            var processing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                val modifier = Modifier.fillMaxWidth()

                OutlinedTextField(
                    modifier = modifier,
                    value = username,
                    isError = usernameError,
                    onValueChange = {
                        username = it
                        usernameError = it.isEmpty()
                                || username == profile.username
                                || username.length < 4
                                || username.length > 10
                    },
                    label = { Text(text = stringResource(R.string.username)) },
                    readOnly = processing
                )

                val focusManager = LocalFocusManager.current
                Button(
                    modifier = modifier,
                    onClick = {
                        processing = true
                        focusManager.clearFocus()

                        tryChangeUsername(
                            context = context,
                            username = username,
                            onError = {
                                usernameError = when (it) {
                                    ChangeUsernameError.ALREADY_EXISTS -> true
                                    ChangeUsernameError.INCORRECT_LENGTH -> true
                                }

                                processing = false
                            },
                            onSuccess = onChange
                        )
                    },
                    enabled = !(usernameError || processing)
                ) {
                    Text(stringResource(R.string.change_username))
                }
            }
        }
    }
}