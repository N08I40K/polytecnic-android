package ru.n08i40k.polytechnic.next.ui.main.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.users.impl.FakeProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.settings.settingsDataStore
import ru.n08i40k.polytechnic.next.ui.model.ScheduleViewModel
import ru.n08i40k.polytechnic.next.ui.model.profileViewModel

@Preview(showBackground = true)
@Composable
internal fun ProfileCard(profile: Profile = FakeProfileRepository.exampleProfile) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Card(
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val focusManager = LocalFocusManager.current
                    val context = LocalContext.current

                    var usernameChanging by remember { mutableStateOf(false) }
                    var passwordChanging by remember { mutableStateOf(false) }
                    var groupChanging by remember { mutableStateOf(false) }

                    TextField(
                        label = { Text(stringResource(R.string.username)) },
                        value = profile.username,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "username"
                            )
                        },
                        readOnly = true,
                        onValueChange = {},
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                usernameChanging = true
                                focusManager.clearFocus()
                            }
                        },
                    )

                    TextField(
                        label = { Text(stringResource(R.string.password)) },
                        value = "12345678",
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "password"
                            )
                        },
                        readOnly = true,
                        onValueChange = {},
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                passwordChanging = true
                                focusManager.clearFocus()
                            }
                        },
                    )

                    TextField(
                        label = { Text(stringResource(R.string.role)) },
                        value = stringResource(profile.role.stringId),
                        leadingIcon = {
                            Icon(
                                imageVector = profile.role.icon,
                                contentDescription = "role"
                            )
                        },
                        readOnly = true,
                        onValueChange = {},
                    )

                    TextField(
                        label = { Text(stringResource(R.string.group)) },
                        value = profile.group,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "group"
                            )
                        },
                        readOnly = true,
                        onValueChange = {},
                        modifier = Modifier.onFocusChanged {
                            if (it.isFocused) {
                                groupChanging = true
                                focusManager.clearFocus()
                            }
                        },
                    )

                    if (passwordChanging) {
                        ChangePasswordDialog(
                            context,
                            profile,
                            { passwordChanging = false }
                        ) { passwordChanging = false }
                    }

                    if (usernameChanging) {
                        ChangeUsernameDialog(
                            context,
                            profile,
                            {
                                usernameChanging = false
                                context.profileViewModel!!.refreshProfile()
                            }
                        ) { usernameChanging = false }
                    }

                    if (groupChanging) {
                        val scheduleViewModel =
                            hiltViewModel<ScheduleViewModel>(LocalContext.current as ComponentActivity)

                        ChangeGroupDialog(
                            context,
                            profile,
                            { group ->
                                groupChanging = false
                                runBlocking {
                                    context.settingsDataStore.updateData {
                                        it.toBuilder().setGroup(group).build()
                                    }
                                }
                                context.profileViewModel!!.refreshProfile {
                                    scheduleViewModel.refreshGroup()
                                }
                            }
                        ) { groupChanging = false }
                    }
                }
            }
        }
    }
}
