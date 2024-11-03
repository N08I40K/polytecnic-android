package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.LocalDateTime
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonTime
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.model.SubGroup
import ru.n08i40k.polytechnic.next.utils.dayMinutes
import ru.n08i40k.polytechnic.next.utils.fmtAsClock

private enum class LessonTimeFormat {
    FROM_TO, ONLY_MINUTES_DURATION
}

@Composable
private fun fmtTime(start: Int, end: Int, format: LessonTimeFormat): ArrayList<String> {
    return when (format) {
        LessonTimeFormat.FROM_TO -> {
            val startClock = start.fmtAsClock()
            val endClock = end.fmtAsClock()

            arrayListOf(startClock, endClock)
        }

        LessonTimeFormat.ONLY_MINUTES_DURATION -> {
            val duration = end - start

            arrayListOf("$duration " + stringResource(R.string.minutes))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LessonExtraInfo(
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0].lessons[0],
    mutableExpanded: MutableState<Boolean> = mutableStateOf(true)
) {
    Dialog(onDismissRequest = { mutableExpanded.value = false }) {
        if (lesson.type === LessonType.BREAK) {
            mutableExpanded.value = false
            return@Dialog
        }

        Card {
            Column(Modifier.padding(10.dp)) {
                Text(lesson.name!!)

                for (subGroup in lesson.subGroups) {
                    val subGroups = buildString {
                        append("[")
                        append(subGroup.number)
                        append("] ")
                        append(subGroup.teacher)
                        append(" - ")
                        append(subGroup.cabinet)
                    }
                    Text(subGroups)
                }

                val duration = buildString {
                    append(stringResource(R.string.lesson_duration))
                    append(" - ")
                    val duration =
                        lesson.time.end.dayMinutes - lesson.time.start.dayMinutes

                    append(duration / 60)
                    append(stringResource(R.string.hours))
                    append(" ")
                    append(duration % 60)
                    append(stringResource(R.string.minutes))
                }
                Text(duration)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonViewRow(
    range: List<Int>? = listOf(1, 3),
    time: LessonTime = LessonTime.fromLocalDateTime(
        LocalDateTime(2024, 1, 1, 0, 0),
        LocalDateTime(2024, 1, 1, 1, 0),
    ),
    timeFormat: LessonTimeFormat = LessonTimeFormat.FROM_TO,
    name: String = "Test",
    subGroups: List<SubGroup> = listOf(),
    group: String? = "ИС-214/23",
    cardColors: CardColors = CardDefaults.cardColors(),
    verticalPadding: Dp = 10.dp,
    now: Boolean = true,
) {
    val contentColor =
        if (timeFormat == LessonTimeFormat.FROM_TO) cardColors.contentColor
        else cardColors.disabledContentColor

    val rangeSize = if (range == null) 1 else (range[1] - range[0] + 1) * 2

    Box(
        if (now) Modifier.border(
            BorderStroke(
                3.5.dp,
                Color(
                    cardColors.containerColor.red * 0.5F,
                    cardColors.containerColor.green * 0.5F,
                    cardColors.containerColor.blue * 0.5F,
                    1F
                )
            )
        ) else Modifier
    ) {
        Row(
            modifier = Modifier.padding(10.dp, verticalPadding * rangeSize),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val rangeString = run {
                if (range == null)
                    "   "
                else
                    buildString {
                        val same = range[0] == range[1]

                        append(if (same) " " else range[0])
                        append(if (same) range[0] else "-")
                        append(if (same) " " else range[1])
                    }
            }
            Text(
                text = rangeString,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            Column(
                modifier = Modifier.fillMaxWidth(0.20f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val formattedTime: ArrayList<String> =
                    fmtTime(time.start.dayMinutes, time.end.dayMinutes, timeFormat)

                Text(
                    text = formattedTime[0],
                    fontFamily = FontFamily.Monospace,
                    color = contentColor
                )

                if (formattedTime.count() > 1) {
                    Text(
                        text = formattedTime[1],
                        fontFamily = FontFamily.Monospace,
                        color = contentColor
                    )
                }
            }

            Column(verticalArrangement = Arrangement.Center) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor
                        )

                        if (group != null) {
                            Text(
                                text = group,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = contentColor
                            )
                        }

                        for (subGroup in subGroups) {
                            Text(
                                text = subGroup.teacher,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = contentColor
                            )
                        }
                    }

                    Column(modifier = Modifier.wrapContentWidth()) {
                        if (subGroups.size != 1) {
                            for (i in 0..<(if (group != null) 2 else 1))
                                Text(text = "")
                        }
                        for (subGroup in subGroups) {
                            Text(
                                text = subGroup.cabinet,
                                maxLines = 1,
                                fontFamily = FontFamily.Monospace,
                                color = contentColor
                            )
                        }
                    }
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FreeLessonRow(
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0].lessons[0],
    nextLesson: Lesson = FakeScheduleRepository.exampleGroup.days[0].lessons[1],
    cardColors: CardColors = CardDefaults.cardColors(),
    now: Boolean = true
) {
    LessonViewRow(
        lesson.defaultRange,
        LessonTime(lesson.time.start, nextLesson.time.end),
        LessonTimeFormat.ONLY_MINUTES_DURATION,
        stringResource(R.string.lesson_break),
        lesson.subGroups,
        lesson.group,
        cardColors,
        2.5.dp,
        now
    )
}

@Preview(showBackground = true)
@Composable
fun LessonRow(
    day: Day = FakeScheduleRepository.exampleGroup.days[0],
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0].lessons[0],
    cardColors: CardColors = CardDefaults.cardColors(),
    now: Boolean = true,
) {
    LessonViewRow(
        lesson.defaultRange,
        lesson.time,
        LessonTimeFormat.FROM_TO,
        lesson.name!!,
        lesson.subGroups,
        lesson.group,
        cardColors,
        5.dp,
        now
    )
}