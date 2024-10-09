package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import ru.n08i40k.polytechnic.next.ui.LoadingContent
import ru.n08i40k.polytechnic.next.ui.model.ScheduleUiState
import ru.n08i40k.polytechnic.next.ui.model.ScheduleViewModel

@Composable
private fun rememberUpdatedLifecycleOwner(): LifecycleOwner {
    val lifecycleOwner = LocalLifecycleOwner.current
    return remember { lifecycleOwner }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScheduleScreen(
    scheduleViewModel: ScheduleViewModel = ScheduleViewModel(MockAppContainer(LocalContext.current)),
    onRefresh: () -> Unit = {}
) {
    val uiState by scheduleViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        delay(120_000)
        onRefresh()
    }

    val lifecycleOwner = rememberUpdatedLifecycleOwner()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    onRefresh()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LoadingContent(
        empty = when (uiState) {
            is ScheduleUiState.NoSchedule -> uiState.isLoading
            is ScheduleUiState.HasSchedule -> false
        },
        loading = uiState.isLoading,
        onRefresh = onRefresh,
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState) {
            is ScheduleUiState.HasSchedule -> {
                Column {
                    val hasSchedule = uiState as ScheduleUiState.HasSchedule

                    UpdateInfo(hasSchedule.lastUpdateAt, hasSchedule.updateDates)
                    Spacer(Modifier.height(10.dp))
                    DayPager(hasSchedule.group)
                }
            }

            is ScheduleUiState.NoSchedule -> {
                if (!uiState.isLoading) {
                    TextButton(onClick = onRefresh, modifier = Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.reload), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}