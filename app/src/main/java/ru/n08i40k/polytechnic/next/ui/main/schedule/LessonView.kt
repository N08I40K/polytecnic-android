package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonTime
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
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0]!!.lessons[0]!!,
    mutableExpanded: MutableState<Boolean> = mutableStateOf(true)
) {
    Dialog(onDismissRequest = { mutableExpanded.value = false }) {
        Card {
            Column(Modifier.padding(10.dp)) {
                Text(lesson.name)

                if (lesson.teacherNames.isNotEmpty()) {
                    val teachers = buildString {
                        append(stringResource(if (lesson.teacherNames.count() > 1) R.string.lesson_teachers else R.string.lesson_teacher))
                        append(" - ")
                        append(lesson.teacherNames.joinToString(", "))
                    }
                    Text(teachers)
                }

                val duration = buildString {
                    append(stringResource(R.string.lesson_duration))
                    append(" - ")
                    val duration =
                        if (lesson.time != null) lesson.time.end - lesson.time.start else 0

                    append(duration / 60)
                    append(stringResource(R.string.hours))
                    append(" ")
                    append(duration % 60)
                    append(stringResource(R.string.minutes))
                }
                Text(duration)

                if (lesson.cabinets.isNotEmpty()) {
                    val cabinets = buildString {
                        append(stringResource(R.string.cabinets))
                        append(" - ")
                        append(lesson.cabinets.joinToString(", "))
                    }
                    Text(cabinets)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LessonViewRow(
    idx: Int = 1,
    time: LessonTime? = LessonTime(0, 60),
    timeFormat: LessonTimeFormat = LessonTimeFormat.FROM_TO,
    name: String = "Test",
    teacherNames: ArrayList<String> = arrayListOf(
        "Хомченко Н.Е. (1 подggggggggggggggggggggggggggggggggggggggгруппа)",
        "Хомченко Н.Е. (2 подгруппа)"
    ),
    cabinets: ArrayList<String> = arrayListOf("14", "31"),
    cardColors: CardColors = CardDefaults.cardColors(),
    verticalPadding: Dp = 10.dp
) {
    val contentColor =
        if (timeFormat == LessonTimeFormat.FROM_TO) cardColors.contentColor else cardColors.disabledContentColor

    val teacherNamesRepl = teacherNames.map { it.replace("подгруппа", "подгр.") }

    Row(
        modifier = Modifier.padding(10.dp, verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (idx == -1) "1" else idx.toString(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (idx == -1) Color(0) else contentColor
        )

        Spacer(Modifier.width(7.5.dp))

        if (time != null) {
            val formattedTime: ArrayList<String> = fmtTime(time.start, time.end, timeFormat)

            Column(
                modifier = Modifier.fillMaxWidth(0.25f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formattedTime[0], fontFamily = FontFamily.Monospace, color = contentColor
                )
                if (formattedTime.count() > 1) {
                    Text(
                        text = formattedTime[1],
                        fontFamily = FontFamily.Monospace,
                        color = contentColor
                    )
                }
            }
        }

        Spacer(Modifier.width(7.5.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = contentColor
                    )

                    for (teacherName in teacherNamesRepl) {
                        Text(
                            text = teacherName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor
                        )
                    }
                }

                Column(modifier = Modifier.wrapContentWidth()) {
                    if (cabinets.size <= teacherNamesRepl.size) {
                        Text(
                            text = "",
                            maxLines = 1
                        )
                    }
                    for (listIdx: Int in 0..<cabinets.size) {
                        Text(
                            text = cabinets[listIdx],
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = contentColor
                        )
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun FreeLessonRow(
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0]!!.lessons[0]!!,
    nextLesson: Lesson = FakeScheduleRepository.exampleGroup.days[0]!!.lessons[1]!!,
    cardColors: CardColors = CardDefaults.cardColors()
) {
    LessonViewRow(
        -1,
        if (lesson.time != null && nextLesson.time != null) LessonTime(
            lesson.time.end, nextLesson.time.start
        ) else null,
        LessonTimeFormat.ONLY_MINUTES_DURATION,
        stringResource(R.string.lesson_break),
        arrayListOf(),
        arrayListOf(),
        cardColors,
        2.5.dp
    )
}

@Preview(showBackground = true)
@Composable
fun LessonRow(
    day: Day = FakeScheduleRepository.exampleGroup.days[0]!!,
    lesson: Lesson = FakeScheduleRepository.exampleGroup.days[0]!!.lessons[0]!!,
    cardColors: CardColors = CardDefaults.cardColors()
) {
    LessonViewRow(
        lesson.defaultIndex,
        lesson.time,
        LessonTimeFormat.FROM_TO,
        lesson.name,
        lesson.teacherNames,
        lesson.cabinets,
        cardColors,
        5.dp
    )
}