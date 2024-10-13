package ru.n08i40k.polytechnic.next.ui.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.settings.settingsDataStore


@Preview(showBackground = true)
@Composable
fun AuthForm(
    appNavController: NavHostController = rememberNavController(),
    onPendingSnackbar: (String) -> Unit = {},
) {
    val navController = rememberNavController()

    val modifier = Modifier.fillMaxSize()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = "sign-in",
        enterTransition = {
            slideIn(
                animationSpec = tween(
                    400,
                    delayMillis = 250,
                    easing = LinearOutSlowInEasing
                )
            ) { fullSize -> IntOffset(0, fullSize.height / 16) } + fadeIn(
                animationSpec = tween(
                    400,
                    delayMillis = 250,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    250,
                    easing = FastOutSlowInEasing
                )
            )
        },
    ) {
        composable("sign-in") {
            Row(
                modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.inverseSurface)) {
                    LoginForm(appNavController, navController, onPendingSnackbar)
                }
            }
        }
        composable("sign-up") {
            Row(
                modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.inverseSurface)) {
                    RegisterForm(appNavController, navController, onPendingSnackbar)
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun AuthScreen(appNavController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val accessToken: String = runBlocking {
            context.settingsDataStore.data.map { settings -> settings.accessToken }.first()
        }

        if (accessToken.isNotEmpty()) {
            appNavController.navigate("main") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onPendingSnackbar: (String) -> Unit = {
        scope.launch { snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long) }
    }

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
                AuthForm(
                    appNavController,
                    onPendingSnackbar
                )
            }
        })
}