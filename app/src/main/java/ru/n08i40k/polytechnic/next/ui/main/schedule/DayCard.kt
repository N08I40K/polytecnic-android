package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.LessonType

@Composable
private fun getCurrentLessonIdx(day: Day?): Flow<Int> {
    val value by remember {
        derivedStateOf {
            flow {
                while (true) {
                    emit(day?.currentIdx ?: -1)
                    delay(5_000)
                }
            }
        }
    }

    return value
}

@Preview(showBackground = true)
@Composable
fun DayCard(
    modifier: Modifier = Modifier,
    day: Day? = FakeScheduleRepository.exampleGroup.days[0],
    distance: Int = 0
) {
    val defaultCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )
    val customCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
    val noneCardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor =
            if (distance == 0) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.secondaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.inverseSurface)
    ) {
        if (day == null) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.day_null)
            )
            return@Card
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            text = day.name,
        )

        if (distance >= -1 && distance <= 1) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(when (distance) {
                    -1 -> R.string.yesterday
                    0 -> R.string.today
                    1 -> R.string.tomorrow
                    else -> throw RuntimeException()
                }),
            )
        }

        val currentLessonIdx by getCurrentLessonIdx(if (distance == 0) day else null)
            .collectAsStateWithLifecycle(0)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            if (day.lessons.isEmpty()) {
                Text("Can't get schedule!")
                return@Column
            }

            for (lessonIdx in day.lessons.indices) {
                val lesson = day.lessons[lessonIdx]

                val cardColors = when (lesson.type) {
                    LessonType.DEFAULT -> defaultCardColors
                    LessonType.ADDITIONAL -> customCardColors
                    LessonType.BREAK -> noneCardColors
                }

                val mutableExpanded = remember { mutableStateOf(false) }

                Box(
                    Modifier
                        .clickable { mutableExpanded.value = true }
                        .background(cardColors.containerColor)
                ) {
                    val now = lessonIdx == currentLessonIdx

                    if (lesson.type === LessonType.BREAK)
                        FreeLessonRow(lesson, lesson, cardColors, now)
                    else
                        LessonRow(day, lesson, cardColors, now)
                }

                if (mutableExpanded.value)
                    LessonExtraInfo(lesson, mutableExpanded)
            }
        }
    }
}