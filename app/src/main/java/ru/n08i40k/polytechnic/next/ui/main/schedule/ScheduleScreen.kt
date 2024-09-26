package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.MockAppContainer
import ru.n08i40k.polytechnic.next.ui.LoadingContent
import ru.n08i40k.polytechnic.next.ui.model.ScheduleUiState
import ru.n08i40k.polytechnic.next.ui.model.ScheduleViewModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScheduleScreen(
    scheduleViewModel: ScheduleViewModel = ScheduleViewModel(MockAppContainer(LocalContext.current)),
    onRefreshSchedule: () -> Unit = {}
) {
    val uiState by scheduleViewModel.uiState.collectAsStateWithLifecycle()

    LoadingContent(
        empty = when (uiState) {
            is ScheduleUiState.NoSchedule -> uiState.isLoading
            is ScheduleUiState.HasSchedule -> false
        },
        loading = uiState.isLoading,
        onRefresh = onRefreshSchedule
    ) {
        when (uiState) {
            is ScheduleUiState.HasSchedule -> {
                DayPager((uiState as ScheduleUiState.HasSchedule).group)
            }

            is ScheduleUiState.NoSchedule -> {
                if (!uiState.isLoading) {
                    TextButton(onClick = onRefreshSchedule, modifier = Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.reload), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}