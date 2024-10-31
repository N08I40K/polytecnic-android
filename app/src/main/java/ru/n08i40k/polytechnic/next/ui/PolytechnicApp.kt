package ru.n08i40k.polytechnic.next.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import ru.n08i40k.polytechnic.next.ui.auth.AuthScreen
import ru.n08i40k.polytechnic.next.ui.main.MainScreen
import ru.n08i40k.polytechnic.next.ui.theme.AppTheme


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PolytechnicApp() {
    AppTheme(darkTheme = true, content = {
        val navController = rememberNavController()
        val context = LocalContext.current

        val accessToken = runBlocking {
            context.settingsDataStore.data.map { it.accessToken }.first()
        }

        NavHost(
            navController = navController,
            startDestination = if (accessToken.isEmpty()) "auth" else "main"
        ) {
            composable(route = "auth") {
                AuthScreen(navController)
            }

            composable(route = "main") {
                MainScreen(navController)
            }
        }
    })
}