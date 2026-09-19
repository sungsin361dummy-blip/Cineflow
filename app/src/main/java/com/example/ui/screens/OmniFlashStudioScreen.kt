package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AnamorphicCyan
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaBorder
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ContinuityEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.PromptOutputMode

@Composable
fun OmniFlashStudioScreen(
    selectedMode: PromptOutputMode,
    generatedOutputText: String,
    selectedScene: String,
    onSelectMode: (PromptOutputMode) -> Unit,
    onCopyToClipboard: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // Hero Status Header
            item {
                StudioHeroCard()
            }

            // Mode Selector Tabs
            item {
                ModeSelectorChips(
                    selectedMode = selectedMode,
                    onSelectMode = onSelectMode
                )
            }

            // Copy Action Bar
            item {
                val label = when (selectedMode) {
                    PromptOutputMode.OMNI_FLASH_SYSTEM -> "Omni Flash System Instructions"
                    PromptOutputMode.FLOW_JSON -> "Google Flow Scene JSON"
                    PromptOutputMode.ANTI_DRIFT_MANIFEST -> "Zero-Drift Continuity Protocol"
                    PromptOutputMode.IMAGE_PROMPT -> "Imagen 3 / Midjourney Prompt"
                    PromptOutputMode.VIDEO_MOTION -> "Video Motion Prompt"
                }

                Button(
                    onClick = { onCopyToClipboard(label, generatedOutputText) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGoldPrimary,
                        contentColor = CinemaBackground
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_copy_prompt_output")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Copy $label",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // Formatted Monospace Code Display
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ContinuityEmerald)
                                )
                                Text(
                                    text = "PROMPT GENERATOR OUTPUT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Text(
                                text = "${generatedOutputText.length} chars • ~${generatedOutputText.length / 4} tokens",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Code Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CinemaBackground)
                                .border(1.dp, CinemaBorder, RoundedCornerShape(8.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = generatedOutputText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Google Flow Best Practice Guide
            item {
                GoogleFlowQuickGuide()
            }
        }
    }
}

@Composable
fun StudioHeroCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
            listOf(AnamorphicCyan.copy(alpha = 0.4f), AmberGoldPrimary.copy(alpha = 0.3f))
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
                        .background(AnamorphicCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AnamorphicCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "Google Omni Flash & Flow Engine",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AnamorphicCyan
                )
            }

            Text(
                text = "Engineered for Gemini 2.0 / 1.5 Omni Flash's million-token context and native JSON schema output. Continuity anchors and optical laws are locked into every prompt.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun ModeSelectorChips(
    selectedMode: PromptOutputMode,
    onSelectMode: (PromptOutputMode) -> Unit
) {
    val modes = listOf(
        Triple(PromptOutputMode.OMNI_FLASH_SYSTEM, "Omni Flash System", Icons.Default.Code),
        Triple(PromptOutputMode.ANTI_DRIFT_MANIFEST, "Zero-Drift Protocol", Icons.Default.Shield),
        Triple(PromptOutputMode.FLOW_JSON, "Flow Scene JSON", Icons.Default.DataObject),
        Triple(PromptOutputMode.IMAGE_PROMPT, "Imagen / Midjourney", Icons.Default.PhotoCamera),
        Triple(PromptOutputMode.VIDEO_MOTION, "Video Motion (Kling)", Icons.Default.Movie)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "OUTPUT FORMAT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.5.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            modes.forEach { (mode, title, icon) ->
                val isSelected = mode == selectedMode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AmberGoldPrimary else CinemaSurface)
                        .border(
                            1.dp,
                            if (isSelected) AmberGoldPrimary else CinemaBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectMode(mode) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) CinemaBackground else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = title,
                            color = if (isSelected) CinemaBackground else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoogleFlowQuickGuide() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AmberGoldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Deployment into Google AI Studio & Flow",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AmberGoldPrimary
                )
            }

            GuideStep(
                step = "1",
                text = "Copy 'Omni Flash System' and paste into System Instructions in Google AI Studio."
            )
            GuideStep(
                step = "2",
                text = "Enable Structured Output (JSON Schema) to enforce deterministic shot arrays."
            )
            GuideStep(
                step = "3",
                text = "Use 'Flow Scene JSON' to trigger downstream Google Cloud Functions or video generation pipelines."
            )
            GuideStep(
                step = "4",
                text = "Mobile Micro-Series: Output strictly formats 9:16 framing, 8s max duration per clip, and a complete 9-scene episodic arc."
            )
            GuideStep(
                step = "5",
                text = "Zero-Drift Continuity: Copy 'Zero-Drift Protocol' to enforce facial geometry, wardrobe locks, and set physics across generation models."
            )
            GuideStep(
                step = "6",
                text = "Universe Master Seed: In Universe Bible, lock or randomize your fixed seed (--seed) to synchronize character rendering across cuts."
            )
            GuideStep(
                step = "7",
                text = "Eyeline & Key Light Continuity: Use the Eyeline Vector and Key Light Angle in the Shot Editor to preserve 180° cinematic rules and shadow alignment."
            )
        }
    }
}

@Composable
fun GuideStep(step: String, text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(AmberGoldPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = AmberGoldPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            lineHeight = 16.sp
        )
    }
}
