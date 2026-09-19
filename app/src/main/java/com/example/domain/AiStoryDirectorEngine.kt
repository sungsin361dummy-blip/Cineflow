package com.example.domain

import com.example.data.local.CharacterEntity
import com.example.data.local.LocationEntity
import com.example.data.local.SceneBridgeEntity
import com.example.data.local.SceneShotEntity
import com.example.data.local.UniverseBibleEntity

data class DirectedSceneResult(
    val sceneTitle: String,
    val shots: List<SceneShotEntity>,
    val bridge: SceneBridgeEntity
)

object AiStoryDirectorEngine {

    fun directStoryboard(
        storyPremise: String,
        genre: String,
        shotCount: Int,
        characters: List<CharacterEntity>,
        locations: List<LocationEntity>,
        bible: UniverseBibleEntity,
        existingScenes: List<String>
    ): DirectedSceneResult {
        val cleanPremise = if (storyPremise.isBlank()) "High-stakes confrontation under neon lights and sudden rain" else storyPremise.trim()
        
        // Determine scene title
        val sceneNum = existingScenes.size + 1
        val sceneSlug = generateSceneSlug(cleanPremise, genre)
        val sceneTitle = "Ep1 Sc$sceneNum: $sceneSlug"

        // Pick primary and secondary characters
        val char1 = characters.getOrNull(0)?.codenameToken ?: "[PROTAGONIST_LEAD]"
        val char2 = characters.getOrNull(1)?.codenameToken ?: "[ANTAGONIST_RIVAL]"

        // Pick primary location
        val matchedLocation = locations.firstOrNull { loc ->
            cleanPremise.contains(loc.name, ignoreCase = true) || 
            cleanPremise.contains("alley", ignoreCase = true) || 
            cleanPremise.contains("rain", ignoreCase = true) ||
            cleanPremise.contains("neon", ignoreCase = true)
        } ?: locations.firstOrNull()
        val locToken = matchedLocation?.setToken ?: "[LOC_SET_01]"
        val envBase = matchedLocation?.let { "${it.name}: ${it.architecturalAnchor}" } 
            ?: "Atmospheric cinematic set with deep perspective and textural shadows"
        val lightingBase = matchedLocation?.lightingAtmosphereAnchor 
            ?: "Chiaroscuro high-contrast lighting with cold specular reflections"

        val shots = mutableListOf<SceneShotEntity>()

        // Analyze theme/keywords in premise (supports Tagalog & English)
        val isRain = cleanPremise.contains("ulan", ignoreCase = true) || cleanPremise.contains("rain", ignoreCase = true) || cleanPremise.contains("basa", ignoreCase = true)
        val isFight = cleanPremise.contains("away", ignoreCase = true) || cleanPremise.contains("fight", ignoreCase = true) || cleanPremise.contains("baril", ignoreCase = true) || cleanPremise.contains("gun", ignoreCase = true) || cleanPremise.contains("tapatan", ignoreCase = true)
        val isSecret = cleanPremise.contains("secret", ignoreCase = true) || cleanPremise.contains("lihim", ignoreCase = true) || cleanPremise.contains("code", ignoreCase = true) || cleanPremise.contains("drive", ignoreCase = true) || cleanPremise.contains("file", ignoreCase = true)
        val isEmotional = cleanPremise.contains("iyak", ignoreCase = true) || cleanPremise.contains("luha", ignoreCase = true) || cleanPremise.contains("mahal", ignoreCase = true) || cleanPremise.contains("love", ignoreCase = true) || cleanPremise.contains("traydor", ignoreCase = true) || cleanPremise.contains("betray", ignoreCase = true)

        val weatherCond = if (isRain) "Heavy Rain & Wet Asphalt Specular Reflections" else "Atmospheric Fog with Suspended Dust Particulates"
        val timeOfDay = if (genre.contains("Noir") || genre.contains("Cyberpunk")) "Night / Rain (02:00 AM)" else "Twilight / Blue Hour"

        // Shot 1: Establishing / Environmental Hook
        shots.add(
            SceneShotEntity(
                sceneTitle = sceneTitle,
                shotNumber = 1,
                shotType = "Wide Shot",
                cameraMotion = "Slow Dolly In",
                lensMm = "24mm Wide Anamorphic",
                characterToken = char1,
                locationToken = locToken,
                actionDescription = "$char1 steps into the frame, drenched in atmosphere. Silhouetted against flickering practical neon lights, scanning the perimeter with razor focus.",
                dialogueSpoken = "",
                dialogueDelivery = "(Silent tension / ambient breathing)",
                cameraFramingIntent = "Speaker Direct (A-Cam)",
                timeOfDay = timeOfDay,
                weatherAtmosphere = weatherCond,
                extrasCrowdDensity = "Sparse Distant Silhouettes",
                environment = envBase,
                lighting = "$lightingBase, deep rim lighting catching moisture on jacket",
                audioFoley = if (isRain) "Heavy rainfall drumming on metal awning, distant thunder rumble." else "Low industrial drone, humming fluorescent ballast.",
                eyelineVector = "Center-Left 15°",
                keyLightAngle = "45° Camera Left Key",
                durationSeconds = 4
            )
        )

        // Shot 2: Encounter / The Two-Shot (Framing Scale: Medium Shot, Intent: Two-Shot)
        val encounterAction = if (isFight) {
            "$char1 and $char2 face each other at a tense four-foot distance. Neither breaks posture; fists clenched, coats dripping."
        } else if (isSecret) {
            "$char2 steps out from the darkness into the light pool opposite $char1, holding an encrypted titanium datapad."
        } else {
            "$char1 confronts $char2 in profile. The heavy emotional silence between them is palpable as water beads down their faces."
        }

        shots.add(
            SceneShotEntity(
                sceneTitle = sceneTitle,
                shotNumber = 2,
                shotType = "Medium Shot",
                cameraMotion = "Lateral Pan Right",
                lensMm = "35mm Cinema",
                characterToken = char1,
                locationToken = locToken,
                actionDescription = encounterAction,
                dialogueSpoken = if (isSecret) "Alam kong hawak mo ang files. Hindi ka makakalabas dito nang buhay kung hindi mo ibibigay." else "Akala mo ba matatakasan mo ang ginawa mo?",
                dialogueDelivery = "Low, measured rasp with restrained venom",
                cameraFramingIntent = "Two-Shot (Both in Frame)",
                timeOfDay = timeOfDay,
                weatherAtmosphere = weatherCond,
                extrasCrowdDensity = "Zero Extras (Desolate)",
                environment = envBase,
                lighting = "Balanced dual-key chiaroscuro illuminating both character profiles",
                audioFoley = "Water splashing under combat boots, metallic click.",
                eyelineVector = "Screen-Right 30°",
                keyLightAngle = "45° Camera Left Key",
                durationSeconds = 6
            )
        )

        // Shot 3: Over-The-Shoulder (OTS Speaker Focus)
        shots.add(
            SceneShotEntity(
                sceneTitle = sceneTitle,
                shotNumber = 3,
                shotType = "Over-The-Shoulder",
                cameraMotion = "Tracking Steadicam",
                lensMm = "50mm T/2.0",
                characterToken = char2,
                locationToken = locToken,
                actionDescription = "Framed over $char1's shoulder. $char2 smirks coldly under the streetlight, eyes unblinking, showing zero remorse.",
                dialogueSpoken = if (isSecret) "Masyado kang huli. Na-decrypt na ang system tatlumpung minuto na ang nakalilipas." else "Wala kang ebidensya laban sa akin. Subukan mo akong pigilan.",
                dialogueDelivery = "Cold, cynical cadence; slight condescending smirk",
                cameraFramingIntent = "Over-The-Shoulder (OTS Favoring)",
                timeOfDay = timeOfDay,
                weatherAtmosphere = weatherCond,
                extrasCrowdDensity = "Zero Extras (Desolate)",
                environment = envBase,
                lighting = "45° Camera Right Key, high specular catchlight in $char2's iris",
                audioFoley = "Wind howling through narrow corridor, distant police siren echoing.",
                eyelineVector = "Screen-Left 30°",
                keyLightAngle = "45° Camera Right Key",
                durationSeconds = 5
            )
        )

        if (shotCount >= 4) {
            // Shot 4: Reaction Close-Up (Preserving 180° Eyeline Vector & Key Light continuity)
            shots.add(
                SceneShotEntity(
                    sceneTitle = sceneTitle,
                    shotNumber = 4,
                    shotType = "Close-Up",
                    cameraMotion = "Slow Dolly In",
                    lensMm = "85mm Portrait",
                    characterToken = char1,
                    locationToken = locToken,
                    actionDescription = "Tight framing on $char1's expression. Jaw muscles twitching, raindrops running down the permanent jaw scar, realizing the full scope of betrayal.",
                    dialogueSpoken = "Kung ganoon... wala na akong dahilan para itira ka.",
                    dialogueDelivery = "Deadly whisper under breath, absolute finality",
                    cameraFramingIntent = "Reaction Shot (B-Cam)",
                    timeOfDay = timeOfDay,
                    weatherAtmosphere = weatherCond,
                    extrasCrowdDensity = "Zero Extras (Desolate)",
                    environment = envBase,
                    lighting = "45° Camera Left Key matching Shot 2, dramatic cheekbone shadows",
                    audioFoley = "Heavy heartbeat thump, fabric rustle as hand reaches inside coat.",
                    eyelineVector = "Screen-Right 30°",
                    keyLightAngle = "45° Camera Left Key",
                    durationSeconds = 4
                )
            )
        }

        if (shotCount >= 5) {
            // Shot 5: Reveal / Breaking Point Insert / Climax
            val isProp = isSecret || isFight
            shots.add(
                SceneShotEntity(
                    sceneTitle = sceneTitle,
                    shotNumber = 5,
                    shotType = if (isProp) "Insert / Cutaway Shot" else "Extreme Close-Up",
                    cameraMotion = "Dutch Angle Drift",
                    lensMm = "100mm Macro",
                    characterToken = char1,
                    locationToken = locToken,
                    actionDescription = if (isSecret) {
                        "Extreme macro on the glowing digital encryption key falling onto the wet puddles, sparks hissing against the water."
                    } else {
                        "Macro focus on $char1's finger tightening on the trigger / device button as the reflections in the puddle ripple violently."
                    },
                    dialogueSpoken = "",
                    dialogueDelivery = "(Action climax beat)",
                    cameraFramingIntent = "Insert / Cutaway Shot",
                    timeOfDay = timeOfDay,
                    weatherAtmosphere = weatherCond,
                    extrasCrowdDensity = "Zero Extras (Desolate)",
                    environment = envBase,
                    lighting = "Anamorphic cyan rim light on metallic surface with fiery orange practical reflections",
                    audioFoley = "Sharp metallic cocking / electronic chime cut short by sudden thunder.",
                    eyelineVector = "Direct Camera (Eye Contact)",
                    keyLightAngle = "Top-Down Overhead Chiaroscuro",
                    durationSeconds = 3
                )
            )
        }

        if (shotCount >= 8) {
            // Shot 6: High Velocity Steadicam
            shots.add(
                SceneShotEntity(
                    sceneTitle = sceneTitle,
                    shotNumber = 6,
                    shotType = "Medium Close-Up",
                    cameraMotion = "Tracking Steadicam",
                    lensMm = "50mm T/2.0",
                    characterToken = char2,
                    locationToken = locToken,
                    actionDescription = "$char2 lunges sideways towards the fire escape, dodging the line of sight as sparks fly from the concrete barrier behind.",
                    dialogueSpoken = "Hinding-hindi mo ako mahuhuli!",
                    dialogueDelivery = "Desperate shout amidst gunfire/action",
                    cameraFramingIntent = "Speaker Direct (A-Cam)",
                    timeOfDay = timeOfDay,
                    weatherAtmosphere = weatherCond,
                    extrasCrowdDensity = "Zero Extras (Desolate)",
                    environment = envBase,
                    lighting = "Rapid strobe flash reflections from muzzle/neon",
                    audioFoley = "Concrete ricochet crack, rapid combat boot sprint.",
                    eyelineVector = "Screen-Right 30°",
                    keyLightAngle = "45° Camera Right Key",
                    durationSeconds = 4
                )
            )

            // Shot 7: Point-of-View (POV) Pursuit
            shots.add(
                SceneShotEntity(
                    sceneTitle = sceneTitle,
                    shotNumber = 7,
                    shotType = "Point of View",
                    cameraMotion = "Tracking Steadicam",
                    lensMm = "35mm Cinema",
                    characterToken = char1,
                    locationToken = locToken,
                    actionDescription = "$char1's subjective POV sprinting down the rain-slicked alleyway, breath fogging, crosshair sights trailing the fleeing silhouette.",
                    dialogueSpoken = "",
                    dialogueDelivery = "(Heavy adrenaline panting in audio)",
                    cameraFramingIntent = "Point-of-View (POV)",
                    timeOfDay = timeOfDay,
                    weatherAtmosphere = weatherCond,
                    extrasCrowdDensity = "Sparse Distant Silhouettes",
                    environment = envBase,
                    lighting = "Dynamic swinging overhead streetlamps casting shifting shadows",
                    audioFoley = "Heavy rhythmic breathing inside helmet/collar, rushing wind.",
                    eyelineVector = "Center-Left 15°",
                    keyLightAngle = "Direct Frontal Flat",
                    durationSeconds = 5
                )
            )

            // Shot 8: Cliffhanger Freeze / Silhouette Out
            shots.add(
                SceneShotEntity(
                    sceneTitle = sceneTitle,
                    shotNumber = 8,
                    shotType = "Extreme Wide Shot",
                    cameraMotion = "Slow Dolly Out",
                    lensMm = "24mm Wide Anamorphic",
                    characterToken = char1,
                    locationToken = locToken,
                    actionDescription = "High-angle crane pull-back revealing the whole cityscape in the storm. Both figures freeze at the edge of the rooftop ledge as lightning fractures the skyline.",
                    dialogueSpoken = "",
                    dialogueDelivery = "(Cliffhanger freeze beat)",
                    cameraFramingIntent = "Two-Shot (Both in Frame)",
                    timeOfDay = timeOfDay,
                    weatherAtmosphere = weatherCond,
                    extrasCrowdDensity = "Zero Extras (Desolate)",
                    environment = envBase,
                    lighting = "Massive lightning strobe backlit silhouette against dark storm clouds",
                    audioFoley = "Deafening thunder crash reverberating, cutting sharply into silence.",
                    eyelineVector = "Downcast -20°",
                    keyLightAngle = "Backlit / Rim Silhouette",
                    durationSeconds = 6
                )
            )
        }

        val bridge = SceneBridgeEntity(
            sceneTitle = sceneTitle,
            nextSceneTitle = "Scene Next: Escalation / Aftermath",
            outgoingAnchorState = "$char1: Drenched in rain, eyes locked at +15° azimuth. $char2: Wet tailored collar, holding datapad firmly.",
            transitionCutType = "Match Cut (Action / Sound)",
            transitionAudioBridge = "Distant thunder trail fading into next scene opening ambience.",
            timeElapsedDelta = "Continuous (0s - Immediate Next Action)",
            propCarryover = "Encrypted datapad in left hand, damp leather collar, glowing amber wristwatch.",
            incomingLeadIn = "Next scene opens on the direct eyeline reaction with identical lighting and wet textures.",
            dramaticTensionRating = 90
        )

        return DirectedSceneResult(
            sceneTitle = sceneTitle,
            shots = shots,
            bridge = bridge
        )
    }

    private fun generateSceneSlug(premise: String, genre: String): String {
        val lower = premise.lowercase()
        return when {
            lower.contains("rain") || lower.contains("ulan") -> "Rain Confrontation & The Standoff"
            lower.contains("secret") || lower.contains("lihim") || lower.contains("code") -> "The Encrypted Exchange"
            lower.contains("fight") || lower.contains("baril") || lower.contains("away") -> "Rooftop Breach & Ambush"
            lower.contains("love") || lower.contains("iyak") || lower.contains("mahal") -> "Breaking Point & The Ultimatum"
            lower.contains("interrogat") || lower.contains("tanong") -> "High-Pressure Interrogation"
            genre.contains("Cyberpunk") -> "Neon Alley Pursuit"
            genre.contains("Noir") -> "Midnight Harbor Shadowplay"
            else -> "The Critical Encounter"
        }
    }
}
