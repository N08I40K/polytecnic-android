package ru.n08i40k.polytechnic.next.ui.main.replacer

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.MockAppContainer
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleReplacerRepository
import ru.n08i40k.polytechnic.next.model.ScheduleReplacer
import ru.n08i40k.polytechnic.next.ui.LoadingContent
import ru.n08i40k.polytechnic.next.ui.model.ScheduleReplacerUiState
import ru.n08i40k.polytechnic.next.ui.model.ScheduleReplacerViewModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReplacerScreen(
    scheduleReplacerViewModel: ScheduleReplacerViewModel = ScheduleReplacerViewModel(
        MockAppContainer(
            LocalContext.current
        )
    ),
    refresh: () -> Unit = {}
) {
    val uiState by scheduleReplacerViewModel.uiState.collectAsStateWithLifecycle()

    var uri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        uri = it
    }

    UploadFile(scheduleReplacerViewModel, uri) { uri = null }

    LoadingContent(
        empty = when (uiState) {
            is ScheduleReplacerUiState.NoData -> uiState.isLoading
            is ScheduleReplacerUiState.HasData -> false
        },
        loading = uiState.isLoading,
        onRefresh = refresh,
        verticalArrangement = Arrangement.Top,
        content = {
            when (uiState) {
                is ScheduleReplacerUiState.NoData -> {
                    if (!uiState.isLoading) {
                        TextButton(onClick = refresh, modifier = Modifier.fillMaxSize()) {
                            Text(stringResource(R.string.reload), textAlign = TextAlign.Center)
                        }
                    }
                }

                is ScheduleReplacerUiState.HasData -> {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            ClearButton(Modifier.fillMaxWidth(0.5F)) {
                                scheduleReplacerViewModel.clear()
                            }

                            SetNewButton(Modifier.fillMaxWidth()) {
                                launcher.launch(arrayOf("application/vnd.ms-excel"))
                            }
                        }

                        ReplacerList((uiState as ScheduleReplacerUiState.HasData).replacers)
                    }
                }
            }
        }
    )
}

@Composable
fun UploadFile(
    scheduleReplacerViewModel: ScheduleReplacerViewModel,
    uri: Uri?,
    onFinish: () -> Unit
) {
    if (uri == null)
        return

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    // get file name
    val query = contentResolver.query(uri, null, null, null, null)
    if (query == null) {
        onFinish()
        return
    }

    val fileName = query.use { cursor ->
        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()

        cursor.getString(nameIdx)
    }

    // get file type
    val fileType: String? = contentResolver.getType(uri)
    if (fileType == null) {
        onFinish()
        return
    }

    // get file data
    val inputStream = contentResolver.openInputStream(uri)
    if (inputStream == null) {
        onFinish()
        return
    }

    val fileData = inputStream.readBytes()

    inputStream.close()

    scheduleReplacerViewModel.set(fileName, fileData, fileType)
    onFinish()
}

//@Preview(showBackground = true)
//@Composable
//private fun UploadFileDialog(
//    opened: Boolean = true,
//    onClose: () -> Unit = {}
//) {
//    Dialog(onDismissRequest = onClose) {
//        Card {
//            Button
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
private fun SetNewButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(modifier = modifier, onClick = onClick) {
        val setReplacerText = stringResource(R.string.set_replacer)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = setReplacerText)
            Text(text = setReplacerText)
            Icon(imageVector = Icons.Filled.Add, contentDescription = setReplacerText)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClearButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(modifier = modifier, onClick = onClick) {
        val clearReplacersText = stringResource(R.string.clear_replacers)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = clearReplacersText)
            Text(text = clearReplacersText)
            Icon(imageVector = Icons.Filled.Delete, contentDescription = clearReplacersText)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReplacerElement(replacer: ScheduleReplacer = FakeScheduleReplacerRepository.exampleReplacers[0]) {
    Column(
        modifier = Modifier.border(
            BorderStroke(
                Dp.Hairline,
                MaterialTheme.colorScheme.inverseSurface
            )
        )
    ) {
        val modifier = Modifier.fillMaxWidth()

        Text(modifier = modifier, textAlign = TextAlign.Center, text = replacer.etag)
        Text(modifier = modifier, textAlign = TextAlign.Center, text = buildString {
            append(replacer.size)
            append(" ")
            append(stringResource(R.string.bytes))
        })
    }
}

@Preview(showBackground = true)
@Composable
fun ReplacerList(replacers: List<ScheduleReplacer> = FakeScheduleReplacerRepository.exampleReplacers) {
    Surface {
        LazyColumn(
            contentPadding = PaddingValues(0.dp, 5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
        ) {
            items(replacers) {
                ReplacerElement(it)
            }
        }
    }
}
