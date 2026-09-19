package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CharacterEntity
import com.example.data.local.CineFlowDatabase
import com.example.data.local.LocationEntity
import com.example.data.local.SceneBridgeEntity
import com.example.data.local.SceneShotEntity
import com.example.data.local.UniverseBibleEntity
import com.example.data.repository.CineFlowRepository
import com.example.domain.AiStoryDirectorEngine
import com.example.domain.CineFlowPromptEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CineTab {
    VAULT,       // Character References & Continuity Anchors
    SETS,        // Location Master Sets & Environment References
    SEQUENCER,   // Scene Shotboard & Sequencer
    STUDIO,      // Omni Flash Prompt & Flow JSON Generator
    BIBLE        // Global Universe Rules
}

enum class PromptOutputMode {
    OMNI_FLASH_SYSTEM,
    FLOW_JSON,
    ANTI_DRIFT_MANIFEST,
    IMAGE_PROMPT,
    VIDEO_MOTION
}

data class CineFlowUiState(
    val activeTab: CineTab = CineTab.VAULT,
    val characters: List<CharacterEntity> = emptyList(),
    val locations: List<LocationEntity> = emptyList(),
    val universeBible: UniverseBibleEntity = UniverseBibleEntity(),
    val allScenes: List<String> = emptyList(),
    val selectedScene: String = "Ep1 Sc1: The Hook - Midnight Standoff",
    val sceneShots: List<SceneShotEntity> = emptyList(),
    val currentSceneBridge: SceneBridgeEntity? = null,
    val allSceneBridges: List<SceneBridgeEntity> = emptyList(),
    val isEditingBridgeDialog: Boolean = false,
    val selectedPromptMode: PromptOutputMode = PromptOutputMode.OMNI_FLASH_SYSTEM,
    val generatedOutputText: String = "",
    val isEditingCharacter: Boolean = false,
    val characterUnderEdit: CharacterEntity? = null,
    val isEditingLocation: Boolean = false,
    val locationUnderEdit: LocationEntity? = null,
    val isAddingShot: Boolean = false,
    val shotUnderEdit: SceneShotEntity? = null,
    val isCreatingSceneDialog: Boolean = false,
    val isAutoDirectDialog: Boolean = false,
    val isAutoDirecting: Boolean = false,
    val infoMessage: String? = null
)

class CineFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CineFlowRepository

    private val _activeTab = MutableStateFlow(CineTab.VAULT)
    private val _selectedScene = MutableStateFlow("Ep1 Sc1: The Hook - Midnight Standoff")
    private val _selectedPromptMode = MutableStateFlow(PromptOutputMode.OMNI_FLASH_SYSTEM)
    private val _editingCharacter = MutableStateFlow<CharacterEntity?>(null)
    private val _isEditingCharacter = MutableStateFlow(false)
    private val _editingLocation = MutableStateFlow<LocationEntity?>(null)
    private val _isEditingLocation = MutableStateFlow(false)
    private val _editingShot = MutableStateFlow<SceneShotEntity?>(null)
    private val _isAddingShot = MutableStateFlow(false)
    private val _isEditingBridgeDialog = MutableStateFlow(false)
    private val _isCreatingSceneDialog = MutableStateFlow(false)
    private val _isAutoDirectDialog = MutableStateFlow(false)
    private val _isAutoDirecting = MutableStateFlow(false)
    private val _infoMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CineFlowUiState>

    init {
        val database = CineFlowDatabase.getDatabase(application)
        repository = CineFlowRepository(database.dao(), application)

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }

        uiState = combine(
            _activeTab,
            repository.allCharacters,
            repository.allLocations,
            repository.universeBible,
            repository.allScenes,
            _selectedScene,
            repository.allShots,
            repository.allSceneBridges,
            _selectedPromptMode,
            _isEditingCharacter,
            _editingCharacter,
            _isEditingLocation,
            _editingLocation,
            _isAddingShot,
            _editingShot,
            _isEditingBridgeDialog,
            _isCreatingSceneDialog,
            _isAutoDirectDialog,
            _isAutoDirecting,
            _infoMessage
        ) { args: Array<Any?> ->
            val tab = args[0] as CineTab
            @Suppress("UNCHECKED_CAST")
            val chars = (args[1] as? List<CharacterEntity>) ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val locs = (args[2] as? List<LocationEntity>) ?: emptyList()
            val bible = (args[3] as? UniverseBibleEntity) ?: UniverseBibleEntity()
            @Suppress("UNCHECKED_CAST")
            val scenes = (args[4] as? List<String>) ?: emptyList()
            val selectedScene = args[5] as String
            @Suppress("UNCHECKED_CAST")
            val allShots = (args[6] as? List<SceneShotEntity>) ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val bridges = (args[7] as? List<SceneBridgeEntity>) ?: emptyList()
            val promptMode = args[8] as PromptOutputMode
            val isEditingChar = args[9] as Boolean
            val editingChar = args[10] as? CharacterEntity
            val isEditingLoc = args[11] as Boolean
            val editingLoc = args[12] as? LocationEntity
            val isAddingShot = args[13] as Boolean
            val editingShot = args[14] as? SceneShotEntity
            val isEditingBridge = args[15] as Boolean
            val isCreatingScene = args[16] as Boolean
            val isAutoDirect = args[17] as Boolean
            val isDirecting = args[18] as Boolean
            val infoMsg = args[19] as? String

            val filteredShots = allShots.filter { it.sceneTitle == selectedScene }
                .sortedBy { it.shotNumber }

            val currentBridge = bridges.find { it.sceneTitle == selectedScene }

            // Compute the active prompt output text
            val generatedText = when (promptMode) {
                PromptOutputMode.OMNI_FLASH_SYSTEM -> {
                    CineFlowPromptEngine.buildOmniFlashSystemPrompt(bible, chars, locs)
                }
                PromptOutputMode.FLOW_JSON -> {
                    CineFlowPromptEngine.buildFlowSceneJson(selectedScene, bible, filteredShots, chars, locs, currentBridge)
                }
                PromptOutputMode.ANTI_DRIFT_MANIFEST -> {
                    CineFlowPromptEngine.buildAntiDriftManifest(bible, chars, locs, filteredShots)
                }
                PromptOutputMode.IMAGE_PROMPT -> {
                    val firstShot = filteredShots.firstOrNull()
                    if (firstShot != null) {
                        val char = chars.find { it.codenameToken == firstShot.characterToken }
                        val loc = locs.find { it.setToken == firstShot.locationToken }
                        CineFlowPromptEngine.buildImagePrompt(bible, firstShot, char, loc)
                    } else {
                        "No shots in $selectedScene yet. Add shots to generate image prompts."
                    }
                }
                PromptOutputMode.VIDEO_MOTION -> {
                    val firstShot = filteredShots.firstOrNull()
                    if (firstShot != null) {
                        val char = chars.find { it.codenameToken == firstShot.characterToken }
                        val loc = locs.find { it.setToken == firstShot.locationToken }
                        CineFlowPromptEngine.buildVideoMotionPrompt(bible, firstShot, char, loc)
                    } else {
                        "No shots in $selectedScene yet. Add shots to generate video motion prompts."
                    }
                }
            }

            CineFlowUiState(
                activeTab = tab,
                characters = chars,
                locations = locs,
                universeBible = bible,
                allScenes = scenes.ifEmpty { listOf(selectedScene) },
                selectedScene = selectedScene,
                sceneShots = filteredShots,
                currentSceneBridge = currentBridge,
                allSceneBridges = bridges,
                isEditingBridgeDialog = isEditingBridge,
                selectedPromptMode = promptMode,
                generatedOutputText = generatedText,
                isEditingCharacter = isEditingChar,
                characterUnderEdit = editingChar,
                isEditingLocation = isEditingLoc,
                locationUnderEdit = editingLoc,
                isAddingShot = isAddingShot,
                shotUnderEdit = editingShot,
                isCreatingSceneDialog = isCreatingScene,
                isAutoDirectDialog = isAutoDirect,
                isAutoDirecting = isDirecting,
                infoMessage = infoMsg
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CineFlowUiState()
        )
    }

    fun selectTab(tab: CineTab) {
        _activeTab.value = tab
    }

    fun selectScene(scene: String) {
        _selectedScene.value = scene
    }

    fun selectPromptMode(mode: PromptOutputMode) {
        _selectedPromptMode.value = mode
    }

    fun openCharacterEditor(character: CharacterEntity? = null) {
        _editingCharacter.value = character
        _isEditingCharacter.value = true
    }

    fun closeCharacterEditor() {
        _editingCharacter.value = null
        _isEditingCharacter.value = false
    }

    fun saveCharacterWithPhotos(
        character: CharacterEntity,
        newPhotoUris: List<Uri>
    ) {
        viewModelScope.launch {
            val savedPaths = mutableListOf<String>()
            if (character.referenceImageUris.isNotBlank()) {
                savedPaths.addAll(character.referenceImageUris.split(",").filter { it.isNotBlank() })
            }
            for (uri in newPhotoUris) {
                val savedPath = repository.saveReferenceImage(uri)
                savedPaths.add(savedPath)
            }
            val finalUris = savedPaths.distinct().joinToString(",")
            val updatedChar = character.copy(referenceImageUris = finalUris)

            if (updatedChar.id == 0L) {
                repository.insertCharacter(updatedChar)
                showToast("Character created with verified continuity authority")
            } else {
                repository.updateCharacter(updatedChar)
                showToast("Character continuity anchors updated")
            }
            closeCharacterEditor()
        }
    }

    fun deleteCharacter(id: Long) {
        viewModelScope.launch {
            repository.deleteCharacter(id)
            showToast("Character removed")
        }
    }

    fun openLocationEditor(location: LocationEntity? = null) {
        _editingLocation.value = location
        _isEditingLocation.value = true
    }

    fun closeLocationEditor() {
        _editingLocation.value = null
        _isEditingLocation.value = false
    }

    fun saveLocationWithPhotos(
        location: LocationEntity,
        newPhotoUris: List<Uri>
    ) {
        viewModelScope.launch {
            val savedPaths = mutableListOf<String>()
            if (location.referenceImageUris.isNotBlank()) {
                savedPaths.addAll(location.referenceImageUris.split(",").filter { it.isNotBlank() })
            }
            for (uri in newPhotoUris) {
                val savedPath = repository.saveLocationReferenceImage(uri)
                savedPaths.add(savedPath)
            }
            val finalUris = savedPaths.distinct().joinToString(",")
            val updatedLoc = location.copy(referenceImageUris = finalUris)

            if (updatedLoc.id == 0L) {
                repository.insertLocation(updatedLoc)
                showToast("Master Set Reference '${updatedLoc.name}' locked!")
            } else {
                repository.updateLocation(updatedLoc)
                showToast("Location Reference updated")
            }
            closeLocationEditor()
        }
    }

    fun deleteLocation(id: Long) {
        viewModelScope.launch {
            repository.deleteLocation(id)
            showToast("Location Set removed")
        }
    }

    fun openShotEditor(shot: SceneShotEntity? = null) {
        _editingShot.value = shot
        _isAddingShot.value = true
    }

    fun closeShotEditor() {
        _editingShot.value = null
        _isAddingShot.value = false
    }

    fun saveShot(shot: SceneShotEntity) {
        viewModelScope.launch {
            if (shot.id == 0L) {
                repository.insertShot(shot)
                showToast("Shot added to ${shot.sceneTitle}")
            } else {
                repository.updateShot(shot)
                showToast("Shot updated")
            }
            closeShotEditor()
        }
    }

    fun deleteShot(id: Long) {
        viewModelScope.launch {
            repository.deleteShot(id)
            showToast("Shot removed")
        }
    }

    fun openCreateSceneDialog() {
        _isCreatingSceneDialog.value = true
    }

    fun closeCreateSceneDialog() {
        _isCreatingSceneDialog.value = false
    }

    fun openAutoDirectDialog() {
        _isAutoDirectDialog.value = true
    }

    fun closeAutoDirectDialog() {
        _isAutoDirectDialog.value = false
    }

    fun autoDirectScene(storyPremise: String, genre: String, shotCount: Int) {
        viewModelScope.launch {
            _isAutoDirecting.value = true
            try {
                val state = uiState.value
                val result = AiStoryDirectorEngine.directStoryboard(
                    storyPremise = storyPremise,
                    genre = genre,
                    shotCount = shotCount,
                    characters = state.characters,
                    locations = state.locations,
                    bible = state.universeBible,
                    existingScenes = state.allScenes
                )

                // Insert all shots into database
                for (shot in result.shots) {
                    repository.insertShot(shot)
                }

                // Save transition continuity bridge
                repository.saveSceneBridge(result.bridge)

                _selectedScene.value = result.sceneTitle
                _isAutoDirectDialog.value = false
                showToast("✨ AI Auto-Directed ${result.shots.size} shots for '${result.sceneTitle}'!")
            } catch (e: Exception) {
                showToast("Error directing scene: ${e.message}")
            } finally {
                _isAutoDirecting.value = false
            }
        }
    }

    fun createScene(sceneTitle: String) {
        val trimmed = sceneTitle.trim()
        if (trimmed.isNotBlank()) {
            _selectedScene.value = trimmed
            _isCreatingSceneDialog.value = false
            // Add a placeholder shot so the scene persists in distinct list
            viewModelScope.launch {
                val existing = repository.getShotsForScene(trimmed)
                // If no shots, add shot 1
                repository.insertShot(
                    SceneShotEntity(
                        sceneTitle = trimmed,
                        shotNumber = 1,
                        shotType = "Wide Shot",
                        cameraMotion = "Slow Dolly In",
                        lensMm = "35mm Anamorphic",
                        characterToken = uiState.value.characters.firstOrNull()?.codenameToken ?: "[PROTAGONIST]",
                        actionDescription = "Enters the environment, scanning the surroundings cautiously.",
                        environment = "Cinematic set interior, atmospheric depth, smoke and low-key lighting.",
                        lighting = "Warm key lighting with cool anamorphic backlight.",
                        audioFoley = "Subtle ambient room tone, footsteps.",
                        durationSeconds = 8
                    )
                )
                showToast("Scene '$trimmed' created (8s max)")
            }
        }
    }

    fun openBridgeEditor() {
        _isEditingBridgeDialog.value = true
    }

    fun closeBridgeEditor() {
        _isEditingBridgeDialog.value = false
    }

    fun saveSceneBridge(bridge: SceneBridgeEntity) {
        viewModelScope.launch {
            repository.saveSceneBridge(bridge)
            _isEditingBridgeDialog.value = false
            showToast("Scene Continuity Bridge updated!")
        }
    }

    fun loadEpisodeTemplate(episodeNumber: Int) {
        viewModelScope.launch {
            val epPrefix = "Ep$episodeNumber"
            val beats = listOf(
                "Sc1: The Hook - Opening Impact",
                "Sc2: Inciting Incident - Disruption",
                "Sc3: Escalation - Conflict Sparks",
                "Sc4: Revelation - Secret Uncovered",
                "Sc5: Midpoint Climax - High Tension",
                "Sc6: Complication - The Retaliation",
                "Sc7: Standoff - Direct Confrontation",
                "Sc8: The Twist - Reality Shifts",
                "Sc9: Cliffhanger - The Breaking Point"
            )
            val charToken = uiState.value.characters.firstOrNull()?.codenameToken ?: "[PROTAGONIST]"
            beats.forEachIndexed { index, beat ->
                val fullTitle = "$epPrefix $beat"
                val nextTitle = if (index < beats.size - 1) "$epPrefix ${beats[index + 1]}" else "$epPrefix End / Ep${episodeNumber + 1} Teaser"
                repository.insertShot(
                    SceneShotEntity(
                        sceneTitle = fullTitle,
                        shotNumber = 1,
                        shotType = when (index % 4) {
                            0 -> "Extreme Wide Shot"
                            1 -> "Medium Close-Up"
                            2 -> "Low Angle Tracking"
                            else -> "Over-The-Shoulder"
                        },
                        cameraMotion = when (index % 3) {
                            0 -> "Slow Dolly In"
                            1 -> "Tracking Steadicam"
                            else -> "Slow Jib Up"
                        },
                        lensMm = "35mm Anamorphic",
                        characterToken = charToken,
                        actionDescription = "Executes vertical micro-drama beat $index in 9:16 framing.",
                        environment = "Atmospheric cinematic set, vertical depth.",
                        lighting = "High-contrast chiaroscuro lighting.",
                        audioFoley = "Subtle ambient room tone, tension bass pulse.",
                        durationSeconds = 8
                    )
                )

                // Pre-generate intelligent Scene Continuity Bridge
                repository.saveSceneBridge(
                    SceneBridgeEntity(
                        sceneTitle = fullTitle,
                        nextSceneTitle = nextTitle,
                        outgoingAnchorState = "Character locks gaze on target, breathing heavily in rain; clenched left fist holding key object.",
                        transitionCutType = when (index % 4) {
                            0 -> "Match Cut (Action / Sound)"
                            1 -> "Audio J-Cut Lead (Next Scene Foley Enters 1.5s Early)"
                            2 -> "Hard Cut on Peak Movement"
                            else -> "Smash Cut to High Tension"
                        },
                        transitionAudioBridge = "Subtle heartbeat pulse or bass drop cuts abruptly on visual seam.",
                        timeElapsedDelta = if (index == 0) "Continuous (0s - Immediate Next Beat)" else "Continuous (+15 Seconds Later)",
                        propCarryover = "Key object in hand, damp collar seam, identical lateral scar orientation.",
                        incomingLeadIn = "Next scene starts with immediate reaction shot preserving momentum and eyeline angle.",
                        dramaticTensionRating = 70 + (index * 3).coerceAtMost(28)
                    )
                )
            }
            _selectedScene.value = "$epPrefix ${beats[0]}"
            showToast("Episode $episodeNumber (9 scenes + Continuity Bridges) generated!")
        }
    }

    fun saveUniverseBible(bible: UniverseBibleEntity) {
        viewModelScope.launch {
            repository.saveUniverseBible(bible)
            showToast("Universe Master Rules saved")
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        showToast("Copied $label to clipboard")
    }

    private fun showToast(msg: String) {
        Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
    }
}
