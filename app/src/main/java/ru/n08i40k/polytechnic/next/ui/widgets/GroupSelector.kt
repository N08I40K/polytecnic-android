package ru.n08i40k.polytechnic.next.ui.widgets

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGetGroupNames

@Composable
private fun getGroups(context: Context, onUpdated: (String?) -> Unit): ArrayList<String?> {
    val groupPlaceholder = stringResource(R.string.loading)

    val groups = remember { arrayListOf(null, groupPlaceholder) }

    LaunchedEffect(groups) {
        ScheduleGetGroupNames(context, {
            groups.clear()
            groups.addAll(it.names)
            onUpdated(groups.getOrElse(0) { "TODO" }!!)
        }, {
            groups.clear()
            groups.add(null)
            groups.add(context.getString(R.string.failed_to_fetch_group_names))
            onUpdated(groups[1]!!)
        }).send()
    }

    return groups
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GroupSelector(
    value: String? = "ИС-214/24",
    isError: Boolean = false,
    readOnly: Boolean = false,
    onValueChange: (String?) -> Unit = {},
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
            val groups = getGroups(LocalContext.current, onValueChange)

            TextField(
                label = { Text(stringResource(R.string.group)) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                value = value ?: groups.getOrElse(1) { "TODO" }!!,
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

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                groups.forEach {
                    if (it == null)
                        return@forEach

                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            if (groups.size > 0 && groups[0] != null)
                                onValueChange(it)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}