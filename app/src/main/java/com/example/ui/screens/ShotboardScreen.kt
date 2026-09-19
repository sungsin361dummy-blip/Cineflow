package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.CharacterEntity
import com.example.data.local.LocationEntity
import com.example.data.local.SceneShotEntity
import com.example.data.local.UniverseBibleEntity
import com.example.domain.CineFlowPromptEngine
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AnamorphicCyan
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaBorder
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ContinuityEmerald
import com.example.ui.theme.DirectorCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotboardScreen(
    scenes: List<String>,
    selectedScene: String,
    shots: List<SceneShotEntity>,
    characters: List<CharacterEntity>,
    locations: List<LocationEntity> = emptyList(),
    bible: UniverseBibleEntity,
    onSelectScene: (String) -> Unit,
    onOpenCreateSceneDialog: () -> Unit,
    isCreatingSceneDialog: Boolean,
    onCloseCreateSceneDialog: () -> Unit,
    onCreateScene: (String) -> Unit,
    onOpenAutoDirectDialog: () -> Unit = {},
    isAutoDirectDialog: Boolean = false,
    isAutoDirecting: Boolean = false,
    onCloseAutoDirectDialog: () -> Unit = {},
    onAutoDirect: (premise: String, genre: String, shotCount: Int) -> Unit = { _, _, _ -> },
    isAddingShot: Boolean,
    shotUnderEdit: SceneShotEntity?,
    onOpenShotEditor: (SceneShotEntity?) -> Unit,
    onCloseShotEditor: () -> Unit,
    onSaveShot: (SceneShotEntity) -> Unit,
    onDeleteShot: (Long) -> Unit,
    onCopyToClipboard: (String, String) -> Unit,
    onLoadEpisodeTemplate: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = CinemaBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenShotEditor(null) },
                containerColor = AmberGoldPrimary,
                contentColor = CinemaBackground,
                modifier = Modifier.testTag("fab_add_shot")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Shot")
                    Text("Add Shot", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            // Episode Architecture & 9-Scene Rule Indicator
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CinemaSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(AnamorphicCyan.copy(alpha = 0.5f), CinemaBorder)
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🎬", fontSize = 16.sp)
                                Text(
                                    text = "9:16 VERTICAL MICRO-SERIES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AnamorphicCyan,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AmberGoldPrimary.copy(alpha = 0.18f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "8s MAX / SHOT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGoldPrimary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Structure: 9 Scenes/Ep • Vertical 9:16 Framing • Total ~72s Pacing",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            if (onLoadEpisodeTemplate != null) {
                                TextButton(
                                    onClick = { onLoadEpisodeTemplate(2) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("+ New Ep (9 Sc)", fontSize = 11.sp, color = AnamorphicCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Scene Selector Horizontal Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EPISODE SCENES (9 SCENE PACING)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${scenes.size} scenes available",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(scenes) { scene ->
                            val isSelected = scene == selectedScene
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) AmberGoldPrimary else CinemaSurface
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AmberGoldPrimary else CinemaBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onSelectScene(scene) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = scene,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CinemaSurfaceVariant)
                                    .border(1.dp, CinemaBorder, RoundedCornerShape(8.dp))
                                    .clickable { onOpenCreateSceneDialog() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "New Scene",
                                        tint = AmberGoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "New Scene",
                                        color = AmberGoldPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberGoldPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, AmberGoldPrimary, RoundedCornerShape(8.dp))
                                    .clickable { onOpenAutoDirectDialog() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("✨", fontSize = 13.sp)
                                    Text(
                                        text = "AI Auto-Direct",
                                        color = AmberGoldPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Scene Header & Overview Stats
            item {
                val totalSeconds = shots.sumOf { it.durationSeconds }
                SceneHeaderCard(
                    sceneTitle = selectedScene,
                    shotCount = shots.size,
                    totalSeconds = totalSeconds,
                    aspectRatio = bible.aspectRatio,
                    cameraRig = bible.cameraLensSpec
                )
            }

            // Shots list
            if (shots.isEmpty()) {
                item {
                    EmptyShotsPlaceholder(onAdd = { onOpenShotEditor(null) })
                }
            } else {
                items(shots, key = { it.id }) { shot ->
                    val matchedChar = characters.find { it.codenameToken == shot.characterToken }
                    val matchedLoc = locations.find { it.setToken == shot.locationToken }
                    ShotCard(
                        shot = shot,
                        character = matchedChar,
                        location = matchedLoc,
                        bible = bible,
                        onCopyImagePrompt = {
                            val prompt = CineFlowPromptEngine.buildImagePrompt(bible, shot, matchedChar, matchedLoc)
                            onCopyToClipboard("Shot ${shot.shotNumber} Image Prompt", prompt)
                        },
                        onCopyVideoPrompt = {
                            val prompt = CineFlowPromptEngine.buildVideoMotionPrompt(bible, shot, matchedChar, matchedLoc)
                            onCopyToClipboard("Shot ${shot.shotNumber} Video Motion Prompt", prompt)
                        },
                        onEdit = { onOpenShotEditor(shot) },
                        onDelete = { onDeleteShot(shot.id) }
                    )
                }
            }
        }

        // New Scene Dialog
        if (isCreatingSceneDialog) {
            CreateSceneDialog(
                onDismiss = onCloseCreateSceneDialog,
                onCreate = onCreateScene
            )
        }

        // Add / Edit Shot Dialog
        if (isAddingShot) {
            ShotEditorDialog(
                sceneTitle = selectedScene,
                initialShot = shotUnderEdit,
                characters = characters,
                locations = locations,
                nextShotNumber = (shots.maxOfOrNull { it.shotNumber } ?: 0) + 1,
                onDismiss = onCloseShotEditor,
                onSave = onSaveShot
            )
        }

        // AI Auto-Direct Storyboard Dialog
        if (isAutoDirectDialog) {
            AutoDirectStoryDialog(
                isDirecting = isAutoDirecting,
                onDismiss = onCloseAutoDirectDialog,
                onDirect = onAutoDirect
            )
        }
    }
}

@Composable
fun SceneHeaderCard(
    sceneTitle: String,
    shotCount: Int,
    totalSeconds: Int,
    aspectRatio: String,
    cameraRig: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
            listOf(CinemaBorder, CinemaBorder.copy(alpha = 0.5f))
        )),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sceneTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AmberGoldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$shotCount SHOTS",
                            color = AmberGoldPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnamorphicCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⏱️ ${totalSeconds}s",
                            color = AnamorphicCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ratio: $aspectRatio",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = "Max 8s/Shot",
                    fontSize = 11.sp,
                    color = AmberGoldPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "•",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = cameraRig,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ShotCard(
    shot: SceneShotEntity,
    character: CharacterEntity?,
    location: LocationEntity? = null,
    bible: UniverseBibleEntity,
    onCopyImagePrompt: () -> Unit,
    onCopyVideoPrompt: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
            listOf(CinemaBorder, CinemaBorder.copy(alpha = 0.4f))
        )),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shot_card_${shot.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Shot Number & Spec Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AmberGoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${shot.shotNumber}",
                            color = CinemaBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = shot.shotType,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Character and Location Token Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (shot.locationToken.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AmberGoldPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🏛️ ${shot.locationToken}",
                                color = AmberGoldPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnamorphicCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = shot.characterToken,
                            color = AnamorphicCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Specs Badges Row (Horizontal Scrollable for rich continuity tags)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CinemaSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🎥 ${shot.cameraMotion}",
                            fontSize = 10.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CinemaSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🔍 ${shot.lensMm}",
                            fontSize = 10.sp,
                            color = AmberGoldPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AmberGoldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⏱️ ${shot.durationSeconds}s (8s MAX)",
                            fontSize = 10.sp,
                            color = AmberGoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnamorphicCyan.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "📱 9:16",
                            fontSize = 10.sp,
                            color = AnamorphicCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ContinuityEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🛡️ DRIFT LOCKED",
                            fontSize = 10.sp,
                            color = ContinuityEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Continuity Health Score
                val (healthScore, _) = CineFlowPromptEngine.evaluateShotContinuityHealth(shot, character, location)
                val healthColor = when {
                    healthScore >= 90 -> ContinuityEmerald
                    healthScore >= 70 -> AmberGoldPrimary
                    else -> DirectorCrimson
                }
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(healthColor.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🎯 CONTINUITY: $healthScore%",
                            fontSize = 10.sp,
                            color = healthColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (shot.eyelineVector.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CinemaSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "👀 ${shot.eyelineVector}",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                if (shot.keyLightAngle.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CinemaSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "💡 ${shot.keyLightAngle}",
                                fontSize = 10.sp,
                                color = AmberGoldPrimary.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                // Camera Framing Intent Badge (Speaker vs Reaction vs OTS)
                if (shot.cameraFramingIntent.isNotBlank()) {
                    val intentIcon = when {
                        shot.cameraFramingIntent.contains("Reaction") -> "🎭"
                        shot.cameraFramingIntent.contains("Speaker") -> "🎙️"
                        shot.cameraFramingIntent.contains("OTS") || shot.cameraFramingIntent.contains("Over") -> "👥"
                        shot.cameraFramingIntent.contains("Two-Shot") -> "👫"
                        shot.cameraFramingIntent.contains("POV") -> "👁️"
                        else -> "🎬"
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnamorphicCyan.copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$intentIcon ${shot.cameraFramingIntent}",
                                fontSize = 10.sp,
                                color = AnamorphicCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                // Time of Day & Weather Lock Badge
                if (shot.timeOfDay.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CinemaSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "⏳ ${shot.timeOfDay} • ${shot.weatherAtmosphere}",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                // Extras Crowd Density Badge
                if (shot.extrasCrowdDensity.isNotBlank()) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CinemaSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🚶 ${shot.extrasCrowdDensity}",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Micro-Action
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "ATOMIC MICRO-ACTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGoldPrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = shot.actionDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }

            // Environment & Lighting
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "LIGHTING & ATMOSPHERE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnamorphicCyan,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${shot.lighting} • ${shot.environment}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }

            // Dialogue & Lip-Sync Phonemes
            if (shot.dialogueSpoken.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CinemaSurfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.RecordVoiceOver,
                            contentDescription = "Spoken Dialogue",
                            tint = AmberGoldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "\"${shot.dialogueSpoken}\"",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                    if (shot.dialogueDelivery.isNotBlank()) {
                        Text(
                            text = "🗣️ Lip Mechanics: ${shot.dialogueDelivery}",
                            fontSize = 10.sp,
                            color = AmberGoldPrimary.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Audio & Foley
            if (shot.audioFoley.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Foley",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = shot.audioFoley,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCopyImagePrompt,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnamorphicCyan.copy(alpha = 0.15f),
                        contentColor = AnamorphicCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Image Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onCopyVideoPrompt,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGoldPrimary.copy(alpha = 0.15f),
                        contentColor = AmberGoldPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Video Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DirectorCrimson, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyShotsPlaceholder(onAdd: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "No Shots in This Scene",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Assemble shot sequences specifying camera moves, optical lenses, and atomic character micro-actions.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = AmberGoldPrimary, contentColor = CinemaBackground),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add First Shot", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var sceneTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Scene", fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = sceneTitle,
                onValueChange = { sceneTitle = it },
                label = { Text("Scene Title", fontSize = 12.sp) },
                placeholder = { Text("e.g. Scene 2: Safehouse Infiltration") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberGoldPrimary,
                    unfocusedBorderColor = CinemaBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sceneTitle.isNotBlank()) onCreate(sceneTitle)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberGoldPrimary, contentColor = CinemaBackground)
            ) {
                Text("Create Scene", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = CinemaSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotEditorDialog(
    sceneTitle: String,
    initialShot: SceneShotEntity?,
    characters: List<CharacterEntity>,
    locations: List<LocationEntity> = emptyList(),
    nextShotNumber: Int,
    onDismiss: () -> Unit,
    onSave: (SceneShotEntity) -> Unit
) {
    var shotNumber by remember { mutableStateOf(initialShot?.shotNumber?.toString() ?: nextShotNumber.toString()) }
    var selectedShotType by remember { mutableStateOf(initialShot?.shotType ?: "Medium Close-Up") }
    var selectedCameraMotion by remember { mutableStateOf(initialShot?.cameraMotion ?: "Slow Dolly In") }
    var lensMm by remember { mutableStateOf(initialShot?.lensMm ?: "50mm Anamorphic") }
    var durationSeconds by remember { mutableStateOf(initialShot?.durationSeconds ?: 8) }
    var selectedCharacterToken by remember {
        mutableStateOf(initialShot?.characterToken ?: (characters.firstOrNull()?.codenameToken ?: "[PROTAGONIST]"))
    }
    var selectedLocationToken by remember {
        mutableStateOf(initialShot?.locationToken ?: (locations.firstOrNull()?.setToken ?: ""))
    }
    var actionDescription by remember {
        mutableStateOf(initialShot?.actionDescription ?: "Turns head slowly toward the doorway with a guarded expression.")
    }
    var dialogueSpoken by remember {
        mutableStateOf(initialShot?.dialogueSpoken ?: "")
    }
    var dialogueDelivery by remember {
        mutableStateOf(initialShot?.dialogueDelivery ?: "")
    }
    var selectedFramingIntent by remember {
        mutableStateOf(initialShot?.cameraFramingIntent ?: "Speaker Direct (A-Cam)")
    }
    var selectedTimeOfDay by remember {
        mutableStateOf(initialShot?.timeOfDay ?: (locations.find { it.setToken == selectedLocationToken }?.timeOfDay ?: "Night / Rain (02:00 AM)"))
    }
    var weatherAtmosphere by remember {
        mutableStateOf(initialShot?.weatherAtmosphere ?: "Heavy Rain & Wet Asphalt Specular")
    }
    var selectedExtrasDensity by remember {
        mutableStateOf(initialShot?.extrasCrowdDensity ?: "Zero Extras (Desolate)")
    }
    var environment by remember {
        mutableStateOf(initialShot?.environment ?: "Narrow alleyway with wet asphalt, glowing neon bokeh in background.")
    }
    var lighting by remember {
        mutableStateOf(initialShot?.lighting ?: "Warm tungsten key light with cyan rim light separating hair and collar.")
    }
    var audioFoley by remember {
        mutableStateOf(initialShot?.audioFoley ?: "Muffled rain, distant hover-car turbine.")
    }
    var eyelineVector by remember {
        mutableStateOf(initialShot?.eyelineVector ?: "Center-Right 15°")
    }
    var keyLightAngle by remember {
        mutableStateOf(initialShot?.keyLightAngle ?: "45° Camera Left Key")
    }

    val eyelineOptions = listOf(
        "Screen-Left 30°", "Center-Left 15°", "Direct Camera (Eye Contact)", "Center-Right 15°", "Screen-Right 30°", "Downcast -20°"
    )

    val keyLightOptions = listOf(
        "45° Camera Left Key", "45° Camera Right Key", "Direct Frontal Flat", "Backlit / Rim Silhouette", "Top-Down Overhead Chiaroscuro", "Under-chin Upward Glow"
    )

    val shotTypes = listOf(
        "Extreme Wide Shot", "Wide Shot", "Medium Shot", "Medium Close-Up",
        "Close-Up", "Extreme Close-Up", "Over-The-Shoulder", "Point of View"
    )

    val framingIntentOptions = listOf(
        "Speaker Direct (A-Cam)",
        "Reaction Shot (B-Cam)",
        "Over-The-Shoulder (OTS Favoring)",
        "Two-Shot (Both in Frame)",
        "Insert / Cutaway Shot",
        "Point-of-View (POV)"
    )

    val timeOfDayOptions = listOf(
        "Golden Hour / Magic Hour",
        "High Noon Hard Sun",
        "Overcast Gloom (Diffused)",
        "Twilight / Blue Hour",
        "Night / Rain (02:00 AM)",
        "Interior Neon Chiaroscuro"
    )

    val extrasDensityOptions = listOf(
        "Zero Extras (Desolate)",
        "Sparse Distant Silhouettes",
        "Busy Bokeh Crowd (Defocused)",
        "Heavy Foreground Pedestrians"
    )

    val cameraMotions = listOf(
        "Static Tripod", "Slow Dolly In", "Slow Dolly Out", "Lateral Pan Right",
        "Lateral Pan Left", "Tracking Steadicam", "Low Jib Up", "Dutch Angle Drift"
    )

    val lenses = listOf(
        "24mm Wide Anamorphic", "35mm Cinema", "50mm T/2.0", "65mm Anamorphic", "85mm Portrait", "100mm Macro"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CinemaSurface,
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(CinemaBorder, CinemaBorder.copy(0.4f)))),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (initialShot == null) "Add Shot to $sceneTitle" else "Edit Shot #${initialShot.shotNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Shot Number & Character
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = shotNumber,
                        onValueChange = { shotNumber = it },
                        label = { Text("Shot #", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(0.7f)
                    )

                    // Character Selector
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            text = "Character Anchor",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(characters) { char ->
                                val isSelected = char.codenameToken == selectedCharacterToken
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) AmberGoldPrimary else CinemaSurfaceVariant)
                                        .clickable { selectedCharacterToken = char.codenameToken }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = char.codenameToken,
                                        fontSize = 11.sp,
                                        color = if (isSelected) CinemaBackground else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Master Set / Location Anchor Selector
                if (locations.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "MASTER SET / LOCATION REFERENCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                val isNone = selectedLocationToken.isBlank()
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isNone) AmberGoldPrimary else CinemaSurfaceVariant)
                                        .clickable { selectedLocationToken = "" }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "None / Generic",
                                        fontSize = 11.sp,
                                        color = if (isNone) CinemaBackground else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            items(locations) { loc ->
                                val isSelected = loc.setToken == selectedLocationToken
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) AmberGoldPrimary else CinemaSurfaceVariant)
                                        .clickable {
                                            selectedLocationToken = loc.setToken
                                            // Auto-suggest environment and lighting from locked master set
                                            if (environment.isBlank() || environment.contains("Narrow alleyway")) {
                                                environment = "${loc.name}: ${loc.architecturalAnchor}"
                                            }
                                            if (lighting.isBlank() || lighting.contains("Warm tungsten")) {
                                                lighting = loc.lightingAtmosphereAnchor
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "🏛️ ${loc.setToken} (${loc.name})",
                                        fontSize = 11.sp,
                                        color = if (isSelected) CinemaBackground else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Shot Type Selector Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "SHOT FRAMING SCALE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGoldPrimary)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(shotTypes) { type ->
                            val isSelected = type == selectedShotType
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AmberGoldPrimary else CinemaSurfaceVariant)
                                    .clickable { selectedShotType = type }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Camera Framing Intent (Speaker vs Reaction vs OTS vs Two-Shot)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CAMERA FRAMING INTENT (SPEAKER VS REACTION / OTS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnamorphicCyan
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(framingIntentOptions) { intent ->
                            val isSelected = intent == selectedFramingIntent
                            val icon = when {
                                intent.contains("Reaction") -> "🎭"
                                intent.contains("Speaker") -> "🎙️"
                                intent.contains("OTS") || intent.contains("Over") -> "👥"
                                intent.contains("Two-Shot") -> "👫"
                                intent.contains("POV") -> "👁️"
                                else -> "🎬"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AnamorphicCyan else CinemaSurfaceVariant)
                                    .clickable { selectedFramingIntent = intent }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "$icon $intent",
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Time of Day Lock (Scene Continuity)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SCENE TIME OF DAY (LIGHTING CONTINUITY LOCK)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(timeOfDayOptions) { tod ->
                            val isSelected = tod == selectedTimeOfDay
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AmberGoldPrimary else CinemaSurfaceVariant)
                                    .clickable { selectedTimeOfDay = tod }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = tod,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Weather & Atmospheric Condition Lock
                OutlinedTextField(
                    value = weatherAtmosphere,
                    onValueChange = { weatherAtmosphere = it },
                    label = { Text("Weather & Atmosphere State (e.g. Heavy Rain, Wet Asphalt Reflections, Fog)", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Background Extras / Crowd Density
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "EXTRAS & BACKGROUND DENSITY (NO DRIFTING CROWDS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(extrasDensityOptions) { ext ->
                            val isSelected = ext == selectedExtrasDensity
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ContinuityEmerald else CinemaSurfaceVariant)
                                    .clickable { selectedExtrasDensity = ext }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = ext,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Camera Motion Selector Chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "CAMERA TRAJECTORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AnamorphicCyan)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(cameraMotions) { motion ->
                            val isSelected = motion == selectedCameraMotion
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AnamorphicCyan else CinemaSurfaceVariant)
                                    .clickable { selectedCameraMotion = motion }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = motion,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Lens mm Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "OPTICS & LENS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ContinuityEmerald)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(lenses) { lens ->
                            val isSelected = lens == lensMm
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ContinuityEmerald else CinemaSurfaceVariant)
                                    .clickable { lensMm = lens }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = lens,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Temporal Duration Selector (8s Max Constraint)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CLIP DURATION (8s MAX CEILING)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary
                        )
                        Text(
                            text = "${durationSeconds}s / 8s max",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary
                        )
                    }
                    val durations = listOf(2, 3, 4, 5, 6, 7, 8)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(durations) { dur ->
                            val isSelected = dur == durationSeconds
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AmberGoldPrimary else CinemaSurfaceVariant)
                                    .clickable { durationSeconds = dur }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (dur == 8) "8s (MAX)" else "${dur}s",
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Micro-Action
                OutlinedTextField(
                    value = actionDescription,
                    onValueChange = { actionDescription = it },
                    label = { Text("Atomic Micro-Action (Single movement)", fontSize = 12.sp) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Spoken Dialogue (Lip-Sync Sync)
                OutlinedTextField(
                    value = dialogueSpoken,
                    onValueChange = { dialogueSpoken = it },
                    label = { Text("Spoken Dialogue Line (Optional)", fontSize = 12.sp) },
                    placeholder = { Text("e.g. They found the coordinates.", fontSize = 12.sp, color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Dialogue Delivery & Lip Mechanics
                if (dialogueSpoken.isNotBlank()) {
                    OutlinedTextField(
                        value = dialogueDelivery,
                        onValueChange = { dialogueDelivery = it },
                        label = { Text("Lip Mechanics & Phoneme Delivery", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Whispered through clenched teeth, minimal jaw drop, sharp dental consonants", fontSize = 11.sp, color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Environment
                OutlinedTextField(
                    value = environment,
                    onValueChange = { environment = it },
                    label = { Text("Environment & Background Depth", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Lighting
                OutlinedTextField(
                    value = lighting,
                    onValueChange = { lighting = it },
                    label = { Text("Lighting & Color Grade", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Audio Foley
                OutlinedTextField(
                    value = audioFoley,
                    onValueChange = { audioFoley = it },
                    label = { Text("Audio Foley / Ambient Cues", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Eyeline Vector (Continuity Lock)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CONTINUOUS EYELINE VECTOR (180° RULE)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ContinuityEmerald
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(eyelineOptions) { opt ->
                            val isSelected = opt == eyelineVector
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ContinuityEmerald else CinemaSurfaceVariant)
                                    .clickable { eyelineVector = opt }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Key Light Angle (Continuity Lock)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "KEY LIGHT SOURCE ANGLE (SHADOW CONTINUITY)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(keyLightOptions) { opt ->
                            val isSelected = opt == keyLightAngle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AmberGoldPrimary else CinemaSurfaceVariant)
                                    .clickable { keyLightAngle = opt }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val num = shotNumber.toIntOrNull() ?: nextShotNumber
                            val entity = SceneShotEntity(
                                id = initialShot?.id ?: 0L,
                                sceneTitle = sceneTitle,
                                shotNumber = num,
                                shotType = selectedShotType,
                                cameraMotion = selectedCameraMotion,
                                lensMm = lensMm,
                                characterToken = selectedCharacterToken,
                                locationToken = selectedLocationToken,
                                actionDescription = actionDescription.trim(),
                                dialogueSpoken = dialogueSpoken.trim(),
                                dialogueDelivery = dialogueDelivery.trim(),
                                cameraFramingIntent = selectedFramingIntent,
                                timeOfDay = selectedTimeOfDay,
                                weatherAtmosphere = weatherAtmosphere.trim(),
                                extrasCrowdDensity = selectedExtrasDensity,
                                environment = environment.trim(),
                                lighting = lighting.trim(),
                                audioFoley = audioFoley.trim(),
                                eyelineVector = eyelineVector,
                                keyLightAngle = keyLightAngle,
                                durationSeconds = durationSeconds.coerceIn(1, 8)
                            )
                            onSave(entity)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGoldPrimary, contentColor = CinemaBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Shot", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AutoDirectStoryDialog(
    isDirecting: Boolean,
    onDismiss: () -> Unit,
    onDirect: (premise: String, genre: String, shotCount: Int) -> Unit
) {
    var premise by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("Cyberpunk Noir") }
    var selectedShotCount by remember { mutableStateOf(5) }

    val genres = listOf(
        "Cyberpunk Noir",
        "Gritty Crime Drama",
        "Suspense Thriller",
        "Emotional Drama",
        "High-Stakes Action"
    )

    val premiseSuggestions = listOf(
        "🌧️ Ulan & Standoff" to "Dalawang magkaibigan nagtapatan sa ulan sa isang makitid na alley tungkol sa ninakaw na data drive.",
        "🍜 Secret Rendezvous" to "Lihim na pagtatagpo sa isang neon noodle bar bago magsimula ang panggigipit ng sindikato.",
        "🚨 Rooftop Ambush" to "Pagtakas sa rooftop habang may humahabol na tactical drone sa gitna ng bagyo.",
        "💔 Betrayal & Standoff" to "Komprontasyon ng dalawang ahente sa safehouse nang matuklasan ang lihim na pagtataksil."
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CinemaSurface,
            border = BorderStroke(1.dp, AmberGoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✨", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "AI Auto-Direct Story",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "AI will direct camera framing, eyelines, and shots",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = CinemaBorder, thickness = 1.dp)

                // Story Premise Input
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "STORY PREMISE / LOGLINE (TAGALOG OR ENGLISH)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary,
                        letterSpacing = 0.5.sp
                    )
                    OutlinedTextField(
                        value = premise,
                        onValueChange = { premise = it },
                        placeholder = {
                            Text(
                                "Ilarawan ang takbo ng eksena... (hal. Dalawang magkaibigan nagkainitan sa ulan sa tabi ng neon billboard)",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Suggestion chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "QUICK IDEAS / SAMPLE PREMISES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ContinuityEmerald,
                        letterSpacing = 0.5.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(premiseSuggestions) { (title, fullText) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CinemaSurfaceVariant)
                                    .border(1.dp, CinemaBorder, RoundedCornerShape(6.dp))
                                    .clickable { premise = fullText }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Genre Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "CINEMATIC GENRE / ATMOSPHERE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnamorphicCyan,
                        letterSpacing = 0.5.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(genres) { genre ->
                            val isSelected = genre == selectedGenre
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) AnamorphicCyan else CinemaSurfaceVariant)
                                    .clickable { selectedGenre = genre }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = genre,
                                    fontSize = 11.sp,
                                    color = if (isSelected) CinemaBackground else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Shot Count / Pacing Arc
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SHOT PACING (8s MAX DURATION PER SHOT)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple(3, "3 Shots", "Fast Beat"),
                            Triple(5, "5 Shots", "Classic Arc"),
                            Triple(8, "8 Shots", "Full Scene")
                        ).forEach { (count, title, sub) ->
                            val isSelected = count == selectedShotCount
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AmberGoldPrimary else CinemaSurfaceVariant)
                                    .border(1.dp, if (isSelected) AmberGoldPrimary else CinemaBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedShotCount = count }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CinemaBackground else TextPrimary
                                    )
                                    Text(
                                        text = sub,
                                        fontSize = 10.sp,
                                        color = if (isSelected) CinemaBackground.copy(alpha = 0.8f) else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Director highlights badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CinemaBackground)
                        .border(1.dp, CinemaBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🎬 Auto-Director Rules Enforced:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ContinuityEmerald
                        )
                        Text(
                            text = "• Automatic Shot Framing (Wide -> Medium Two-Shot -> OTS -> Reaction CU)\n• 180° Eyeline Vector & Key Light Shadow Consistency\n• Temporal Pacing: 8s max duration for AI video models\n• Auto-Generates Scene Transition Bridge & Foley",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, enabled = !isDirecting) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onDirect(premise, selectedGenre, selectedShotCount)
                        },
                        enabled = !isDirecting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGoldPrimary,
                            contentColor = CinemaBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isDirecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = CinemaBackground,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Directing Scene...", fontWeight = FontWeight.Bold)
                        } else {
                            Text("🎬 Direct Scene", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
