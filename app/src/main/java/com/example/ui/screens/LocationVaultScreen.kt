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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationCity
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
import com.example.data.local.LocationEntity
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
fun LocationVaultScreen(
    locations: List<LocationEntity>,
    onSaveLocation: (LocationEntity, List<Uri>) -> Unit,
    onDeleteLocation: (Long) -> Unit,
    onCopyToClipboard: (String, String) -> Unit,
    isEditing: Boolean,
    locationUnderEdit: LocationEntity?,
    onOpenEditor: (LocationEntity?) -> Unit,
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
                modifier = Modifier.testTag("fab_add_location")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Master Set")
                    Text("Add Master Set", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
            // Master Set Authority Banner
            item {
                LocationAuthorityBanner()
            }

            // Location Count Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Master Set & Location Vault",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${locations.size} locked master environments for scene continuity",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (locations.isEmpty()) {
                item {
                    EmptyLocationPlaceholder(onAdd = { onOpenEditor(null) })
                }
            } else {
                items(locations, key = { it.id }) { loc ->
                    LocationCard(
                        location = loc,
                        onCopyToken = {
                            val card = CineFlowPromptEngine.buildLocationCard(loc)
                            onCopyToClipboard("Master Set Reference ${loc.setToken}", card)
                        },
                        onEdit = { onOpenEditor(loc) },
                        onDelete = { onDeleteLocation(loc.id) }
                    )
                }
            }
        }

        // Edit / Add Location Dialog
        if (isEditing) {
            LocationEditorDialog(
                initialLocation = locationUnderEdit,
                onDismiss = onCloseEditor,
                onSave = onSaveLocation
            )
        }
    }
}

@Composable
fun LocationAuthorityBanner() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(ContinuityEmerald.copy(alpha = 0.5f), AnamorphicCyan.copy(alpha = 0.3f))
            )
        ),
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
                    text = "Master Set Authority Active",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ContinuityEmerald
                )
            }

            Text(
                text = "Ikaw ang may buong authority sa visual environment. Mag-upload ng master reference photos (wide angles, architectural textures, lighting plates) para manatiling 100% pareho ang location sa buong palabas nang walang background drift!",
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
                    Text("📷 Reference Image Upload", fontSize = 10.sp, color = AmberGoldPrimary, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CinemaSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("🏛️ Spatial Geometry Locked", fontSize = 10.sp, color = AnamorphicCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LocationCard(
    location: LocationEntity,
    onCopyToken: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val photoList = remember(location.referenceImageUris) {
        location.referenceImageUris.split(",").filter { it.isNotBlank() }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
            listOf(CinemaBorder, CinemaBorder.copy(alpha = 0.3f))
        )),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Name, Set Token, Authority Status
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AnamorphicCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationCity,
                            contentDescription = null,
                            tint = AnamorphicCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = location.setToken,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGoldPrimary
                            )
                            Text("•", color = TextMuted, fontSize = 12.sp)
                            Text(
                                text = location.environmentType,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopyToken) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Set Bible Token",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Location",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Location",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Reference Photos Preview
            if (photoList.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "UPLOADED SET REFERENCES (${photoList.size} PHOTOS)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGoldPrimary,
                        letterSpacing = 0.5.sp
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(photoList) { photoPath ->
                            Box(
                                modifier = Modifier
                                    .size(width = 110.dp, height = 75.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, CinemaBorder, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(photoPath)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Set Reference Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .background(CinemaBackground.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("REF", fontSize = 8.sp, color = ContinuityEmerald, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CinemaSurfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("⚠️", fontSize = 12.sp)
                        Text(
                            text = "No reference image uploaded yet. Tap edit to upload set photos for 100% visual lock.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CinemaDivider)
            )

            // Continuity Anchors (Architecture, Lighting, Time)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AnchorRow(
                    label = "ARCHITECTURAL ANCHOR",
                    text = location.architecturalAnchor,
                    accentColor = AnamorphicCyan
                )
                AnchorRow(
                    label = "LIGHTING & MOOD",
                    text = location.lightingAtmosphereAnchor,
                    accentColor = AmberGoldPrimary
                )
                if (location.propsAndFoleyAnchor.isNotBlank()) {
                    AnchorRow(
                        label = "PROPS & AMBIENCE",
                        text = location.propsAndFoleyAnchor,
                        accentColor = TextSecondary
                    )
                }
            }

            // Time of Day Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CinemaSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⏱️ ${location.timeOfDay}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (location.hasReferenceAuthority) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ContinuityEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "MASTER REFERENCE ANCHOR LOCKED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ContinuityEmerald
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Master Set Reference", color = TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete '${location.name}'? Shots referencing this location will lose their master set anchor.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DirectorCrimson)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CinemaSurface
        )
    }
}

@Composable
fun AnchorRow(
    label: String,
    text: String,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            letterSpacing = 0.5.sp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary.copy(alpha = 0.9f),
            lineHeight = 16.sp
        )
    }
}

@Composable
fun EmptyLocationPlaceholder(onAdd: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(
            listOf(CinemaBorder, CinemaBorder.copy(alpha = 0.3f))
        )),
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
                imageVector = Icons.Default.Landscape,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "No Master Sets Defined Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Ikaw ang magpapasya sa locations ng palabas. Mag-upload ng photos at itakda ang geometry para hindi magbago ang kwarto o lugar sa buong series.",
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
                Text("Add Master Set", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationEditorDialog(
    initialLocation: LocationEntity?,
    onDismiss: () -> Unit,
    onSave: (LocationEntity, List<Uri>) -> Unit
) {
    var name by remember { mutableStateOf(initialLocation?.name ?: "") }
    var setToken by remember { mutableStateOf(initialLocation?.setToken ?: "[SET_MAIN]") }
    var environmentType by remember { mutableStateOf(initialLocation?.environmentType ?: "Urban Exterior / Cyberpunk") }
    var architecturalAnchor by remember {
        mutableStateOf(
            initialLocation?.architecturalAnchor
                ?: "Narrow wet asphalt alleyway flanked by exposed conduit pipes, rust-eaten iron fire escapes, and overhead tangled cables."
        )
    }
    var lightingAtmosphereAnchor by remember {
        mutableStateOf(
            initialLocation?.lightingAtmosphereAnchor
                ?: "Low-key chiaroscuro, flickering amber sodium vapor light, cyan neon reflections in rain puddles."
        )
    }
    var propsAndFoleyAnchor by remember {
        mutableStateOf(
            initialLocation?.propsAndFoleyAnchor
                ?: "Dripping rusted drainage pipes, distant hover-car turbine thrum."
        )
    }
    var timeOfDay by remember { mutableStateOf(initialLocation?.timeOfDay ?: "Midnight / Torrential Rain") }
    var hasAuthority by remember { mutableStateOf(initialLocation?.hasReferenceAuthority ?: true) }

    val newSelectedPhotos = remember { mutableStateListOf<Uri>() }
    val existingPhotos = remember {
        mutableStateListOf<String>().apply {
            if (initialLocation != null && initialLocation.referenceImageUris.isNotBlank()) {
                addAll(initialLocation.referenceImageUris.split(",").filter { it.isNotBlank() })
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
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(CinemaBorder, CinemaBorder.copy(0.4f)))
            ),
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
                        text = if (initialLocation == null) "New Master Set Reference" else "Edit Set Reference",
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
                            text = "SET AUTHORITY",
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
                            text = "LOCATION REFERENCE PHOTOS",
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

                    if (existingPhotos.isEmpty() && newSelectedPhotos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CinemaSurfaceVariant)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                                Text("Mag-upload ng reference photos ng set", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(existingPhotos) { photoPath ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, AmberGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(photoPath)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { existingPhotos.remove(photoPath) },
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                            .background(CinemaBackground.copy(alpha = 0.8f), CircleShape)
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
                                        .border(1.dp, ContinuityEmerald, RoundedCornerShape(8.dp))
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
                                            .background(CinemaBackground.copy(alpha = 0.8f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DirectorCrimson, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Set Name & Token
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Set Name", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Abandoned LRT Station") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = setToken,
                        onValueChange = { setToken = it },
                        label = { Text("Set Token", fontSize = 12.sp) },
                        placeholder = { Text("[SET_NAME]") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(0.8f)
                    )
                }

                // Environment Type & Time of Day
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = environmentType,
                        onValueChange = { environmentType = it },
                        label = { Text("Type / Setting", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = timeOfDay,
                        onValueChange = { timeOfDay = it },
                        label = { Text("Time / Weather", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGoldPrimary,
                            unfocusedBorderColor = CinemaBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Architectural Anchor
                OutlinedTextField(
                    value = architecturalAnchor,
                    onValueChange = { architecturalAnchor = it },
                    label = { Text("Architectural & Spatial Anchor (Permanent Geometry)", fontSize = 12.sp) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Lighting & Atmosphere Anchor
                OutlinedTextField(
                    value = lightingAtmosphereAnchor,
                    onValueChange = { lightingAtmosphereAnchor = it },
                    label = { Text("Lighting & Atmosphere Anchor (Sources, Temperature)", fontSize = 12.sp) },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Props & Foley
                OutlinedTextField(
                    value = propsAndFoleyAnchor,
                    onValueChange = { propsAndFoleyAnchor = it },
                    label = { Text("Props & Ambient Foley Audio", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGoldPrimary,
                        unfocusedBorderColor = CinemaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Master Authority Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { hasAuthority = !hasAuthority }
                ) {
                    Checkbox(
                        checked = hasAuthority,
                        onCheckedChange = { hasAuthority = it },
                        colors = CheckboxDefaults.colors(checkedColor = ContinuityEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lock as Master Reference Anchor for all Shots in this Set",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }

                // Action Buttons
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
                            val entity = (initialLocation ?: LocationEntity(
                                name = name.trim(),
                                setToken = if (setToken.startsWith("[")) setToken.trim() else "[$setToken]".trim(),
                                environmentType = environmentType.trim(),
                                referenceImageUris = existingPhotos.joinToString(","),
                                hasReferenceAuthority = hasAuthority,
                                architecturalAnchor = architecturalAnchor.trim(),
                                lightingAtmosphereAnchor = lightingAtmosphereAnchor.trim(),
                                propsAndFoleyAnchor = propsAndFoleyAnchor.trim(),
                                timeOfDay = timeOfDay.trim()
                            )).copy(
                                name = name.trim(),
                                setToken = if (setToken.startsWith("[")) setToken.trim() else "[$setToken]".trim(),
                                environmentType = environmentType.trim(),
                                referenceImageUris = existingPhotos.joinToString(","),
                                hasReferenceAuthority = hasAuthority,
                                architecturalAnchor = architecturalAnchor.trim(),
                                lightingAtmosphereAnchor = lightingAtmosphereAnchor.trim(),
                                propsAndFoleyAnchor = propsAndFoleyAnchor.trim(),
                                timeOfDay = timeOfDay.trim()
                            )
                            onSave(entity, newSelectedPhotos.toList())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGoldPrimary, contentColor = CinemaBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Lock Set Reference", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
