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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.android.volley.AuthFailureError
import com.android.volley.ClientError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.users.impl.FakeProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.network.data.auth.ChangePasswordRequest
import ru.n08i40k.polytechnic.next.network.data.auth.ChangePasswordRequestData

private enum class ChangePasswordError {
    INCORRECT_CURRENT_PASSWORD,
    SAME_PASSWORDS
}

private fun tryChangePassword(
    context: Context,
    oldPassword: String,
    newPassword: String,
    onError: (ChangePasswordError) -> Unit,
    onSuccess: () -> Unit
) {
    ChangePasswordRequest(ChangePasswordRequestData(oldPassword, newPassword), context, {
        onSuccess()
    }, {
        if (it is ClientError && it.networkResponse.statusCode == 409)
            onError(ChangePasswordError.SAME_PASSWORDS)
        else if (it is AuthFailureError)
            onError(ChangePasswordError.INCORRECT_CURRENT_PASSWORD)
        else throw it
    }).send()
}

@Preview(showBackground = true)
@Composable
internal fun ChangePasswordDialog(
    context: Context = LocalContext.current,
    profile: Profile = FakeProfileRepository.exampleProfile,
    onChange: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var oldPassword by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }

            var oldPasswordError by remember { mutableStateOf(false) }
            var newPasswordError by remember { mutableStateOf(false) }

            var processing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                val modifier = Modifier.fillMaxWidth()

                OutlinedTextField(
                    modifier = modifier,
                    value = oldPassword,
                    isError = oldPasswordError,
                    onValueChange = {
                        oldPassword = it
                        oldPasswordError = it.isEmpty()
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text(text = stringResource(R.string.old_password)) },
                    readOnly = processing
                )
                OutlinedTextField(
                    modifier = modifier,
                    value = newPassword,
                    isError = newPasswordError,
                    onValueChange = {
                        newPassword = it
                        newPasswordError = it.isEmpty() || newPassword == oldPassword
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text(text = stringResource(R.string.new_password)) },
                    readOnly = processing
                )

                val focusManager = LocalFocusManager.current
                Button(
                    modifier = modifier,
                    onClick = {
                        processing = true
                        focusManager.clearFocus()

                        tryChangePassword(
                            context = context,
                            oldPassword = oldPassword,
                            newPassword = newPassword,
                            onError = {
                                when (it) {
                                    ChangePasswordError.SAME_PASSWORDS -> {
                                        oldPasswordError = true
                                        newPasswordError = true
                                    }

                                    ChangePasswordError.INCORRECT_CURRENT_PASSWORD -> {
                                        oldPasswordError = true
                                    }
                                }

                                processing = false
                            },
                            onSuccess = onChange
                        )
                    },
                    enabled = !(newPasswordError || oldPasswordError || processing)
                ) {
                    Text(stringResource(R.string.change_password))
                }
            }
        }
    }
}