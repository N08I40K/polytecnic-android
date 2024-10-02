package ru.n08i40k.polytechnic.next.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.utils.EnumAsStringSerializer

private class UserRoleStringSerializer : EnumAsStringSerializer<UserRole>(
    "UserRole",
    { it.value },
    { v -> UserRole.entries.first { it.value == v } }
)


@Serializable(with = UserRoleStringSerializer::class)
enum class UserRole(val value: String, val icon: ImageVector, @StringRes val stringId: Int) {
    STUDENT("STUDENT", Icons.Filled.Face, R.string.role_student),
    TEACHER("TEACHER", Icons.Filled.Person, R.string.role_teacher),
    ADMIN("ADMIN", Icons.Filled.Settings, R.string.role_admin);

    companion object {
        val AcceptableUserRoles = listOf(STUDENT, TEACHER)
    }
}