package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.CharacterEntity
import com.example.data.local.CineFlowDao
import com.example.data.local.LocationEntity
import com.example.data.local.SceneBridgeEntity
import com.example.data.local.SceneShotEntity
import com.example.data.local.UniverseBibleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CineFlowRepository(
    private val dao: CineFlowDao,
    private val context: Context
) {
    val allCharacters: Flow<List<CharacterEntity>> = dao.getAllCharacters()
    val allLocations: Flow<List<LocationEntity>> = dao.getAllLocations()
    val universeBible: Flow<UniverseBibleEntity?> = dao.getUniverseBible()
    val allShots: Flow<List<SceneShotEntity>> = dao.getAllShots()
    val allScenes: Flow<List<String>> = dao.getAllScenes()

    fun getShotsForScene(sceneTitle: String): Flow<List<SceneShotEntity>> =
        dao.getShotsForScene(sceneTitle)

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val currentBible = universeBible.firstOrNull()
        if (currentBible == null) {
            dao.saveUniverseBible(UniverseBibleEntity())
        }

        val currentLocations = allLocations.firstOrNull()
        if (currentLocations.isNullOrEmpty()) {
            val starterLocations = listOf(
                LocationEntity(
                    name = "Neon Alleyway & Ramen Alcove",
                    setToken = "[SET_NEON_ALLEYWAY]",
                    environmentType = "Urban Exterior / Cyberpunk Dystopia",
                    referenceImageUris = "",
                    hasReferenceAuthority = true,
                    architecturalAnchor = "Narrow wet asphalt alleyway flanked by exposed dripping industrial conduit pipes, rust-eaten corrugated iron awnings, steam grates venting white condensation, overhead tangled telecom cables.",
                    lightingAtmosphereAnchor = "Low-key chiaroscuro, flickering amber sodium vapor lamps from a shuttered noodle stall, high-contrast cyan neon reflections pooling in street puddles, volumetric rain haze.",
                    propsAndFoleyAnchor = "Dripping rusted drainage gutters, distant low hover-traffic drone, flickering neon transformer hum.",
                    timeOfDay = "Midnight / Torrential Rain"
                ),
                LocationEntity(
                    name = "Underground Server Vault 09",
                    setToken = "[SET_SERVER_VAULT]",
                    environmentType = "High-Security Interior / Tech Bunker",
                    referenceImageUris = "",
                    hasReferenceAuthority = true,
                    architecturalAnchor = "Reinforced circular blast chamber made of brushed dark gunmetal titanium tiles, floor-to-ceiling server towers with vertical pulsating optical data ribbons, recessed hydraulic airlock doors.",
                    lightingAtmosphereAnchor = "Cold arctic blue and ultraviolet data rack LEDs, harsh overhead white fluorescent panels casting sharp geometric floor shadows, pulsing red hazard perimeter lights.",
                    propsAndFoleyAnchor = "Liquid nitrogen cooling mist vents at floor level, heavy pressurized airlock hiss, high-speed optical cooling fans.",
                    timeOfDay = "Subterranean / Controlled Artificial"
                ),
                LocationEntity(
                    name = "Syndicate Penthouse Balcony",
                    setToken = "[SET_PENTHOUSE_BALCONY]",
                    environmentType = "Luxury Overlook / High-Rise",
                    referenceImageUris = "",
                    hasReferenceAuthority = true,
                    architecturalAnchor = "Cantilevered tempered glass and polished obsidian marble veranda on the 88th floor overlooking an endless smog-veiled megacity skyline, brushed brass railing with built-in biometric sensors.",
                    lightingAtmosphereAnchor = "Golden hour twilight transitioning into deep indigo, warm interior tungsten spill through floor-to-ceiling panoramic glass, holographic city billboards casting moving multicolored reflections.",
                    propsAndFoleyAnchor = "Heavy wind buffeting against glass edge, low urban city roar, antique crystal tumbler on marble ledge.",
                    timeOfDay = "Golden Hour Dusk / Approaching Smog Storm"
                )
            )
            starterLocations.forEach { dao.insertLocation(it) }
        }

        val currentChars = allCharacters.firstOrNull()
        if (currentChars.isNullOrEmpty()) {
            val starterCharacters = listOf(
                CharacterEntity(
                    name = "Rei Tanaka",
                    codenameToken = "[REI]",
                    role = "Lead Cyber-Detective / Protagonist",
                    referenceImageUris = "",
                    hasReferenceAuthority = true,
                    physicalAnchor = "28-year-old Japanese woman, obsidian black blunt bob haircut with asymmetrical bangs, striking hazel-amber eyes, faint 2cm diagonal scar across the left bridge of her nose.",
                    bodySomatotypeAnchor = "168cm tall, lean athletic mesomorph build, narrow waist, broad athletic clavicles, balanced upright posture.",
                    craniofacialAnchor = "High angular zygomatic cheekbones, straight nasal bridge with sharp tip, defined sharp jawline, almond-shaped hooded eyes, neutral pupillary distance.",
                    wardrobeAnchor = "High-collared matte charcoal ballistic trench coat with worn collar seams, dark titanium lapel pin, layered dark slate tactical turtle-neck sweater, matte black combat trousers.",
                    signatureAura = "Intense piercing gaze, minimal blinks, stoic posture with guarded shoulder tension, slight jaw clench when observing details.",
                    voiceAudioProfile = "ElevenLabs: 'Rachel - Crisp Low Pacing', calm, gravelly, quiet intensity."
                ),
                CharacterEntity(
                    name = "Marcus Vance",
                    codenameToken = "[MARCUS]",
                    role = "Rogue Syndicate Tactician",
                    referenceImageUris = "",
                    hasReferenceAuthority = true,
                    physicalAnchor = "42-year-old Caucasian male, salt-and-pepper cropped crew cut, intense storm-grey eyes, cybernetic ocular implant rim in left temple with faint amber glow, square stubbled jaw.",
                    bodySomatotypeAnchor = "186cm tall, broad-shouldered heavy endo-mesomorph build, thick neck, powerful imposing frame, slight forward-leaning tactical stance.",
                    craniofacialAnchor = "Heavy brow ridge, deep-set ocular sockets, prominent square mandibular angle, straight broken nasal bridge, weathered skin pores.",
                    wardrobeAnchor = "Distressed olive-drab flight bomber jacket with faded squadron patch, distressed dark denim, fingerless carbon-fiber gloves, brushed bronze pendant.",
                    signatureAura = "Calculated relaxed slouch masking explosive reflexes, wry skeptical smirk, constant scanning of perimeter exits.",
                    voiceAudioProfile = "ElevenLabs: 'Adam - Deep Gritty', measured, baritone, authoritative."
                ),
                CharacterEntity(
                    name = "Aurelia Voss",
                    codenameToken = "[AURELIA]",
                    role = "Bio-Alchemist & Informant",
                    referenceImageUris = "",
                    hasReferenceAuthority = true,
                    physicalAnchor = "31-year-old Afro-descendant woman, intricate copper-threaded braided cornrows swept into an architectural high bun, deep espresso brown eyes, glowing iridescent micro-dermal markings along collarbone.",
                    bodySomatotypeAnchor = "175cm tall, slender ectomorph frame, long graceful neck, high shoulder line, fluid dancer-like poise.",
                    craniofacialAnchor = "High sculpted heart-shaped cheekbones, full defined lips, aristocratic slender nasal bridge, large expressive wide-set almond eyes.",
                    wardrobeAnchor = "Iridescent midnight-plum tailored velvet coat, high copper-trimmed mandarin collar, antique mechanical brass rings on four fingers.",
                    signatureAura = "Hypnotic stillness, graceful fluid hand gestures, piercing analytical eye contact.",
                    voiceAudioProfile = "ElevenLabs: 'Serena - Velvety European Accent', smooth, melodic, enigmatic."
                )
            )
            starterCharacters.forEach { dao.insertCharacter(it) }
        }

        val currentShots = allShots.firstOrNull()
        if (currentShots.isNullOrEmpty()) {
            val starterShots = listOf(
                // Scene 1: The Hook (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc1: The Hook - Midnight Standoff",
                    shotNumber = 1,
                    shotType = "Extreme Wide Shot",
                    cameraMotion = "Slow Jib Up",
                    lensMm = "24mm Anamorphic",
                    characterToken = "[REI]",
                    actionDescription = "Stands motionless under a flickering holographic ramen stall sign while torrential rain pours onto the reflective neon asphalt.",
                    environment = "Narrow dystopian alleyway lined with dripping conduit pipes, neon cyan and magenta signs diffused through heavy misty rain.",
                    lighting = "High contrast chiaroscuro, sodium vapor amber key light from street vendor cart, cool cyan rain rim light.",
                    audioFoley = "Heavy rhythmic rainfall on corrugated tin, distant hover-car turbine thrum.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc1: The Hook - Midnight Standoff",
                    shotNumber = 2,
                    shotType = "Medium Close-Up",
                    cameraMotion = "Slow Dolly In",
                    lensMm = "65mm Anamorphic",
                    characterToken = "[REI]",
                    actionDescription = "Slowly turns head toward the shadow of the doorway, rain droplets beading on cheekbone, eyes narrowing with sharp vigilance.",
                    environment = "Rain-streaked dystopian alleyway, vertical bokeh with glowing teal and amber reflections.",
                    lighting = "Diffused soft frontal fill, harsh amber rim light outlining left jaw and cheek scar.",
                    audioFoley = "Subtle leather flex of trench coat collar, muffled footstep in puddle.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 2: Inciting Incident (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc2: Inciting Incident - Encrypted Drive",
                    shotNumber = 1,
                    shotType = "Over-The-Shoulder",
                    cameraMotion = "Static Tripod",
                    lensMm = "50mm T/2.0",
                    characterToken = "[MARCUS]",
                    actionDescription = "Steps halfway out of the dark brick doorway, holding a damp glowing encrypted data drive toward Rei.",
                    environment = "Brick alcove behind rusted metal security grating, puddles reflecting streetlamps.",
                    lighting = "Backlit silhouette through doorway, specular highlight glinting off cybernetic temple implant.",
                    audioFoley = "Metallic click of lighter flipping open, deep quiet breath.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 3: Escalation (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc3: Escalation - Drone Perimeter Breach",
                    shotNumber = 1,
                    shotType = "Low Angle Tracking",
                    cameraMotion = "Tracking Steadicam",
                    lensMm = "35mm Anamorphic",
                    characterToken = "[REI]",
                    actionDescription = "Draws tactical sidearm in one fluid motion as red surveillance searchlights sweep down from high above.",
                    environment = "Alleyway intersection under high-rise megastructure, sirens echoing.",
                    lighting = "Crimson scanner beams slicing through mist and torrential rain.",
                    audioFoley = "High-pitched drone rotor whine, sharp metallic click of firearm slide.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 4: Secret Revealed (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc4: Secret Revealed - Biometric Key",
                    shotNumber = 1,
                    shotType = "Extreme Close-Up",
                    cameraMotion = "Slow Dolly In",
                    lensMm = "100mm Macro",
                    characterToken = "[AURELIA]",
                    actionDescription = "Touches holographic crystal display as glowing copper dermal circuitry pulses beneath the skin of her fingertips.",
                    environment = "Dimly lit clandestine bio-lab, glassware filled with luminescent cyan fluid.",
                    lighting = "Phosphorescent cyan glow from micro-dermal markings casting soft upward light.",
                    audioFoley = "Subtle synthesizer resonance, electric ionization hum.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 5: Midpoint Climax (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc5: Midpoint Climax - Rooftop Pursuit",
                    shotNumber = 1,
                    shotType = "Wide Tracking Shot",
                    cameraMotion = "Tracking Steadicam",
                    lensMm = "35mm Cinema",
                    characterToken = "[MARCUS]",
                    actionDescription = "Sprints across rain-slicked industrial rooftop, vaulting over massive AC exhaust duct into darkness.",
                    environment = "Skyline rooftop overlooking sprawling neon megalopolis 200 floors below.",
                    lighting = "Massive holographic billboard casting pulsating magenta and violet light.",
                    audioFoley = "Heavy boots pounding wet metal, gusting rooftop wind.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 6: Complication (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc6: Complication - Transit Hub Ambush",
                    shotNumber = 1,
                    shotType = "Medium Shot",
                    cameraMotion = "Lateral Pan Right",
                    lensMm = "50mm T/2.0",
                    characterToken = "[REI]",
                    actionDescription = "Ducks behind concrete pillar as kinetic energy rounds shatter glass transit displays behind her.",
                    environment = "Empty subterranean maglev train platform with shattered holographic timetables.",
                    lighting = "Strobe flashes from muzzle blasts, sparking severed high-voltage cables.",
                    audioFoley = "Explosive glass shattering, sharp kinetic impacts on reinforced concrete.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 7: High-Stakes Standoff (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc7: High-Stakes Standoff - Face Off",
                    shotNumber = 1,
                    shotType = "Dutch Angle Close-Up",
                    cameraMotion = "Dutch Angle Drift",
                    lensMm = "65mm Anamorphic",
                    characterToken = "[MARCUS]",
                    actionDescription = "Aims sidearm directly ahead, cybernetic ocular implant whirring as aperture iris contracts.",
                    environment = "Abandoned server vault surrounded by silent cooling towers.",
                    lighting = "Harsh green status LEDs reflecting in his storm-grey eyes and weathered brow.",
                    audioFoley = "Mechanical servo whir, tense quiet breathing.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 8: The Twist (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc8: The Twist - Double Agent Exposed",
                    shotNumber = 1,
                    shotType = "Medium Close-Up",
                    cameraMotion = "Slow Dolly In",
                    lensMm = "85mm Portrait",
                    characterToken = "[AURELIA]",
                    actionDescription = "Lowers hood to reveal the syndicate crest branded onto her neck, a slow cold smile forming.",
                    environment = "Shadowed server room perimeter, cooling mist venting around ankles.",
                    lighting = "Rim light in deep amber, shadow cloaking half of face.",
                    audioFoley = "Rustle of heavy velvet, distant alarm countdown klaxon.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                ),
                // Scene 9: Cliffhanger (8s)
                SceneShotEntity(
                    sceneTitle = "Ep1 Sc9: Cliffhanger - Vault Detonation",
                    shotNumber = 1,
                    shotType = "Extreme Close-Up to Wide",
                    cameraMotion = "Slow Dolly Out",
                    lensMm = "24mm Wide Anamorphic",
                    characterToken = "[REI]",
                    actionDescription = "Glares down at the blinking detonator counter hitting 00:03 as blast doors slam shut with finality.",
                    environment = "Reinforced titanium blast airlock, red emergency strobe lights flashing violently.",
                    lighting = "Pulsing emergency red sirens casting intense shadow shifts.",
                    audioFoley = "Heavy hydraulic door thud, high-priority emergency countdown siren.",
                    durationSeconds = 8,
                    generatedPrompt = ""
                )
            )
            starterShots.forEach { dao.insertShot(it) }
        }
    }

    suspend fun saveReferenceImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val refDir = File(context.filesDir, "character_refs")
            if (!refDir.exists()) refDir.mkdirs()
            val fileName = "ref_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val destFile = File(refDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            uri.toString()
        }
    }

    suspend fun saveLocationReferenceImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val refDir = File(context.filesDir, "location_refs")
            if (!refDir.exists()) refDir.mkdirs()
            val fileName = "loc_ref_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg"
            val destFile = File(refDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            uri.toString()
        }
    }

    suspend fun insertLocation(location: LocationEntity): Long = withContext(Dispatchers.IO) {
        dao.insertLocation(location)
    }

    suspend fun updateLocation(location: LocationEntity) = withContext(Dispatchers.IO) {
        dao.updateLocation(location)
    }

    suspend fun deleteLocation(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteLocationById(id)
    }

    suspend fun insertCharacter(character: CharacterEntity): Long = withContext(Dispatchers.IO) {
        dao.insertCharacter(character)
    }

    suspend fun updateCharacter(character: CharacterEntity) = withContext(Dispatchers.IO) {
        dao.updateCharacter(character)
    }

    suspend fun deleteCharacter(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteCharacterById(id)
    }

    suspend fun saveUniverseBible(bible: UniverseBibleEntity) = withContext(Dispatchers.IO) {
        dao.saveUniverseBible(bible)
    }

    suspend fun insertShot(shot: SceneShotEntity): Long = withContext(Dispatchers.IO) {
        dao.insertShot(shot)
    }

    suspend fun updateShot(shot: SceneShotEntity) = withContext(Dispatchers.IO) {
        dao.updateShot(shot)
    }

    suspend fun deleteShot(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteShotById(id)
    }

    suspend fun deleteScene(sceneTitle: String) = withContext(Dispatchers.IO) {
        dao.deleteScene(sceneTitle)
        dao.deleteSceneBridge(sceneTitle)
    }

    // Scene Transition Bridges
    val allSceneBridges: Flow<List<SceneBridgeEntity>> = dao.getAllSceneBridges()

    fun getBridgeForScene(sceneTitle: String): Flow<SceneBridgeEntity?> =
        dao.getBridgeForScene(sceneTitle)

    suspend fun saveSceneBridge(bridge: SceneBridgeEntity) = withContext(Dispatchers.IO) {
        dao.saveSceneBridge(bridge)
    }
}
