package ru.n08i40k.polytechnic.next.ui.main.schedule

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonType
import java.util.Calendar

private fun getCurrentMinutes(): Int {
    return Calendar.getInstance()
        .get(Calendar.HOUR_OF_DAY) * 60 + Calendar.getInstance()
        .get(Calendar.MINUTE)
}

@Composable
private fun getMinutes(): Int {
    var value by remember { mutableIntStateOf(getCurrentMinutes()) }

    DisposableEffect(Unit) {
        val handler = Handler(Looper.getMainLooper())

        val runnable = {
            value = getCurrentMinutes()
        }

        handler.postDelayed(runnable, 60_000)

        onDispose {
            handler.removeCallbacks(runnable)
        }
    }

    return value
}

@Preview(showBackground = true)
@Composable
fun DayCard(
    modifier: Modifier = Modifier,
    day: Day? = FakeScheduleRepository.exampleGroup.days[0],
    current: Boolean = true
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if (current) MaterialTheme.colorScheme.surfaceContainerHighest else MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        if (day == null) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
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

        val currentMinutes = getMinutes()

        val isCurrentLesson: (lesson: Lesson) -> Boolean = {
            current
                    && it.time != null
                    && currentMinutes >= it.time.start
                    && currentMinutes <= it.time.end
        }

        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.5.dp)
        ) {
            if (day.nonNullIndices.isEmpty()) {
                Text("Can't get schedule!")
            } else {
                val defaultCardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                val customCardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
                val noneCardColors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,

                    )

                for (i in day.nonNullIndices.first()..day.nonNullIndices.last()) {
                    val lesson = day.lessons[i]!!

                    val cardColors = when (lesson.type) {
                        LessonType.DEFAULT -> defaultCardColors
                        LessonType.CUSTOM -> customCardColors
                    }

                    val mutableExpanded = remember { mutableStateOf(false) }

                    val lessonBoxModifier = remember {
                        Modifier
                            .padding(PaddingValues(2.5.dp, 0.dp))
                            .clickable { mutableExpanded.value = true }
                            .background(cardColors.containerColor)
                    }

                    Box(
                        modifier = if (isCurrentLesson(lesson)) lessonBoxModifier.border(
                            border = BorderStroke(
                                3.5.dp,
                                Color(
                                    cardColors.containerColor.red * 0.5F,
                                    cardColors.containerColor.green * 0.5F,
                                    cardColors.containerColor.blue * 0.5F,
                                    1F
                                )
                            )
                        ) else lessonBoxModifier
                    ) {
                        LessonRow(
                            day, lesson, cardColors
                        )
                    }
                    if (i != day.nonNullIndices.last()) {
                        Box(
                            modifier = Modifier
                                .padding(PaddingValues(2.5.dp, 0.dp))
                                .background(noneCardColors.containerColor)
                        ) {
                            FreeLessonRow(
                                lesson,
                                day.lessons[day.nonNullIndices[day.nonNullIndices.indexOf(i) + 1]]!!,
                                noneCardColors
                            )
                        }
                    }

                    if (mutableExpanded.value) LessonExtraInfo(
                        lesson, mutableExpanded
                    )
                }
            }
        }
    }
}