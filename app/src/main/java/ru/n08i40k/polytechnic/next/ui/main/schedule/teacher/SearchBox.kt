package ru.n08i40k.polytechnic.next.ui.main.schedule.teacher

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    title: String,
    onSearchAttempt: (String) -> Unit,
    variants: List<String>,
) {
    var value by remember { mutableStateOf("") }

    val searchableVariants =
        remember(variants.size) { variants.map { it.replace(" ", "").replace(".", "").lowercase() } }
    val filteredVariants = remember(searchableVariants, value) {
        searchableVariants.filter { it.contains(value) }
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = {}
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.hasFocus)
                            dropdownExpanded = true
                    }
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true),
                label = { Text(title) },
                value = value,
                onValueChange = {
                    value = it
                    dropdownExpanded = true
                },
                trailingIcon = {
                    IconButton(onClick = { onSearchAttempt(value) }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                singleLine = true,
            )
        }

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            filteredVariants.forEach {
                val fullVariant = variants[searchableVariants.indexOf(it)]

                DropdownMenuItem(
                    text = { Text(fullVariant) },
                    onClick = {
                        value = fullVariant
                        onSearchAttempt(value)

                        dropdownExpanded = false
                    }
                )
            }
        }
    }
}