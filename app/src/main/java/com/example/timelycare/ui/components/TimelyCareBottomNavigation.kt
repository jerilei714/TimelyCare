package com.example.timelycare.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.timelycare.ui.theme.*

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun TimelyCareBottomNavigation(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Dashboard", Icons.Default.Home),
        BottomNavItem("Medications", Icons.Default.Add),
        BottomNavItem("Calendar", Icons.Default.DateRange),
        BottomNavItem("Contacts", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = TimelyCareWhite,
        contentColor = TimelyCareGray
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TimelyCareBlue,
                    selectedTextColor = TimelyCareBlue,
                    unselectedIconColor = TimelyCareGray,
                    unselectedTextColor = TimelyCareGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}