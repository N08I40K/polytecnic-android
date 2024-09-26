package ru.n08i40k.polytechnic.next.ui.main.profile

import androidx.compose.foundation.layout.Arrangement
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
import ru.n08i40k.polytechnic.next.ui.model.ProfileUiState
import ru.n08i40k.polytechnic.next.ui.model.ProfileViewModel


@Preview(showBackground = true)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = ProfileViewModel(MockAppContainer(LocalContext.current).profileRepository) {},
    onRefreshProfile: () -> Unit = {}
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    LoadingContent(
        empty = when (uiState) {
            is ProfileUiState.NoProfile -> uiState.isLoading
            is ProfileUiState.HasProfile -> false
        },
        loading = uiState.isLoading,
        onRefresh = onRefreshProfile,
        verticalArrangement = Arrangement.Top
    ) {
        when (uiState) {
            is ProfileUiState.HasProfile -> {
                ProfileCard((uiState as ProfileUiState.HasProfile).profile)
            }

            is ProfileUiState.NoProfile -> {
                if (!uiState.isLoading) {
                    TextButton(onClick = onRefreshProfile, modifier = Modifier.fillMaxSize()) {
                        Text(stringResource(R.string.reload), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
