package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository
import ru.n08i40k.polytechnic.next.model.Group
import java.util.Calendar
import kotlin.math.absoluteValue

@Preview
@Composable
fun DayPager(group: Group = FakeScheduleRepository.exampleGroup) {
    val currentDay = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2)
    val calendarDay = currentDay
        .coerceAtLeast(0)
        .coerceAtMost(group.days.size - 1)

    val pagerState = rememberPagerState(initialPage = calendarDay, pageCount = { group.days.size })

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 20.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.height(600.dp)
    ) { page ->
        DayCard(
            modifier = Modifier.graphicsLayer {
                val offset = pagerState.getOffsetDistanceInPages(page).absoluteValue

                lerp(
                    start = 1f, stop = 0.95f, fraction = 1f - offset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }
                alpha = lerp(
                    start = 0.5f, stop = 1f, fraction = 1f - offset.coerceIn(0f, 1f)
                )
            },
            day = group.days[page],
            current = currentDay == page
        )
    }
}