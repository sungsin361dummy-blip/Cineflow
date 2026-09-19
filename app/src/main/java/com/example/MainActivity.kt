package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CineBottomBar
import com.example.ui.components.CineTopBar
import com.example.ui.screens.CharacterVaultScreen
import com.example.ui.screens.LocationVaultScreen
import com.example.ui.screens.OmniFlashStudioScreen
import com.example.ui.screens.ShotboardScreen
import com.example.ui.screens.UniverseBibleScreen
import com.example.ui.theme.CineFlowTheme
import com.example.ui.theme.CinemaBackground
import com.example.viewmodel.CineFlowViewModel
import com.example.viewmodel.CineTab

class MainActivity : ComponentActivity() {

    private val viewModel: CineFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CineFlowTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        CineTopBar(
                            seriesTitle = uiState.universeBible.title,
                            genre = uiState.universeBible.genre
                        )
                    },
                    bottomBar = {
                        CineBottomBar(
                            activeTab = uiState.activeTab,
                            onTabSelected = { tab -> viewModel.selectTab(tab) }
                        )
                    },
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = CinemaBackground,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CinemaBackground)
                            .padding(innerPadding)
                    ) {
                        Crossfade(
                            targetState = uiState.activeTab,
                            animationSpec = tween(220),
                            label = "tab_crossfade"
                        ) { tab ->
                            when (tab) {
                                CineTab.VAULT -> {
                                    CharacterVaultScreen(
                                        characters = uiState.characters,
                                        bible = uiState.universeBible,
                                        onSaveCharacter = { character, photos ->
                                            viewModel.saveCharacterWithPhotos(character, photos)
                                        },
                                        onDeleteCharacter = { id -> viewModel.deleteCharacter(id) },
                                        onCopyToClipboard = { label, text ->
                                            viewModel.copyToClipboard(label, text)
                                        },
                                        isEditing = uiState.isEditingCharacter,
                                        characterUnderEdit = uiState.characterUnderEdit,
                                        onOpenEditor = { char -> viewModel.openCharacterEditor(char) },
                                        onCloseEditor = { viewModel.closeCharacterEditor() }
                                    )
                                }

                                CineTab.SETS -> {
                                    LocationVaultScreen(
                                        locations = uiState.locations,
                                        onSaveLocation = { location, photos ->
                                            viewModel.saveLocationWithPhotos(location, photos)
                                        },
                                        onDeleteLocation = { id -> viewModel.deleteLocation(id) },
                                        onCopyToClipboard = { label, text ->
                                            viewModel.copyToClipboard(label, text)
                                        },
                                        isEditing = uiState.isEditingLocation,
                                        locationUnderEdit = uiState.locationUnderEdit,
                                        onOpenEditor = { loc -> viewModel.openLocationEditor(loc) },
                                        onCloseEditor = { viewModel.closeLocationEditor() }
                                    )
                                }

                                CineTab.SEQUENCER -> {
                                    ShotboardScreen(
                                        scenes = uiState.allScenes,
                                        selectedScene = uiState.selectedScene,
                                        shots = uiState.sceneShots,
                                        characters = uiState.characters,
                                        locations = uiState.locations,
                                        bible = uiState.universeBible,
                                        onSelectScene = { scene -> viewModel.selectScene(scene) },
                                        onOpenCreateSceneDialog = { viewModel.openCreateSceneDialog() },
                                        isCreatingSceneDialog = uiState.isCreatingSceneDialog,
                                        onCloseCreateSceneDialog = { viewModel.closeCreateSceneDialog() },
                                        onCreateScene = { title -> viewModel.createScene(title) },
                                        onOpenAutoDirectDialog = { viewModel.openAutoDirectDialog() },
                                        isAutoDirectDialog = uiState.isAutoDirectDialog,
                                        isAutoDirecting = uiState.isAutoDirecting,
                                        onCloseAutoDirectDialog = { viewModel.closeAutoDirectDialog() },
                                        onAutoDirect = { premise, genre, count ->
                                            viewModel.autoDirectScene(premise, genre, count)
                                        },
                                        isAddingShot = uiState.isAddingShot,
                                        shotUnderEdit = uiState.shotUnderEdit,
                                        onOpenShotEditor = { shot -> viewModel.openShotEditor(shot) },
                                        onCloseShotEditor = { viewModel.closeShotEditor() },
                                        onSaveShot = { shot -> viewModel.saveShot(shot) },
                                        onDeleteShot = { id -> viewModel.deleteShot(id) },
                                        onCopyToClipboard = { label, text ->
                                            viewModel.copyToClipboard(label, text)
                                        },
                                        onLoadEpisodeTemplate = { ep ->
                                            viewModel.loadEpisodeTemplate(ep)
                                        }
                                    )
                                }

                                CineTab.STUDIO -> {
                                    OmniFlashStudioScreen(
                                        selectedMode = uiState.selectedPromptMode,
                                        generatedOutputText = uiState.generatedOutputText,
                                        selectedScene = uiState.selectedScene,
                                        onSelectMode = { mode -> viewModel.selectPromptMode(mode) },
                                        onCopyToClipboard = { label, text ->
                                            viewModel.copyToClipboard(label, text)
                                        }
                                    )
                                }

                                CineTab.BIBLE -> {
                                    UniverseBibleScreen(
                                        bible = uiState.universeBible,
                                        onSaveBible = { bible -> viewModel.saveUniverseBible(bible) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
