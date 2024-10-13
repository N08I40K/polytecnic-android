package ru.n08i40k.polytechnic.next.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit = { FullScreenLoading() },
    loading: Boolean,
    onRefresh: () -> Unit,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
        return
    }

    PullToRefreshBox(isRefreshing = loading, onRefresh = onRefresh) {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = verticalArrangement) {
            item {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) { CircularProgressIndicator() }
}