package ru.n08i40k.polytechnic.next.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ru.n08i40k.polytechnic.next.R


@Preview(showBackground = true)
@Composable
internal fun LoginForm(
    appNavController: NavHostController = rememberNavController(),
    navController: NavHostController = rememberNavController(),
    onPendingSnackbar: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var loading by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val onClick = fun() {
        focusManager.clearFocus()

        if (username.length < 4) usernameError = true
        if (password.isEmpty()) passwordError = true

        if (usernameError || passwordError) return

        loading = true

        trySignIn(
            context,
            username,
            password,
            {
                loading = false

                val stringRes = when (it) {
                    SignInError.INVALID_CREDENTIALS -> {
                        usernameError = true
                        passwordError = true

                        R.string.invalid_credentials
                    }

                    SignInError.TIMED_OUT -> R.string.timed_out
                    SignInError.NO_CONNECTION -> R.string.no_connection
                    SignInError.APPLICATION_TOO_OLD -> R.string.app_too_old
                    SignInError.UNKNOWN -> R.string.unknown_error
                }

                onPendingSnackbar(context.getString(stringRes))
            },
            {
                loading = false

                appNavController.navigate("main") {
                    popUpTo("auth") { inclusive = true }
                }
            }
        )
    }

    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = stringResource(R.string.sign_in_title),
            modifier = Modifier.padding(10.dp),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.size(10.dp))

        OutlinedTextField(
            value = username,
            singleLine = true,
            onValueChange = {
                username = it
                usernameError = false
            },
            label = { Text(stringResource(R.string.username)) },
            isError = usernameError
        )

        OutlinedTextField(
            value = password,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {
                passwordError = false
                password = it
            },
            label = { Text(stringResource(R.string.password)) },
            isError = passwordError
        )

        TextButton(onClick = { navController.navigate("sign-up") }) {
            Text(text = stringResource(R.string.not_registered))
        }

        Button(
            enabled = !loading && !(usernameError || passwordError),
            onClick = onClick
        ) {
            Text(
                text = stringResource(R.string.proceed),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}