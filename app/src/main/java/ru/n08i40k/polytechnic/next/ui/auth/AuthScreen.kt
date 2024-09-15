package ru.n08i40k.polytechnic.next.ui.auth

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.volley.AuthFailureError
import com.android.volley.ClientError
import com.android.volley.TimeoutError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.AcceptableUserRoles
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.data.auth.LoginRequest
import ru.n08i40k.polytechnic.next.network.data.auth.LoginRequestData
import ru.n08i40k.polytechnic.next.network.data.auth.RegisterRequest
import ru.n08i40k.polytechnic.next.network.data.auth.RegisterRequestData
import ru.n08i40k.polytechnic.next.network.data.profile.UsersMeRequest
import ru.n08i40k.polytechnic.next.settings.settingsDataStore

@Preview(showBackground = true)
@Composable
private fun LoginForm(
    mutableVisible: MutableState<Boolean> = mutableStateOf(true),
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val mutableIsLoading = remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visible by mutableVisible

    Text(
        text = stringResource(R.string.login_title),
        modifier = Modifier.padding(10.dp),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.ExtraBold
    )

    Spacer(modifier = Modifier.size(10.dp))

    val mutableUsernameError = remember { mutableStateOf(false) }
    val mutablePasswordError = remember { mutableStateOf(false) }

    var usernameError by mutableUsernameError
    var passwordError by mutablePasswordError

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

    TextButton(onClick = { visible = false }) {
        Text(text = stringResource(R.string.not_registered))
    }

    Button(onClick = {
        if (username.length < 4) usernameError = true
        if (password.isEmpty()) passwordError = true

        if (usernameError || passwordError) return@Button

        tryLogin(
            username,
            password,
            mutableUsernameError,
            mutablePasswordError,
            mutableIsLoading,
            context,
            snackbarHostState,
            scope,
            navController
        )

        mutableIsLoading.value = true
        focusManager.clearFocus()
    }) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterForm(
    mutableVisible: MutableState<Boolean> = mutableStateOf(true),
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val mutableIsLoading = remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    val mutableRole = remember { mutableStateOf(UserRole.STUDENT) }

    var visible by mutableVisible

    Text(
        text = stringResource(R.string.register_title),
        modifier = Modifier.padding(10.dp),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.ExtraBold
    )

    Spacer(modifier = Modifier.size(10.dp))

    val mutableUsernameError = remember { mutableStateOf(false) }
    var usernameError by mutableUsernameError

    var passwordError by remember { mutableStateOf(false) }

    val mutableGroupError = remember { mutableStateOf(false) }
    var groupError by mutableGroupError

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

    OutlinedTextField(
        value = group,
        singleLine = true,
        onValueChange = {
            groupError = false
            group = it
        },
        label = { Text(stringResource(R.string.group)) },
        isError = groupError
    )

    RoleSelector(mutableRole)

    TextButton(onClick = { visible = false }) {
        Text(text = stringResource(R.string.already_registered))
    }

    Button(
        enabled = !mutableIsLoading.value,
        onClick = {
            if (username.length < 4) usernameError = true
            if (password.isEmpty()) passwordError = true
            if (group.isEmpty()) groupError = true

            if (usernameError || passwordError || groupError) return@Button

            tryRegister(
                username,
                password,
                group,
                mutableRole.value,
                mutableUsernameError,
                mutableGroupError,
                mutableIsLoading,
                context,
                snackbarHostState,
                scope,
                navController
            )

            mutableIsLoading.value = true
            focusManager.clearFocus()
        }) {
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthForm(
    mutableIsLogin: MutableState<Boolean> = mutableStateOf(true),
    navController: NavHostController = rememberNavController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var isLogin by mutableIsLogin

    val mutableVisible = remember { mutableStateOf(true) }
    var visible by mutableVisible

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1.0f else 0f, label = "alpha"
    )

    Column(
        modifier = Modifier
            .padding(10.dp)
            .graphicsLayer {
                alpha = animatedAlpha
                if (alpha == 0F) {
                    if (!visible) isLogin = isLogin.not()
                    visible = true
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isLogin)
            LoginForm(mutableVisible, navController, scope, snackbarHostState)
        else
            RegisterForm(mutableVisible, navController, scope, snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RoleSelector(mutableRole: MutableState<UserRole> = mutableStateOf(UserRole.STUDENT)) {
    var expanded by remember { mutableStateOf(false) }

    var role by mutableRole

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                label = { Text(stringResource(R.string.role)) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                value = stringResource(role.stringId),
                leadingIcon = {
                    Icon(
                        imageVector = role.icon,
                        contentDescription = "role icon"
                    )
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                AcceptableUserRoles.forEach {
                    DropdownMenuItem(
                        text = { Text(stringResource(it.stringId)) },
                        onClick = {
                            role = it
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun tryLogin(
    // data
    username: String,
    password: String,

    // errors
    mutableUsernameError: MutableState<Boolean>,
    mutablePasswordError: MutableState<Boolean>,

    // additional
    mutableIsLoading: MutableState<Boolean>,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    var isLoading by mutableIsLoading

    LoginRequest(LoginRequestData(username, password), context, {
        scope.launch { snackbarHostState.showSnackbar("Cool!") }

        runBlocking {
            context.settingsDataStore.updateData { currentSettings ->
                currentSettings
                    .toBuilder()
                    .setUserId(it.id)
                    .setAccessToken(it.accessToken)
                    .build()
            }
        }

        UsersMeRequest(context, {
            runBlocking {
                context.settingsDataStore.updateData { currentSettings ->
                    currentSettings
                        .toBuilder()
                        .setGroup(it.group)
                        .build()
                }
            }

            navController.navigate("main")
        }, {}).send()
    }, {
        isLoading = false

        if (it is TimeoutError) {
            scope.launch { snackbarHostState.showSnackbar("Request timed out!") }
        }

        if (it is ClientError && it.networkResponse.statusCode == 400) scope.launch {
            snackbarHostState.showSnackbar("Request schema not identical!")
        }

        if (it is AuthFailureError) scope.launch {
            mutableUsernameError.value = true
            mutablePasswordError.value = true
            snackbarHostState.showSnackbar("Invalid credentials!")
        }


        it.printStackTrace()
    }).send()
}

fun tryRegister(
    // data
    username: String,
    password: String,
    group: String,
    role: UserRole,

    // errors
    mutableUsernameError: MutableState<Boolean>,
    mutableGroupError: MutableState<Boolean>,

    // additional
    mutableIsLoading: MutableState<Boolean>,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    navController: NavHostController
) {
    var isLoading by mutableIsLoading

    RegisterRequest(
        RegisterRequestData(
            username,
            password,
            group,
            role
        ), context, {
            scope.launch { snackbarHostState.showSnackbar("Cool!") }

            runBlocking {
                context.settingsDataStore.updateData { currentSettings ->
                    currentSettings.toBuilder().setUserId(it.id)
                        .setAccessToken(it.accessToken).build()
                }
            }

            navController.navigate("main")
        }, {
            isLoading = false

            if (it is TimeoutError) {
                scope.launch { snackbarHostState.showSnackbar("Request timed out!") }
            }

            if (it is ClientError) scope.launch {
                val statusCode = it.networkResponse.statusCode

                when (statusCode) {
                    400 -> snackbarHostState.showSnackbar("Request schema not identical!")
                    409 -> {
                        mutableUsernameError.value = true
                        snackbarHostState.showSnackbar("User already exists!")
                    }

                    404 -> {
                        mutableGroupError.value = true
                        snackbarHostState.showSnackbar("Group doesn't exists!")
                    }
                }
            }

            if (it is AuthFailureError) scope.launch {
                snackbarHostState.showSnackbar(
                    "Invalid credentials!"
                )
            }


            it.printStackTrace()
        }).send()
}

@Preview(showBackground = true)
@Composable
fun AuthScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val accessToken: String = runBlocking {
            context.settingsDataStore.data.map { settings -> settings.accessToken }.first()
        }

        if (accessToken.isNotEmpty()) navController.navigate("main")
    }

    val mutableIsLogin = remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        content = { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card {
                    AuthForm(
                        mutableIsLogin,
                        navController,
                        scope,
                        snackbarHostState
                    )
                }
            }
        })
}