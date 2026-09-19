package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.CharacterEntity
import com.example.data.local.UniverseBibleEntity
import com.example.domain.CineFlowPromptEngine
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.AnamorphicCyan
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaBorder
import com.example.ui.theme.CinemaDivider
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceVariant
import com.example.ui.theme.ContinuityEmerald
import com.example.ui.theme.DirectorCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterVaultScreen(
    characters: List<CharacterEntity>,
    bible: UniverseBibleEntity = UniverseBibleEntity(),
    onSaveCharacter: (CharacterEntity, List<Uri>) -> Unit,
    onDeleteCharacter: (Long) -> Unit,
    onCopyToClipboard: (String, String) -> Unit,
    isEditing: Boolean,
    characterUnderEdit: CharacterEntity?,
    onOpenEditor: (CharacterEntity?) -> Unit,
    onCloseEditor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = CinemaBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenEditor(null) },
                containerColor = AmberGoldPrimary,
                contentColor = CinemaBackground,
                modifier = Modifier.testTag("fab_add_character")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Character")
                    Text("Add Character", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
            // Authority Banner
            item {
                AuthorityHeroBanner()
            }

            // Characters count & header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Character Continuity Vault",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${characters.size} locked character profiles for Omni Flash",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (characters.isEmpty()) {
                item {
                    EmptyVaultPlaceholder(onAdd = { onOpenEditor(null) })
                }
            } else {
                items(characters, key = { it.id }) { character ->
                    CharacterCard(
                        character = character,
                        onCopyToken = {
                            val card = CineFlowPromptEngine.buildCharacterCard(character)
                            onCopyToClipboard("Character Continuity Anchor ${character.codenameToken}", card)
                        },
                        onCopyModelSheet = {
                            val sheetPrompt = CineFlowPromptEngine.buildCharacterModelSheetPrompt(bible, character)
                            onCopyToClipboard("4-View Model Sheet Prompt: ${character.name}", sheetPrompt)
                        },
                        onEdit = { onOpenEditor(character) },
                        onDelete = { onDeleteCharacter(character.id) }
                    )
                }
            }
        }

        // Edit / Add Character Dialog
        if (isEditing) {
            CharacterEditorDialog(
                initialCharacter = characterUnderEdit,
                onDismiss = onCloseEditor,
                onSave = onSaveCharacter
            )
        }
    }
}

@Composable
fun AuthorityHeroBanner() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
            listOf(AmberGoldPrimary.copy(alpha = 0.5f), AnamorphicCyan.copy(alpha = 0.3f))
        )),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(ContinuityEmerald.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Authority",
                        tint = ContinuityEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "Character Reference Authority Active",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ContinuityEmerald
                )
            }

            Text(
                text = "Upload front, 3/4 profile, and wardrobe reference photos. Continuity anchors lock facial bone structure, costume fabrics, and signature acting demeanor across all generated shots.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CinemaSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ Zero Token Drift",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CinemaSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ Body Somatotype & Height Lock",
                        color = AnamorphicCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CinemaSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ Craniofacial Bones Lock",
                        color = AmberGoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: CharacterEntity,
    onCopyToken: () -> Unit,
    onCopyModelSheet: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val imagePaths = remember(character.referenceImageUris) {
        character.referenceImageUris.split(",").filter { it.isNotBlank() }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
            listOf(CinemaBorder, CinemaBorder.copy(alpha = 0.5f))
        )),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("character_card_${character.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AmberGoldPrimary.copy(alpha = 0.15f))
                            .border(1.dp, AmberGoldPrimary.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = character.name.take(1).uppercase(),
                            color = AmberGoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = character.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AmberGoldPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = character.codenameToken,
                                    color = AmberGoldPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = character.role,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                // Authority verification badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(ContinuityEmerald.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = ContinuityEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "VERIFIED",
                            color = ContinuityEmerald,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Reference Photos Row
            if (imagePaths.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "CONTINUITY REFERENCE PHOTOS (${imagePaths.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(imagePaths) { path ->
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, CinemaBorder, RoundedCornerShape(8.dp))
                                    .background(CinemaSurfaceVariant)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(path)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Reference Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Body Somatotype & Build Anchor
            if (character.bodySomatotypeAnchor.isNotBlank()) {
                AnchorDetailBlock(
                    title = "BODY SOMATOTYPE & STATURE",
                    content = character.bodySomatotypeAnchor,
                    accentColor = AnamorphicCyan
                )
            }

            // Craniofacial Bones & Facial Geometry
            if (character.craniofacialAnchor.isNotBlank()) {
                AnchorDetailBlock(
                    title = "CRANIOFACIAL BONES & GEOMETRY",
                    content = character.craniofacialAnchor,
                    accentColor = AmberGoldPrimary
                )
            }

            // Physical Anchor
            AnchorDetailBlock(
                title = "PHYSICAL & HAIR ANCHOR",
                content = character.physicalAnchor,
                accentColor = AnamorphicCyan
            )

            // Wardrobe Anchor
            AnchorDetailBlock(
                title = "WARDROBE & COSTUME ANCHOR",
                content = character.wardrobeAnchor,
                accentColor = AmberGoldPrimary
            )

            // Signature Aura
            AnchorDetailBlock(
                title = "SIGNATURE AURA & DEMEANOR",
                content = character.signatureAura,
                accentColor = ContinuityEmerald
            )

            // Audio / Voice Profile
            if (character.voiceAudioProfile.isNotBlank()) {
                AnchorDetailBlock(
                    title = "AUDIO / VOCAL PROFILE",
                    content = character.voiceAudioProfile,
                    accentColor = TextSecondary
                )
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
                    onClick = onCopyToken,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGoldPrimary.copy(alpha = 0.15f),
                        contentColor = AmberGoldPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Anchor",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Anchor", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onCopyModelSheet,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnamorphicCyan.copy(alpha = 0.15f),
                        contentColor = AnamorphicCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Model Sheet",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("4-View Sheet", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(CinemaBorder, CinemaBorder)))
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                }

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DirectorCrimson),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(CinemaBorder, CinemaBorder)))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnchorDetailBlock(
    title: String,
    content: String,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 0.5.sp
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary.copy(alpha = 0.9f),
            lineHeight = 16.sp
        )
    }
}

@Composable
fun EmptyVaultPlaceholder(onAdd: () -> Unit) {
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
                imageVector = Icons.Default.Face,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "No Characters Locked Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Upload character reference photos and define immutable physical & wardrobe anchors to prevent facial and costume drift across video clips.",
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
                Text("Add Character", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditorDialog(
    initialCharacter: CharacterEntity?,
    onDismiss: () -> Unit,
    onSave: (CharacterEntity, List<Uri>) -> Unit
) {
    var name by remember { mutableStateOf(initialCharacter?.name ?: "") }
    var codenameToken by remember { mutableStateOf(initialCharacter?.codenameToken ?: "[HERO]") }
    var role by remember { mutableStateOf(initialCharacter?.role ?: "Lead Character / Protagonist") }
    var bodySomatotypeAnchor by remember {
        mutableStateOf(initialCharacter?.bodySomatotypeAnchor ?: "168cm tall, lean athletic mesomorph build, narrow waist, broad athletic clavicles, balanced upright posture.")
    }
    var craniofacialAnchor by remember {
        mutableStateOf(initialCharacter?.craniofacialAnchor ?: "High angular zygomatic cheekbones, straight nasal bridge with defined tip, sharp angular jawline, almond-shaped hooded eyes, neutral pupillary distance.")
    }
    var physicalAnchor by remember {
        mutableStateOf(initialCharacter?.physicalAnchor ?: "28-year-old female, obsidian black blunt bob haircut with asymmetrical bangs, hazel eyes, faint scar across left nose bridge.")
    }
    var wardrobeAnchor by remember {
        mutableStateOf(initialCharacter?.wardrobeAnchor ?: "Matte black tactical trench coat with high collar, dark turtleneck, combat trousers.")
    }
    var signatureAura by remember {
        mutableStateOf(initialCharacter?.signatureAura ?: "Intense piercing gaze, stoic composure, minimal blinks.")
    }
    var voiceProfile by remember {
        mutableStateOf(initialCharacter?.voiceAudioProfile ?: "ElevenLabs: 'Rachel - Crisp Low Pacing'")
    }
    var hasAuthority by remember { mutableStateOf(initialCharacter?.hasReferenceAuthority ?: true) }

    val newSelectedPhotos = remember { mutableStateListOf<Uri>() }
    val existingPhotos = remember {
        mutableStateListOf<String>().apply {
            if (initialCharacter != null && initialCharacter.referenceImageUris.isNotBlank()) {
                addAll(initialCharacter.referenceImageUris.split(",").filter { it.isNotBlank() })
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            newSelectedPhotos.addAll(uris)
        }
    }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialCharacter == null) "New Character Reference" else "Edit Character Anchors",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ContinuityEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AUTHORITY MODE",
                            color = ContinuityEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Reference Photos Upload Block
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REFERENCE PHOTOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGoldPrimary
                        )
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberGoldPrimary.copy(0.2f),
                                contentColor = AmberGoldPrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload Photos", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Display existing & new photos
                    if (existingPhotos.isEmpty() && newSelectedPhotos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, CinemaBorder, RoundedCornerShape(8.dp))
                                .background(CinemaSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No reference photos attached yet. Tap 'Upload Photos' to select from gallery.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(existingPhotos) { path ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, ContinuityEmerald, RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(path)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { existingPhotos.remove(path) },
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                            .background(CinemaBackground.copy(alpha = 0.7f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DirectorCrimson, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                            items(newSelectedPhotos) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, AmberGoldPrimary, RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(uri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { newSelectedPhotos.remove(uri) },
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                            .background(CinemaBackground.copy(alpha = 0.7f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DirectorCrimson, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Reference Authority Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CinemaSurfaceVariant)
                        .clickable { hasAuthority = !hasAuthority }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = hasAuthority,
                        onCheckedChange = { hasAuthority = it },
                        colors = CheckboxDefaults.colors(checkedColor = ContinuityEmerald)
                    )
                    Column {
                        Text(
                            text = "Reference Authority Granted",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "I hold authority to lock and inject this character's likeness into the AI model.",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Name & Codename Token
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Character Name", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = codenameToken,
                        onValueChange = { codenameToken = it },
                        label = { Text("Token", fontSize = 12.sp) },
                        placeholder = { Text("[NAME]") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = AmberGoldPrimary,
                            unfocusedTextColor = AmberGoldPrimary
                        ),
                        modifier = Modifier.weight(0.8f)
                    )
                }

                // Role
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role / Archetype", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Body Somatotype & Build Anchor
                OutlinedTextField(
                    value = bodySomatotypeAnchor,
                    onValueChange = { bodySomatotypeAnchor = it },
                    label = { Text("Body Somatotype & Stature Anchor", fontSize = 12.sp) },
                    placeholder = { Text("e.g. 168cm tall, lean athletic mesomorph build, narrow waist, broad athletic clavicles, balanced upright posture.") },
                    supportingText = { Text("Exact height, somatotype (mesomorph/ectomorph), shoulder-to-hip ratio, posture frame", fontSize = 10.sp, color = AnamorphicCyan) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnamorphicCyan,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Craniofacial Architecture Anchor
                OutlinedTextField(
                    value = craniofacialAnchor,
                    onValueChange = { craniofacialAnchor = it },
                    label = { Text("Craniofacial Bones & Facial Geometry", fontSize = 12.sp) },
                    placeholder = { Text("e.g. High angular zygomatic cheekbones, straight nasal bridge with sharp tip, defined sharp jawline, hooded eyes, neutral pupillary distance.") },
                    supportingText = { Text("Zygomatic cheekbones, nasal bridge ridge, mandibular jaw angle, pupillary distance, facial symmetry", fontSize = 10.sp, color = AmberGoldPrimary) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Physical Anchor
                OutlinedTextField(
                    value = physicalAnchor,
                    onValueChange = { physicalAnchor = it },
                    label = { Text("Physical & Hair Continuity Anchor (Zero-Drift)", fontSize = 12.sp) },
                    placeholder = { Text("e.g. 28yo female, hazel eyes, permanent scar strictly on LEFT cheekbone, invariant obsidian bob with blunt bangs, natural skin pores.") },
                    supportingText = { Text("Hair silhouette & parting, asymmetric scars/features (specify Left/Right), skin texture", fontSize = 10.sp, color = AnamorphicCyan) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnamorphicCyan,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Wardrobe Anchor
                OutlinedTextField(
                    value = wardrobeAnchor,
                    onValueChange = { wardrobeAnchor = it },
                    label = { Text("Wardrobe & Costume Anchor (Fabric & Color Lock)", fontSize = 12.sp) },
                    placeholder = { Text("e.g. Heavyweight matte black canvas trench coat (#121212), high mandarin collar, oxidized brass zipper, dark turtleneck, combat trousers.") },
                    supportingText = { Text("Exact fabrics (leather/wool/denim), collar architecture, fastener hardware, base pigment dye (never changes with light)", fontSize = 10.sp, color = AmberGoldPrimary) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Signature Aura
                OutlinedTextField(
                    value = signatureAura,
                    onValueChange = { signatureAura = it },
                    label = { Text("Signature Aura & Demeanor", fontSize = 12.sp) },
                    supportingText = { Text("Posture, gaze direction, tension, micro-expressions", fontSize = 10.sp, color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ContinuityEmerald,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Vocal Profile
                OutlinedTextField(
                    value = voiceProfile,
                    onValueChange = { voiceProfile = it },
                    label = { Text("Vocal Profile / ElevenLabs Voice ID", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Dialog Action Buttons
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
                            if (name.isNotBlank()) {
                                val token = if (codenameToken.startsWith("[")) codenameToken else "[$codenameToken]"
                                val entity = CharacterEntity(
                                    id = initialCharacter?.id ?: 0L,
                                    name = name.trim(),
                                    codenameToken = token.trim(),
                                    role = role.trim(),
                                    referenceImageUris = existingPhotos.joinToString(","),
                                    hasReferenceAuthority = hasAuthority,
                                    physicalAnchor = physicalAnchor.trim(),
                                    bodySomatotypeAnchor = bodySomatotypeAnchor.trim(),
                                    craniofacialAnchor = craniofacialAnchor.trim(),
                                    wardrobeAnchor = wardrobeAnchor.trim(),
                                    signatureAura = signatureAura.trim(),
                                    voiceAudioProfile = voiceProfile.trim()
                                )
                                onSave(entity, newSelectedPhotos.toList())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGoldPrimary, contentColor = CinemaBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Anchors", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
