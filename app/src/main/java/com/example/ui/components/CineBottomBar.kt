package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.CineTab

@Composable
fun CineBottomBar(
    activeTab: CineTab,
    onTabSelected: (CineTab) -> Unit
) {
    NavigationBar(
        containerColor = CinemaSurface,
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 6.dp
    ) {
        // Tab 1: Character Vault
        NavigationBarItem(
            selected = activeTab == CineTab.VAULT,
            onClick = { onTabSelected(CineTab.VAULT) },
            icon = {
                Icon(
                    imageVector = if (activeTab == CineTab.VAULT) Icons.Filled.Face else Icons.Outlined.Face,
                    contentDescription = "Character Vault"
                )
            },
            label = { Text("Vault", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CinemaBackground,
                selectedTextColor = AmberGoldPrimary,
                indicatorColor = AmberGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_vault")
        )

        // Tab 2: Sets & Locations Vault
        NavigationBarItem(
            selected = activeTab == CineTab.SETS,
            onClick = { onTabSelected(CineTab.SETS) },
            icon = {
                Icon(
                    imageVector = if (activeTab == CineTab.SETS) Icons.Filled.LocationCity else Icons.Outlined.LocationCity,
                    contentDescription = "Master Sets Vault"
                )
            },
            label = { Text("Sets", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CinemaBackground,
                selectedTextColor = AmberGoldPrimary,
                indicatorColor = AmberGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_sets")
        )

        // Tab 3: Sequencer / Shotboard
        NavigationBarItem(
            selected = activeTab == CineTab.SEQUENCER,
            onClick = { onTabSelected(CineTab.SEQUENCER) },
            icon = {
                Icon(
                    imageVector = if (activeTab == CineTab.SEQUENCER) Icons.Filled.Movie else Icons.Outlined.Movie,
                    contentDescription = "Shotboard Sequencer"
                )
            },
            label = { Text("Sequencer", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CinemaBackground,
                selectedTextColor = AmberGoldPrimary,
                indicatorColor = AmberGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_sequencer")
        )

        // Tab 3: Studio / Omni Flash Prompt Generator
        NavigationBarItem(
            selected = activeTab == CineTab.STUDIO,
            onClick = { onTabSelected(CineTab.STUDIO) },
            icon = {
                Icon(
                    imageVector = if (activeTab == CineTab.STUDIO) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "Omni Flash Studio"
                )
            },
            label = { Text("Omni Studio", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CinemaBackground,
                selectedTextColor = AmberGoldPrimary,
                indicatorColor = AmberGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_studio")
        )

        // Tab 4: Universe Bible
        NavigationBarItem(
            selected = activeTab == CineTab.BIBLE,
            onClick = { onTabSelected(CineTab.BIBLE) },
            icon = {
                Icon(
                    imageVector = if (activeTab == CineTab.BIBLE) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                    contentDescription = "Universe Bible"
                )
            },
            label = { Text("Bible", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CinemaBackground,
                selectedTextColor = AmberGoldPrimary,
                indicatorColor = AmberGoldPrimary,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_bible")
        )
    }
}
