package ru.n08i40k.polytechnic.next.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.MainViewModel
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import ru.n08i40k.polytechnic.next.ui.main.profile.ProfileScreen
import ru.n08i40k.polytechnic.next.ui.main.replacer.ReplacerScreen
import ru.n08i40k.polytechnic.next.ui.main.schedule.ScheduleScreen
import ru.n08i40k.polytechnic.next.ui.model.ProfileUiState
import ru.n08i40k.polytechnic.next.ui.model.ProfileViewModel
import ru.n08i40k.polytechnic.next.ui.model.ScheduleReplacerViewModel
import ru.n08i40k.polytechnic.next.ui.model.ScheduleViewModel
import ru.n08i40k.polytechnic.next.ui.model.profileViewModel


@Composable
private fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
    scheduleViewModel: ScheduleViewModel,
    scheduleReplacerViewModel: ScheduleReplacerViewModel?
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "schedule",
        modifier = Modifier.padding(paddingValues = padding),
        enterTransition = {
            slideIn(
                animationSpec = tween(
                    500,
                    delayMillis = 250,
                    easing = LinearOutSlowInEasing
                )
            ) { fullSize -> IntOffset(-fullSize.width, 0) }
        },
        exitTransition = {
            slideOut(
                animationSpec = tween(
                    500,
                    easing = FastOutSlowInEasing
                )
            ) { fullSize -> IntOffset(fullSize.width, 0) }
        },
        builder = {
            composable("profile") {
                ProfileScreen(LocalContext.current.profileViewModel!!) { context.profileViewModel!!.refreshProfile() }
            }

            composable("schedule") {
                ScheduleScreen(scheduleViewModel) { scheduleViewModel.refreshGroup() }
            }

            if (scheduleReplacerViewModel != null) {
                composable("replacer") {
                    ReplacerScreen(scheduleReplacerViewModel) { scheduleReplacerViewModel.refresh() }
                }
            }
        })
}

@Composable
private fun BottomNavBar(navController: NavHostController, isAdmin: Boolean) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        val currentRoute = navBackStackEntry?.destination?.route

        Constants.bottomNavItem.forEach {
            if (it.isAdmin && !isAdmin)
                return@forEach

            NavigationBarItem(
                selected = it.route == currentRoute,
                onClick = { if (it.route != currentRoute) navController.navigate(it.route) },
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = stringResource(it.label)
                    )
                },
                label = { Text(stringResource(it.label)) })
        }
    }
}

@Composable
fun MainScreen(
    appNavController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val accessToken: String = runBlocking {
            context.settingsDataStore.data.map { settings -> settings.accessToken }.first()
        }

        if (accessToken.isEmpty()) appNavController.navigate("auth")
    }

    val scheduleViewModel =
        hiltViewModel<ScheduleViewModel>(LocalContext.current as ComponentActivity)

    LocalContext.current.profileViewModel =
        viewModel(
            factory = ProfileViewModel.provideFactory(
                profileRepository = mainViewModel.appContainer.profileRepository,
                onUnauthorized = { appNavController.navigate("auth") })
        )

    val profileUiState by LocalContext.current.profileViewModel!!.uiState.collectAsStateWithLifecycle()
    val isAdmin = (profileUiState is ProfileUiState.HasProfile) &&
            (profileUiState as ProfileUiState.HasProfile).profile.role == UserRole.ADMIN

    val scheduleReplacerViewModel: ScheduleReplacerViewModel? =
        if (isAdmin) hiltViewModel(LocalContext.current as ComponentActivity)
        else null

    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController, isAdmin) }
    ) { paddingValues ->
        NavHostContainer(
            navController,
            paddingValues,
            scheduleViewModel,
            scheduleReplacerViewModel
        )
    }
}