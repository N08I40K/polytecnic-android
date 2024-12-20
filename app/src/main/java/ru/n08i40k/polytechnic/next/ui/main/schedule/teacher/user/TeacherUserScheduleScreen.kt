package ru.n08i40k.polytechnic.next.ui.main.schedule.teacher.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.MockAppContainer
import ru.n08i40k.polytechnic.next.ui.main.schedule.DayPager
import ru.n08i40k.polytechnic.next.ui.model.TeacherScheduleUiState
import ru.n08i40k.polytechnic.next.ui.model.TeacherScheduleViewModel
import ru.n08i40k.polytechnic.next.ui.widgets.LoadingContent

@Composable
private fun rememberUpdatedLifecycleOwner(): LifecycleOwner {
    val lifecycleOwner = LocalLifecycleOwner.current
    return remember { lifecycleOwner }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeacherUserScheduleScreen(
    teacherScheduleViewModel: TeacherScheduleViewModel = TeacherScheduleViewModel(
        MockAppContainer(
            LocalContext.current
        )
    ),
    fetch: (String) -> Unit = {}
) {
    var teacherName by remember { mutableStateOf("") }

    val uiState by teacherScheduleViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        delay(120_000)
        fetch(teacherName)
    }

    val lifecycleOwner = rememberUpdatedLifecycleOwner()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    fetch(teacherName)
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(Modifier.fillMaxSize()) {
        TeacherSearchBox(onSearchAttempt = {
            teacherName = it
            fetch(it)
        })

        Spacer(Modifier.height(10.dp))

        LoadingContent(
            empty = when (uiState) {
                is TeacherScheduleUiState.NoData -> uiState.isLoading
                is TeacherScheduleUiState.HasData -> false
            },
            loading = uiState.isLoading,
        ) {
            when (uiState) {
                is TeacherScheduleUiState.HasData -> {
                    Column {
                        val hasData = uiState as TeacherScheduleUiState.HasData

                        DayPager(hasData.teacher)
                    }
                }

                is TeacherScheduleUiState.NoData -> {
                    if (!uiState.isLoading) {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = stringResource(R.string.teacher_not_selected),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}