package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.runBlocking
import ru.n08i40k.polytechnic.next.MainViewModel
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.cache.NetworkCacheRepository
import ru.n08i40k.polytechnic.next.data.cache.impl.FakeNetworkCacheRepository
import ru.n08i40k.polytechnic.next.ui.ExpandableCard
import ru.n08i40k.polytechnic.next.ui.model.ScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

val expanded = mutableStateOf(false)

@Preview(showBackground = true)
@Composable
fun UpdateInfo(networkCacheRepository: NetworkCacheRepository = FakeNetworkCacheRepository()) {
    var expanded by remember { expanded }

    val format = "hh:mm:ss dd.MM.yyyy"

    val updateDates = remember { runBlocking { networkCacheRepository.getUpdateDates() } }

    val currentDate = remember { getCurrentDateTime().toString(format) }
    val cacheUpdateDate = remember { Date(updateDates.cache).toString(format) }
    val scheduleUpdateDate = remember { Date(updateDates.schedule).toString(format) }

    ExpandableCard(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        title = stringResource(R.string.update_info_header)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.last_local_update) + " - ")
                Text(text = currentDate, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.last_server_cache_update) + " - ")
                Text(text = cacheUpdateDate, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.last_server_schedule_update) + " - ")
                Text(text = scheduleUpdateDate, fontWeight = FontWeight.Bold)
            }

        }
    }
}