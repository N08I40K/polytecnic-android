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
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonType
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

            arrayListOf("$duration" + stringResource(R.string.minutes))
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
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0].lessons[4],
    timeFormat: LessonTimeFormat = LessonTimeFormat.FROM_TO,
    cardColors: CardColors = CardDefaults.cardColors(),
    verticalPadding: Dp = 10.dp,
    now: Boolean = true,
) {
    val contentColor =
        if (timeFormat == LessonTimeFormat.FROM_TO) cardColors.contentColor
        else cardColors.disabledContentColor

    val rangeSize =
        if (lesson.defaultRange == null) 1
        else (lesson.defaultRange[1] - lesson.defaultRange[0] + 1) * 2

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
                if (lesson.defaultRange == null)
                    "   "
                else
                    buildString {
                        val same = lesson.defaultRange[0] == lesson.defaultRange[1]

                        append(if (same) " " else lesson.defaultRange[0])
                        append(if (same) lesson.defaultRange[0] else "-")
                        append(if (same) " " else lesson.defaultRange[1])
                    }
            }
            // 1-2
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
                    fmtTime(lesson.time.start.dayMinutes, lesson.time.end.dayMinutes, timeFormat)

                // 10:20 - 11:40
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
                        if (lesson.type.value > LessonType.BREAK.value) {
                            Text(
                                text = when (lesson.type) {
                                    LessonType.CONSULTATION -> stringResource(R.string.lesson_type_consultation)
                                    LessonType.INDEPENDENT_WORK -> stringResource(R.string.lesson_type_independent_work)
                                    LessonType.EXAM -> stringResource(R.string.lesson_type_exam)
                                    LessonType.EXAM_WITH_GRADE -> stringResource(R.string.lesson_type_exam_with_grade)
                                    else -> throw Error("Unknown lesson type!")
                                },
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = contentColor
                            )
                        }
                        Text(
                            text = lesson.name ?: stringResource(R.string.lesson_break),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor
                        )

                        if (lesson.group != null) {
                            Text(
                                text = lesson.group,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = contentColor
                            )
                        }

                        for (subGroup in lesson.subGroups) {
                            Text(
                                text = subGroup.teacher,
                                fontWeight = FontWeight.Thin,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = contentColor
                            )
                        }
                    }

                    Column(modifier = Modifier.wrapContentWidth()) {
                        if (lesson.subGroups.size != 1) {
                            Text(text = "")

                            if (lesson.group != null)
                                Text(text = "")
                        }
                        for (subGroup in lesson.subGroups) {
                            Text(
                                text = subGroup.cabinet,
                                maxLines = 1,
                                fontWeight = FontWeight.Thin,
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
    cardColors: CardColors = CardDefaults.cardColors(),
    now: Boolean = true
) {
    LessonViewRow(
        lesson,
        LessonTimeFormat.ONLY_MINUTES_DURATION,
        cardColors,
        2.5.dp,
        now
    )
}

@Preview(showBackground = true)
@Composable
fun LessonRow(
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0].lessons[0],
    cardColors: CardColors = CardDefaults.cardColors(),
    now: Boolean = true,
) {
    LessonViewRow(
        lesson,
        LessonTimeFormat.FROM_TO,
        cardColors,
        5.dp,
        now
    )
}