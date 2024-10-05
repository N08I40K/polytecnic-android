package ru.n08i40k.polytechnic.next.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
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
import ru.n08i40k.polytechnic.next.PolytechnicApplication
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import ru.n08i40k.polytechnic.next.ui.MainActivity
import ru.n08i40k.polytechnic.next.ui.icons.AppIcons
import ru.n08i40k.polytechnic.next.ui.icons.appicons.Filled
import ru.n08i40k.polytechnic.next.ui.icons.appicons.filled.Download
import ru.n08i40k.polytechnic.next.ui.icons.appicons.filled.Telegram
import ru.n08i40k.polytechnic.next.ui.main.profile.ProfileScreen
import ru.n08i40k.polytechnic.next.ui.main.replacer.ReplacerScreen
import ru.n08i40k.polytechnic.next.ui.main.schedule.ScheduleScreen
import ru.n08i40k.polytechnic.next.ui.model.ProfileUiState
import ru.n08i40k.polytechnic.next.ui.model.ProfileViewModel
import ru.n08i40k.polytechnic.next.ui.model.RemoteConfigViewModel
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

private fun openLink(context: Context, link: String) {
    startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(link)), null)
}

@Composable
private fun LinkButton(
    text: String,
    icon: ImageVector,
    link: String,
    enabled: Boolean = true,
    badged: Boolean = false,
) {
    val context = LocalContext.current

    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { openLink(context, link) },
        enabled = enabled,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgedBox(badge = { if (badged) Badge() }) {
                Icon(imageVector = icon, contentDescription = text)
            }
            Spacer(Modifier.width(5.dp))
            Text(text)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopNavBar(
    remoteConfigViewModel: RemoteConfigViewModel
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val remoteConfigUiState by remoteConfigViewModel.uiState.collectAsStateWithLifecycle()

    val packageVersion =
        (LocalContext.current.applicationContext as PolytechnicApplication).getAppVersion()
    val updateAvailable = remoteConfigUiState.currVersion != packageVersion

    TopAppBar(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        actions = {
            IconButton(onClick = { dropdownExpanded = true }) {
                BadgedBox(badge = { if (updateAvailable) Badge() }) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "TopAppBar Menu")
                }
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                Column(modifier = Modifier.wrapContentWidth()) {
                    LinkButton(
                        text = stringResource(R.string.download_update),
                        icon = AppIcons.Filled.Download,
                        link = remoteConfigUiState.downloadLink,
                        enabled = updateAvailable,
                        badged = updateAvailable
                    )
                    LinkButton(
                        text = stringResource(R.string.telegram_channel),
                        icon = AppIcons.Filled.Telegram,
                        link = remoteConfigUiState.telegramLink,
                    )
                }
            }
        }
    )
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

    // profile view model
    val profileViewModel: ProfileViewModel =
        viewModel(
            factory = ProfileViewModel.provideFactory(
                profileRepository = mainViewModel.appContainer.profileRepository,
                onUnauthorized = { appNavController.navigate("auth") })
        )
    LocalContext.current.profileViewModel = profileViewModel

    // remote config view model
    val remoteConfigViewModel: RemoteConfigViewModel =
        viewModel(
            factory = RemoteConfigViewModel.provideFactory(
                appContext = LocalContext.current,
                remoteConfig = (LocalContext.current as MainActivity).remoteConfig
            )
        )

    // schedule view model
    val scheduleViewModel =
        hiltViewModel<ScheduleViewModel>(LocalContext.current as ComponentActivity)

    // schedule replacer view model
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    val isAdmin = when (profileUiState) {
        is ProfileUiState.NoProfile -> false
        is ProfileUiState.HasProfile -> {
            val profile = (profileUiState as ProfileUiState.HasProfile).profile

            profile.role == UserRole.ADMIN
        }
    }

    val scheduleReplacerViewModel: ScheduleReplacerViewModel? =
        if (isAdmin) hiltViewModel(LocalContext.current as ComponentActivity)
        else null

    // nav controller

    val navController = rememberNavController()
    Scaffold(
        topBar = { TopNavBar(remoteConfigViewModel) },
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