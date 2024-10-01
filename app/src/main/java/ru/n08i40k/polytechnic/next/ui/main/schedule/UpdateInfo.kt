package ru.n08i40k.polytechnic.next.ui.main.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.UpdateDates
import ru.n08i40k.polytechnic.next.ui.ExpandableCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

val expanded = mutableStateOf(false)

@Preview(showBackground = true)
@Composable
fun UpdateInfo(
    lastUpdateAt: Long = 0,
    updateDates: UpdateDates = UpdateDates.newBuilder().build()
) {
    var expanded by remember { expanded }

    val format = "HH:mm:ss dd.MM.yyyy"

    val currentDate = Date(lastUpdateAt).toString(format)
    val cacheUpdateDate = Date(updateDates.cache).toString(format)
    val scheduleUpdateDate = Date(updateDates.schedule).toString(format)

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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.last_local_update))
                Text(
                    text = currentDate,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.last_server_cache_update))
                Text(
                    text = cacheUpdateDate,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.last_server_schedule_update))
                Text(
                    text = scheduleUpdateDate,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

        }
    }
}