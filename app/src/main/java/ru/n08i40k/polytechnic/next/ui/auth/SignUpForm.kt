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
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.ui.widgets.GroupSelector
import ru.n08i40k.polytechnic.next.ui.widgets.RoleSelector
import ru.n08i40k.polytechnic.next.ui.widgets.TeacherNameSelector


@Preview(showBackground = true)
@Composable
internal fun RegisterForm(
    appNavController: NavHostController = rememberNavController(),
    navController: NavHostController = rememberNavController(),
    onPendingSnackbar: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var loading by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var group by remember { mutableStateOf<String?>(null) }
    var role by remember { mutableStateOf(UserRole.STUDENT) }

    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var groupError by remember { mutableStateOf(false) }

    val onClick = fun() {
        focusManager.clearFocus()

        if (username.length < 4) usernameError = true
        if (password.isEmpty()) passwordError = true

        if (usernameError || passwordError || groupError) return

        loading = true

        trySignUp(
            context,
            username,
            password,
            group!!,
            role,
            {
                loading = false

                val stringRes = when (it) {
                    SignUpError.UNKNOWN -> R.string.unknown_error
                    SignUpError.ALREADY_EXISTS -> R.string.already_exists
                    SignUpError.APPLICATION_TOO_OLD -> R.string.app_too_old
                    SignUpError.TIMED_OUT -> R.string.timed_out
                    SignUpError.NO_CONNECTION -> R.string.no_connection
                    SignUpError.GROUP_DOES_NOT_EXISTS -> R.string.group_does_not_exists
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
            text = stringResource(R.string.sign_up_title),
            modifier = Modifier.padding(10.dp),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.size(10.dp))

        if (role != UserRole.TEACHER) {
            OutlinedTextField(
                value = username,
                singleLine = true,
                onValueChange = {
                    username = it
                    usernameError = false
                },
                label = { Text(stringResource(R.string.username)) },
                isError = usernameError,
                readOnly = loading
            )
        } else {
            TeacherNameSelector(
                value = username,
                isError = usernameError,
                readOnly = loading,
                onValueChange = { username = it ?: "" }
            )
        }

        OutlinedTextField(
            value = password,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {
                passwordError = false
                password = it
            },
            label = { Text(stringResource(R.string.password)) },
            isError = passwordError,
            readOnly = loading
        )

        Spacer(modifier = Modifier.size(10.dp))

        GroupSelector(
            value = group,
            isError = groupError,
            readOnly = loading,
            teacher = role == UserRole.TEACHER
        ) {
            groupError = false
            group = it
        }

        Spacer(modifier = Modifier.size(10.dp))

        RoleSelector(
            value = role,
            isError = false,
            readOnly = loading
        ) { role = it }

        TextButton(onClick = { navController.navigate("sign-in") }) {
            Text(text = stringResource(R.string.already_registered))
        }

        Button(
            enabled = !loading && group != null && !(usernameError || passwordError || groupError),
            onClick = onClick
        ) {
            Text(
                text = stringResource(R.string.proceed),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}