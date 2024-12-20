package ru.n08i40k.polytechnic.next.ui.main.schedule.teacher.user

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.network.request.schedule.ScheduleGetTeacherNames

@Composable
private fun getTeacherNames(context: Context): ArrayList<String> {
    val teacherNames = remember { arrayListOf<String>() }

    LaunchedEffect(teacherNames) {
        ScheduleGetTeacherNames(context, {
            teacherNames.clear()
            teacherNames.addAll(it.names)
        }, {
            teacherNames.clear()
        }).send()
    }

    return teacherNames
}

@Preview(showBackground = true)
@Composable
fun TeacherSearchBox(
    onSearchAttempt: (String) -> Unit = {},
) {
    val teachers = getTeacherNames(LocalContext.current)
    val focusManager = LocalFocusManager.current

    SearchBox(
        stringResource(R.string.teacher_name),
        {
            focusManager.clearFocus(true)
            onSearchAttempt(it)
        },
        teachers,
    )
}