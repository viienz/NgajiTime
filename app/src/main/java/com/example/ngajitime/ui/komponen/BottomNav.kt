package com.example.ngajitime.ui.komponen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class MenuAktif { BERANDA, SURAH, STATS, PROFIL }

@Composable
fun NgajiBottomBar(
    menuAktif: MenuAktif,
    onKeBeranda: () -> Unit,
    onKeSurah: () -> Unit,
    onKeStats: () -> Unit,
    onKeProfil: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        //BERANDA
        ItemNav(
            label = "Beranda",
            icon = Icons.Default.Home,
            isActive = menuAktif == MenuAktif.BERANDA,
            onClick = onKeBeranda
        )
        //SURAH
        ItemNav(
            label = "Surah",
            icon = Icons.Default.MenuBook,
            isActive = menuAktif == MenuAktif.SURAH,
            onClick = onKeSurah
        )
        //STATS
        ItemNav(
            label = "Stats",
            icon = Icons.Default.BarChart,
            isActive = menuAktif == MenuAktif.STATS,
            onClick = onKeStats
        )
        //PROFIL
        ItemNav(
            label = "Profil",
            icon = Icons.Default.Person,
            isActive = menuAktif == MenuAktif.PROFIL,
            onClick = onKeProfil
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ItemNav(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isActive,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF2E7D32),
            selectedTextColor = Color(0xFF2E7D32),
            indicatorColor = Color(0xFFE8F5E9)
        )
    )
}