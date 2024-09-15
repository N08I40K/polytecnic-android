package ru.n08i40k.polytechnic.next.ui.main.profile

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import ru.n08i40k.polytechnic.next.network.data.profile.ChangeGroupRequest
import ru.n08i40k.polytechnic.next.network.data.profile.ChangeGroupRequestData
import ru.n08i40k.polytechnic.next.network.data.schedule.ScheduleGetGroupNamesRequest

private enum class ChangeGroupError {
    NOT_EXISTS
}

private fun tryChangeGroup(
    context: Context,
    group: String,
    onError: (ChangeGroupError) -> Unit,
    onSuccess: (String) -> Unit
) {
    ChangeGroupRequest(ChangeGroupRequestData(group), context, {
        onSuccess(group)
    }, {
        if (it is ClientError && it.networkResponse.statusCode == 404)
            onError(ChangeGroupError.NOT_EXISTS)
        else throw it
    }).send()
}

@Composable
private fun getGroups(context: Context): ArrayList<String> {
    val groupPlaceholder = stringResource(R.string.loading)

    val groups = remember { arrayListOf(groupPlaceholder) }

    LaunchedEffect(groups) {
        ScheduleGetGroupNamesRequest(context, {
            groups.clear()
            groups.addAll(it.names)
        }, {
            throw it
        }).send()
    }

    return groups
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun GroupSelector(
    value: String = "ИС-214/24",
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false,
    readOnly: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !readOnly && !expanded
            }
        ) {
            TextField(
                label = { Text(stringResource(R.string.group)) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                value = value,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "group"
                    )
                },
                onValueChange = {},
                isError = isError,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            val context = LocalContext.current
            val groups = getGroups(context)

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                groups.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            onValueChange(it)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun ChangeGroupDialog(
    context: Context = LocalContext.current,
    profile: Profile = FakeProfileRepository.exampleProfile,
    onChange: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var group by remember { mutableStateOf("ИС-214/23") }
            var groupError by remember { mutableStateOf(false) }

            var processing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                val modifier = Modifier.fillMaxWidth()

                GroupSelector(
                    value = group,
                    onValueChange = { group = it },
                    isError = groupError,
                    readOnly = processing
                )

                val focusManager = LocalFocusManager.current
                Button(
                    modifier = modifier,
                    onClick = {
                        processing = true
                        focusManager.clearFocus()

                        tryChangeGroup(
                            context = context,
                            group = group,
                            onError = {
                                when (it) {
                                    ChangeGroupError.NOT_EXISTS -> {
                                        groupError = true
                                    }
                                }

                                processing = false
                            },
                            onSuccess = onChange
                        )
                    },
                    enabled = !(groupError || processing)
                ) {
                    Text(stringResource(R.string.change_group))
                }
            }
        }
    }
}