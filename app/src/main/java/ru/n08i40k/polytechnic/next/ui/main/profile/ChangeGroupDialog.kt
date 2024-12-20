package ru.n08i40k.polytechnic.next.ui.main.profile

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.android.volley.ClientError
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.data.users.impl.FakeProfileRepository
import ru.n08i40k.polytechnic.next.model.Profile
import ru.n08i40k.polytechnic.next.model.UserRole
import ru.n08i40k.polytechnic.next.network.request.profile.ProfileChangeGroup
import ru.n08i40k.polytechnic.next.ui.widgets.GroupSelector

private enum class ChangeGroupError {
    NOT_EXISTS
}

private fun tryChangeGroup(
    context: Context,
    group: String,
    onError: (ChangeGroupError) -> Unit,
    onSuccess: (String) -> Unit
) {
    ProfileChangeGroup(ProfileChangeGroup.RequestDto(group), context, {
        onSuccess(group)
    }, {
        if (it is ClientError && it.networkResponse.statusCode == 404)
            onError(ChangeGroupError.NOT_EXISTS)
        else throw it
    }).send()
}

@Preview(showBackground = true)
@Composable
internal fun ChangeGroupDialog(
    context: Context = LocalContext.current,
    profile: Profile = FakeProfileRepository.exampleProfile,
    onChange: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            var group by remember { mutableStateOf<String?>(profile.group) }
            var groupError by remember { mutableStateOf(false) }

            var processing by remember { mutableStateOf(false) }

            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                val modifier = Modifier.fillMaxWidth()

                GroupSelector(
                    value = group,
                    isError = groupError,
                    readOnly = processing,
                    teacher = profile.role == UserRole.TEACHER
                ) { group = it }

                val focusManager = LocalFocusManager.current
                Button(
                    modifier = modifier,
                    onClick = {
                        processing = true
                        focusManager.clearFocus()

                        tryChangeGroup(
                            context = context,
                            group = group!!,
                            onError = {
                                when (it) {
                                    ChangeGroupError.NOT_EXISTS -> {
                                        groupError = true
                                    }
                                }

                                processing = false
                            },
                            onSuccess = onChange
                        )
                    },
                    enabled = !(groupError || processing) && group != null
                ) {
                    Text(stringResource(R.string.change_group))
                }
            }
        }
    }
}