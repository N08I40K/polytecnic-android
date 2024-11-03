package ru.n08i40k.polytechnic.next.ui.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import ru.n08i40k.polytechnic.next.R

data class BottomNavItem(
    @StringRes val label: Int,
    val icon: ImageVector,
    val route: String,
    val isAdmin: Boolean = false
)

object Constants {
    val bottomNavItem = listOf(
        BottomNavItem(R.string.profile, Icons.Filled.AccountCircle, "profile"),
        BottomNavItem(R.string.replacer, Icons.Filled.Create, "replacer", true),
        BottomNavItem(R.string.schedule, Icons.Filled.DateRange, "schedule"),
        BottomNavItem(R.string.teachers, Icons.Filled.Person, "teacher-schedule")
    )
}