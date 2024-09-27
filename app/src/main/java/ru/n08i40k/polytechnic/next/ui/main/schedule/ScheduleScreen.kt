package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.n08i40k.polytechnic.next.MainViewModel
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
        empty = uiState.isLoading,
        loading = uiState.isLoading,
        onRefresh = { onRefreshSchedule() },
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState) {
            is ScheduleUiState.HasSchedule -> {
                Box {
                    val networkCacheRepository =
                        hiltViewModel<MainViewModel>(LocalContext.current as ComponentActivity)
                            .appContainer
                            .networkCacheRepository

                    UpdateInfo(networkCacheRepository)

                    Column {
                        Spacer(modifier = Modifier.height(200.dp))
                        DayPager((uiState as ScheduleUiState.HasSchedule).group)
                    }
                }
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