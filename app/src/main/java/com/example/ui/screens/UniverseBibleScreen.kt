package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UniverseBibleEntity
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

@Composable
fun UniverseBibleScreen(
    bible: UniverseBibleEntity,
    onSaveBible: (UniverseBibleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember(bible) { mutableStateOf(bible.title) }
    var genre by remember(bible) { mutableStateOf(bible.genre) }
    var visualTone by remember(bible) { mutableStateOf(bible.visualTone) }
    var aspectRatio by remember(bible) { mutableStateOf(bible.aspectRatio) }
    var cameraLensSpec by remember(bible) { mutableStateOf(bible.cameraLensSpec) }
    var filmStockSimulation by remember(bible) { mutableStateOf(bible.filmStockSimulation) }
    var colorGrade by remember(bible) { mutableStateOf(bible.colorGrade) }
    var masterSeed by remember(bible) { mutableStateOf(bible.masterSeed.toString()) }
    var negativeConstraints by remember(bible) { mutableStateOf(bible.negativeConstraints) }

    val aspectRatios = listOf(
        "9:16 Vertical Series (Mobile Cinema)",
        "2.39:1 Anamorphic Cinema",
        "16:9 4K Cinema",
        "1.85:1 Widescreen",
        "4:3 Vintage Academy"
    )

    Scaffold(
        containerColor = CinemaBackground,
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
            // Header Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CinemaSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
                        listOf(AmberGoldPrimary.copy(alpha = 0.4f), CinemaBorder)
                    )),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(AmberGoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = AmberGoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Universe & Global Master Rule Bible",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberGoldPrimary
                            )
                        }

                        Text(
                            text = "Global rules injected into every prompt. Establishes the uniform aspect ratio, optical camera physics, film grain, and negative prompts for the entire series.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Series Identity
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "SERIES IDENTITY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Project / Series Title", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberGoldPrimary,
                                unfocusedBorderColor = CinemaBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = genre,
                            onValueChange = { genre = it },
                            label = { Text("Genre", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberGoldPrimary,
                                unfocusedBorderColor = CinemaBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Optics & Aspect Ratio
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "OPTICAL LAWS & SENSOR CONFIGURATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AnamorphicCyan,
                            letterSpacing = 0.5.sp
                        )

                        // Aspect Ratio Chips
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Aspect Ratio", fontSize = 11.sp, color = TextSecondary)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(aspectRatios) { ratio ->
                                    val isSelected = ratio == aspectRatio
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) AnamorphicCyan else CinemaSurfaceVariant)
                                            .clickable { aspectRatio = ratio }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = ratio,
                                            fontSize = 11.sp,
                                            color = if (isSelected) CinemaBackground else TextPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        if (aspectRatio.contains("9:16")) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AnamorphicCyan.copy(alpha = 0.12f))
                                    .border(1.dp, AnamorphicCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("📱", fontSize = 16.sp)
                                    Column {
                                        Text(
                                            text = "9:16 Vertical Series Constraints Enforced",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AnamorphicCyan
                                        )
                                        Text(
                                            text = "8s max duration per shot • Exactly 9 scenes per episode • Mobile portrait safe-zone framing",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = cameraLensSpec,
                            onValueChange = { cameraLensSpec = it },
                            label = { Text("Camera Rig & Lens System", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnamorphicCyan,
                                unfocusedBorderColor = CinemaBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = filmStockSimulation,
                            onValueChange = { filmStockSimulation = it },
                            label = { Text("Film Stock & Cadence", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnamorphicCyan,
                                unfocusedBorderColor = CinemaBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Atmosphere & Color Grade
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ATMOSPHERE & GRADE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ContinuityEmerald,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = visualTone,
                            onValueChange = { visualTone = it },
                            label = { Text("Visual Tone", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ContinuityEmerald,
                                unfocusedBorderColor = CinemaBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = colorGrade,
                            onValueChange = { colorGrade = it },
                            label = { Text("Color Grade Palette", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ContinuityEmerald,
                                unfocusedBorderColor = CinemaBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Master Seed (Zero-Drift Constant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = masterSeed,
                                onValueChange = { masterSeed = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Universe Master Seed (Anti-Drift Lock)", fontSize = 12.sp) },
                                placeholder = { Text("e.g. 849201") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberGoldPrimary,
                                    unfocusedBorderColor = CinemaBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    masterSeed = (100000L..999999L).random().toString()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberGoldPrimary.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = "Randomize Seed",
                                    tint = AmberGoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Negative Constraints
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "NEGATIVE EXCLUSIONS (ANTI-AI ARTIFACTS)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DirectorCrimson,
                            letterSpacing = 0.5.sp
                        )

                        OutlinedTextField(
                            value = negativeConstraints,
                            onValueChange = { negativeConstraints = it },
                            label = { Text("Negative Prompt Exclusions", fontSize = 12.sp) },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DirectorCrimson,
                                unfocusedBorderColor = CinemaBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = {
                        val updated = bible.copy(
                            title = title.trim(),
                            genre = genre.trim(),
                            visualTone = visualTone.trim(),
                            aspectRatio = aspectRatio.trim(),
                            cameraLensSpec = cameraLensSpec.trim(),
                            filmStockSimulation = filmStockSimulation.trim(),
                            colorGrade = colorGrade.trim(),
                            masterSeed = masterSeed.toLongOrNull() ?: bible.masterSeed,
                            negativeConstraints = negativeConstraints.trim()
                        )
                        onSaveBible(updated)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGoldPrimary,
                        contentColor = CinemaBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_universe_bible")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Master Universe Rules", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
