package com.example.domain

import com.example.data.local.CharacterEntity
import com.example.data.local.LocationEntity
import com.example.data.local.SceneShotEntity
import com.example.data.local.UniverseBibleEntity

object CineFlowPromptEngine {

    /**
     * Builds an individual Image generation prompt (Imagen 3 / Midjourney)
     * incorporating both Character Continuity Anchors and Location Set Reference Anchors
     */
    fun buildImagePrompt(
        bible: UniverseBibleEntity,
        shot: SceneShotEntity,
        character: CharacterEntity?,
        location: LocationEntity? = null
    ): String {
        val characterBlock = if (character != null) {
            val bodyPart = if (character.bodySomatotypeAnchor.isNotBlank()) "Body Somatotype & Frame: ${character.bodySomatotypeAnchor}. " else ""
            val facePart = if (character.craniofacialAnchor.isNotBlank()) "Craniofacial Bone Structure: ${character.craniofacialAnchor}. " else ""
            "${character.codenameToken} ($bodyPart$facePart${character.physicalAnchor}, wearing ${character.wardrobeAnchor}, expression: ${character.signatureAura})"
        } else {
            shot.characterToken
        }

        val environmentBlock = if (location != null) {
            "Location Master Set ${location.setToken} [${location.name}]: ${location.architecturalAnchor}, time/atmosphere: ${location.timeOfDay}. ${shot.environment}"
        } else {
            shot.environment
        }

        val lightingBlock = if (location != null) {
            "${location.lightingAtmosphereAnchor}, ${shot.lighting}"
        } else {
            shot.lighting
        }

        val is916 = bible.aspectRatio.contains("9:16")
        val arParam = when {
            is916 -> "--ar 9:16"
            bible.aspectRatio.contains("2.39") -> "--ar 2.39:1"
            bible.aspectRatio.contains("16:9") -> "--ar 16:9"
            else -> "--ar 9:16"
        }

        val verticalDirective = if (is916) {
            "Vertical 9:16 mobile cinema framing, centered eyeline with top-third headroom, bottom-third UI caption safe zone, "
        } else ""

        val locationRefTag = if (location != null && location.referenceImageUris.isNotBlank()) {
            "[Location Visual Reference Locked: ${location.setToken}] "
        } else ""

        val driftDirectives = if (character != null || location != null) {
            "STRICT DRIFT LOCK: Retain exact facial bone geometry, ocular spacing, asymmetric scars/features on specific lateral sides, invariant hairstyle silhouette, and identical garment fabric weave, collar seams, zipper materials, and base textile pigment across all camera angles. Incident light alters surface specular reflection only, never base textile dye. Zero morphological drift. Zero hallucinated environment alterations. "
        } else ""

        val continuityVectorBlock = if (shot.eyelineVector.isNotBlank() || shot.keyLightAngle.isNotBlank()) {
            "Continuous Eyeline: ${shot.eyelineVector}, Key Light Vector: ${shot.keyLightAngle}. "
        } else ""

        val framingIntentTag = "Camera Framing Intent: ${shot.cameraFramingIntent}. "
        val timeWeatherTag = "Temporal & Weather State: ${shot.timeOfDay}, ${shot.weatherAtmosphere}. "
        val extrasTag = "Extras & Background: ${shot.extrasCrowdDensity}. "

        val seedParam = "--seed ${bible.masterSeed}"

        return "${shot.shotType}, ${shot.cameraMotion}, $framingIntentTag$verticalDirective$locationRefTag" +
                "featuring $characterBlock performing: ${shot.actionDescription}. " +
                "$continuityVectorBlock" +
                "$timeWeatherTag$extrasTag" +
                "Environment: $environmentBlock. " +
                "Lighting & Atmosphere: $lightingBlock, ${bible.visualTone}. " +
                "Camera & Sensor: Shot on ${bible.cameraLensSpec}, ${shot.lensMm}, ${bible.filmStockSimulation}, ${bible.colorGrade}. " +
                "$driftDirectives" +
                "Cinematic photorealistic movie still, 8k resolution $arParam $seedParam --style raw"
    }

    /**
     * Builds temporal video prompt for Kling / Runway Gen-3 / Sora with 8s max duration
     * and Location Environmental Continuity
     */
    fun buildVideoMotionPrompt(
        bible: UniverseBibleEntity,
        shot: SceneShotEntity,
        character: CharacterEntity?,
        location: LocationEntity? = null
    ): String {
        val charName = character?.let { "${it.name} ${it.codenameToken}" } ?: shot.characterToken
        val duration = shot.durationSeconds.coerceIn(1, 8)
        val is916 = bible.aspectRatio.contains("9:16")
        val formatTag = if (is916) "Format: 9:16 vertical cinema frame (1080x1920 mobile). " else "Format: ${bible.aspectRatio}. "

        val setAnchorTag = if (location != null) {
            "Set Anchor: ${location.setToken} (${location.name}), Spatial Geometry: ${location.architecturalAnchor}. "
        } else ""

        val ambientLighting = if (location != null) {
            "${location.lightingAtmosphereAnchor}, ${shot.lighting}"
        } else {
            shot.lighting
        }

        val continuityVectorBlock = if (shot.eyelineVector.isNotBlank() || shot.keyLightAngle.isNotBlank()) {
            "Continuous Eyeline: ${shot.eyelineVector}, Key Light Vector: ${shot.keyLightAngle}. "
        } else ""

        val framingIntentTag = "Camera Framing Intent: ${shot.cameraFramingIntent}. "
        val timeWeatherTag = "Temporal & Weather State: ${shot.timeOfDay}, ${shot.weatherAtmosphere}. "
        val extrasTag = "Extras & Background: ${shot.extrasCrowdDensity}. "

        val speechBlock = if (shot.dialogueSpoken.isNotBlank() || shot.dialogueDelivery.isNotBlank()) {
            val speechDelivery = if (shot.dialogueDelivery.isNotBlank()) "Lip-Sync & Phoneme Mechanics: ${shot.dialogueDelivery}. " else ""
            val speechLine = if (shot.dialogueSpoken.isNotBlank()) "Dialogue Line: \"${shot.dialogueSpoken}\". " else ""
            "$speechDelivery$speechLine"
        } else ""

        val motionDriftLock = "Zero facial morphing, preserve wardrobe garment silhouette, collar architecture, button/zipper configuration, and exact facial proportions during camera movement. Hair length and parting remain fixed. "

        return "Cinematic camera: ${shot.cameraMotion} with ${shot.lensMm} lens at 24fps. " +
                "$framingIntentTag" +
                "$formatTag" +
                "Duration: ${duration}s (8s max clip limit). " +
                "Subject: $charName. " +
                "$setAnchorTag" +
                "Micro-Action: ${shot.actionDescription}. " +
                "$speechBlock" +
                "$continuityVectorBlock" +
                "$timeWeatherTag$extrasTag" +
                "Atmosphere & Physics: $ambientLighting, natural volumetric haze, fluid particle movement, authentic lens optical compression. " +
                "$motionDriftLock" +
                "Seed: ${bible.masterSeed}. Steady motion, hyper-realistic, high aesthetic vertical film quality."
    }

    /**
     * Builds the Google Gemini Omni Flash System Prompt tailored for Google Flow
     */
    fun buildOmniFlashSystemPrompt(
        bible: UniverseBibleEntity,
        characters: List<CharacterEntity>,
        locations: List<LocationEntity> = emptyList()
    ): String {
        val charactersText = characters.joinToString("\n\n") { char ->
            """
            • ${char.codenameToken} (${char.name} - ${char.role})
              - Authority Status: ${if (char.hasReferenceAuthority) "OFFICIALLY VERIFIED REFERENCE MODEL" else "UNVERIFIED"}
              - Body Somatotype & Height Frame: ${if (char.bodySomatotypeAnchor.isNotBlank()) char.bodySomatotypeAnchor else "Standard cinematic frame"}
              - Craniofacial Architecture & Features: ${if (char.craniofacialAnchor.isNotBlank()) char.craniofacialAnchor else "Locked bone landmarks"}
              - Physical & Hair Continuity: ${char.physicalAnchor}
              - Wardrobe Continuity Anchor: ${char.wardrobeAnchor}
              - Signature Aura & Demeanor: ${char.signatureAura}
              - Vocal/Audio Cadence: ${char.voiceAudioProfile}
            """.trimIndent()
        }

        val locationsText = if (locations.isNotEmpty()) {
            locations.joinToString("\n\n") { loc ->
                """
                • ${loc.setToken} ("${loc.name}" - ${loc.environmentType})
                  - Authority Status: ${if (loc.hasReferenceAuthority) "MASTER SET REFERENCE LOCKED" else "UNVERIFIED"}
                  - Architectural Anchor: ${loc.architecturalAnchor}
                  - Lighting & Atmosphere: ${loc.lightingAtmosphereAnchor} (Time: ${loc.timeOfDay})
                  - Props & Foley: ${loc.propsAndFoleyAnchor}
                """.trimIndent()
            }
        } else {
            "None defined yet."
        }

        return """
YOU ARE CINEFLOW OMNI, AN ELITE CINEMATIC DIRECTOR & SHOWRUNNER AI ENGINE.
Optimized for Google Gemini Omni Flash within Google AI Studio / Vertex Flow pipelines.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. UNIVERSE VISUAL BIBLE (GLOBAL CINEMATIC RULES)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
- Series Project Title: ${bible.title}
- Genre: ${bible.genre}
- Visual Tone: ${bible.visualTone}
- Aspect Ratio: ${bible.aspectRatio} (Default 9:16 Vertical Mobile Cinema)
- Camera & Optics: ${bible.cameraLensSpec}
- Film Stock & Grain: ${bible.filmStockSimulation}
- Color Grade: ${bible.colorGrade}
- Fixed Universe Master Seed: ${bible.masterSeed} (Universal cross-shot seed lock)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
2. LOCKED CHARACTER CONTINUITY ANCHORS (MANDATORY INJECTION)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
$charactersText

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
3. LOCKED MASTER SET & LOCATION REFERENCES (ENVIRONMENT ANCHORS)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
$locationsText

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
4. PRODUCTION GUARDRAILS & OPTICAL TAXONOMY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
- ATOMIC TEMPORAL ACTIONS: In video motion prompts, dictate exactly ONE primary micro-action and ONE deliberate camera trajectory per shot.
- OPTICAL ACCURACY: Every shot must specify focal length (mm), aperture, and camera movement velocity.
- CONTINUOUS EYELINE & KEY LIGHT VECTOR: Preserve 180-degree rule across consecutive shots. Track subject eyeline trajectory (e.g., Screen-Left vs. Screen-Right) and key light source direction so facial shadows match perfectly across cuts.
- CONTINUITY PRESERVATION: Always preserve character facial bone structure, wardrobe fabrics, and distinctive scars across every cut.
- SET CONTINUITY: Lock background architectural boundaries, window placements, light sources, and props to prevent background hallucination.
- WARDROBE FABRIC & PIGMENT LOCK: Every character's wardrobe must specify base fabric materials (e.g. heavyweight canvas, raw denim, matte leather) and base dye hex values. Incident coloured lights must only generate specular reflections and ambient tint, NEVER altering the underlying textile dye. Fasteners, lapel width, collar architecture, and zippers remain invariant across all cuts.
- ASYMMETRIC FACIAL LOCK: Unique facial marks, scars, eye implants, or blemishes MUST be bound to a specific lateral side (e.g., strictly LEFT cheek, strictly RIGHT eye) and must NEVER mirror or jump sides between shots.
- HAIR ARCHITECTURE INVARIANCE: Hairstyle length, volume, hairline part, and texture must stay rigidly identical. Zero spontaneous hair growth or changes in tied hairstyles.
- STRICT ZERO-DRIFT LOCK: Retain exact facial bone geometry, ocular spacing, asymmetric scars/features on specific lateral sides, invariant hairstyle silhouette, and identical garment fabric weave, collar seams, zipper materials, and base textile pigment across all camera angles. Incident light alters surface specular reflection only, never base textile dye. Zero morphological drift. Zero hallucinated environment alterations. Seed lock enforced: ${bible.masterSeed}.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
5. EPISODIC & TEMPORAL ARCHITECTURE (9 SCENES / EPISODE, 8S MAX)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
- EPISODIC PACING: Every episode is structured into exactly 9 scenes (Scene 1 to Scene 9) for high-retention micro-drama / vertical cinema pacing:
  1. The Hook (0-8s opening impact)
  2. Inciting Incident (disruption of status quo)
  3. Escalation & Conflict
  4. Secret Revealed / Plot Turn
  5. Midpoint Climax / Pressure Peak
  6. Complication & Retaliation
  7. High-Stakes Standoff
  8. The Twist / Breaking Point
  9. Cliffhanger (Episode End Hook)
- TEMPORAL CEILING: Exactly 8s max duration per shot (1s-8s clip duration window for video generation models Kling, Runway Gen-3, Sora, and Hailuo).
- ASPECT RATIO (9:16): Full vertical mobile framing (1080x1920), vertical composition, zero horizontal pillarbox dead space.
        """.trimIndent()
    }

    /**
     * Builds the Google Flow Structured JSON output for an entire Scene
     */
    fun buildFlowSceneJson(
        sceneTitle: String,
        bible: UniverseBibleEntity,
        shots: List<SceneShotEntity>,
        characters: List<CharacterEntity>,
        locations: List<LocationEntity> = emptyList(),
        bridge: com.example.data.local.SceneBridgeEntity? = null
    ): String {
        val charMap = characters.associateBy { it.codenameToken }
        val locMap = locations.associateBy { it.setToken }

        val shotsJsonArray = shots.joinToString(",\n") { shot ->
            val char = charMap[shot.characterToken]
            val loc = locMap[shot.locationToken]
            val imagePrompt = buildImagePrompt(bible, shot, char, loc).replace("\"", "\\\"")
            val videoPrompt = buildVideoMotionPrompt(bible, shot, char, loc).replace("\"", "\\\"")
            val action = shot.actionDescription.replace("\"", "\\\"")
            val dialogue = shot.dialogueSpoken.replace("\"", "\\\"")
            val delivery = shot.dialogueDelivery.replace("\"", "\\\"")
            val env = shot.environment.replace("\"", "\\\"")
            val lighting = shot.lighting.replace("\"", "\\\"")
            val foley = shot.audioFoley.replace("\"", "\\\"")
            val duration = shot.durationSeconds.coerceIn(1, 8)
            val locationRefUri = loc?.referenceImageUris?.split(",")?.firstOrNull() ?: ""

            """
    {
      "shot_number": ${shot.shotNumber},
      "shot_type": "${shot.shotType}",
      "camera_framing_intent": "${shot.cameraFramingIntent}",
      "duration_seconds": $duration,
      "max_clip_ceiling": "8s max",
      "aspect_ratio": "9:16",
      "camera_motion": "${shot.cameraMotion}",
      "focal_length": "${shot.lensMm}",
      "character_token": "${shot.characterToken}",
      "location_token": "${shot.locationToken}",
      "location_reference_uri": "$locationRefUri",
      "eyeline_vector": "${shot.eyelineVector}",
      "key_light_vector": "${shot.keyLightAngle}",
      "time_of_day": "${shot.timeOfDay}",
      "weather_atmosphere": "${shot.weatherAtmosphere}",
      "extras_crowd_density": "${shot.extrasCrowdDensity}",
      "seed": ${bible.masterSeed},
      "micro_action": "$action",
      "dialogue_spoken": "$dialogue",
      "dialogue_lip_sync_mechanics": "$delivery",
      "environment": "$env",
      "lighting_and_grade": "$lighting",
      "audio_foley": "$foley",
      "image_generation_prompt": "$imagePrompt",
      "video_generation_prompt": "$videoPrompt"
    }""".trimIndent()
        }

        val bridgeJson = if (bridge != null) {
            """
  "scene_continuity_bridge": {
    "next_scene_target": "${bridge.nextSceneTitle.replace("\"", "\\\"")}",
    "outgoing_anchor_state": "${bridge.outgoingAnchorState.replace("\"", "\\\"")}",
    "transition_cut_type": "${bridge.transitionCutType}",
    "audio_j_cut_bridge": "${bridge.transitionAudioBridge.replace("\"", "\\\"")}",
    "time_elapsed_delta": "${bridge.timeElapsedDelta}",
    "prop_and_injury_carryover": "${bridge.propCarryover.replace("\"", "\\\"")}",
    "incoming_lead_in": "${bridge.incomingLeadIn.replace("\"", "\\\"")}",
    "dramatic_hook_intensity": "${bridge.dramaticTensionRating}%"
  },"""
        } else ""

        return """
{
  "project_title": "${bible.title}",
  "genre": "${bible.genre}",
  "scene_title": "$sceneTitle",
  "aspect_ratio": "${if (bible.aspectRatio.contains("9:16")) "9:16" else bible.aspectRatio}",
  "camera_rig": "${bible.cameraLensSpec}",
  "master_seed": ${bible.masterSeed},
  "global_continuity_lock": "Zero morphological character drift; invariant master set geometry across all cuts",
  "episodic_pacing": "9 scenes per episode",
  "max_shot_duration_seconds": 8,
  "total_shots": ${shots.size},$bridgeJson
  "shots": [
$shotsJsonArray
  ]
}
        """.trimIndent()
    }

    /**
     * Builds a dedicated Zero-Drift Continuity Enforcement Manifest
     * for prompt chaining and system directives across video & image models
     */
    fun buildAntiDriftManifest(
        bible: UniverseBibleEntity,
        characters: List<CharacterEntity>,
        locations: List<LocationEntity>,
        shots: List<SceneShotEntity>
    ): String {
        val characterRules = characters.joinToString("\n\n") { char ->
            val refCount = if (char.referenceImageUris.isBlank()) 0 else char.referenceImageUris.split(",").size
            """
            [TOKEN]: ${char.codenameToken} (${char.name})
            • Reference Images Loaded: $refCount reference photo(s)
            • Locked Somatotype & Build: ${if (char.bodySomatotypeAnchor.isNotBlank()) char.bodySomatotypeAnchor else "Standard frame"}
            • Locked Craniofacial Bones: ${if (char.craniofacialAnchor.isNotBlank()) char.craniofacialAnchor else "Fixed bone landmarks"}
            • Locked Physical & Hair: ${char.physicalAnchor}
            • Locked Wardrobe: ${char.wardrobeAnchor}
            • Invariant Body Morpology: Strictly forbid spontaneous muscle bulk changes, height compression, or torso distortion between Wide and Close-Up shots.
            • Invariant Facial Structure: Preserve exact cheekbone height, nose ridge, lateral scar positioning (never mirror), and skin micro-texture.
            • Locked Hair Silhouette: Invariant hairstyle, volume, parting, and color across all angles.
            • Wardrobe Fabric Lock: Invariant textile weave, collar geometry, closures (zippers/buttons), and base fabric pigment. Incident lighting affects specular sheen only.
            • Anti-Drift Rule: FORBIDDEN to alter body build, hair length/color, clothing style/fabric, asymmetric scars, or facial bone structure across scene cuts.
            """.trimIndent()
        }

        val locationRules = locations.joinToString("\n\n") { loc ->
            val refCount = if (loc.referenceImageUris.isBlank()) 0 else loc.referenceImageUris.split(",").size
            """
            [SET TOKEN]: ${loc.setToken} ("${loc.name}")
            • Reference Images Loaded: $refCount reference photo(s)
            • Locked Spatial Bounds: ${loc.architecturalAnchor}
            • Locked Lighting Matrix: ${loc.lightingAtmosphereAnchor}
            • Anti-Drift Rule: FORBIDDEN to alter wall textures, light source angles, window positions, or spatial volume.
            """.trimIndent()
        }

        return """
================================================================================
CINEFLOW ZERO-DRIFT CONTINUITY MANIFEST & ENFORCEMENT PROTOCOL
Project: ${bible.title} | Genre: ${bible.genre} | Aspect Ratio: ${bible.aspectRatio}
================================================================================

[CRITICAL DIRECTIVE FOR MULTIMODAL GENERATORS (IMAGEN 3, VEO, RUNWAY, KLING)]
Any cross-frame variation of locked character likeness or location architecture
is classified as a FATAL CONTINUITY ERROR. Strict seed conditioning and reference
injection are mandated below.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. CHARACTER IDENTITY LOCKS (Zero Facial / Wardrobe Drift)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
$characterRules

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
2. MASTER SET & ENVIRONMENT GEOMETRY LOCKS (Zero Spatial Drift)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
$locationRules

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
3. OPTICAL & FILM STOCK CONSTANTS (Zero Color & Lens Drift)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
• Camera Package: ${bible.cameraLensSpec} (Uniform focal compression across all shots)
• Film Emulation & Grain: ${bible.filmStockSimulation} (Constant grain structure)
• Color Grade Palette: ${bible.colorGrade} (Uniform LUT & color science)
• Pacing & Temporal Law: 9-scene episodic structure, max 8 seconds per shot.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
4. NEGATIVE DRIFT EXCLUSION TOKENS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
${bible.negativeConstraints}, face morphing, altering eye color, changing wardrobe mid-scene, changing room dimensions, shifting window positions, inconsistent lighting temperature, cinematic hallucinations.
================================================================================
        """.trimIndent()
    }

    /**
     * Builds a single Character Continuity Bible Card for sharing
     */
    fun buildCharacterCard(character: CharacterEntity): String {
        return """
=== CHARACTER CONTINUITY ANCHOR: ${character.codenameToken} ===
Name: ${character.name}
Role: ${character.role}
Reference Authority: ${if (character.hasReferenceAuthority) "VERIFIED" else "PENDING"}

[PHYSICAL ANCHOR]:
${character.physicalAnchor}

[WARDROBE & COSTUME ANCHOR]:
${character.wardrobeAnchor}

[SIGNATURE AURA & ACTING]:
${character.signatureAura}

[AUDIO / VOICE PROFILE]:
${character.voiceAudioProfile}
        """.trimIndent()
    }

    /**
     * Builds a single Location Continuity Bible Card for sharing
     */
    fun buildLocationCard(location: LocationEntity): String {
        return """
=== MASTER SET CONTINUITY ANCHOR: ${location.setToken} ===
Name: ${location.name}
Type: ${location.environmentType}
Time / Atmosphere: ${location.timeOfDay}
Reference Authority: ${if (location.hasReferenceAuthority) "VERIFIED" else "PENDING"}

[ARCHITECTURAL ANCHOR]:
${location.architecturalAnchor}

[LIGHTING & ATMOSPHERE ANCHOR]:
${location.lightingAtmosphereAnchor}

[PROPS & FOLEY ANCHOR]:
${location.propsAndFoleyAnchor}
        """.trimIndent()
    }

    /**
     * Builds a Scene Outgoing ➔ Next Scene Bridge Continuity Manifest
     */
    fun buildSceneBridgeCard(bridge: com.example.data.local.SceneBridgeEntity): String {
        return """
=== SCENE CONTINUITY BRIDGE: ${bridge.sceneTitle} ➔ ${bridge.nextSceneTitle.ifBlank { "Next Scene" }} ===
Transition Cut Type: ${bridge.transitionCutType}
Time Elapsed Delta: ${bridge.timeElapsedDelta}
Dramatic Hook Intensity: ${bridge.dramaticTensionRating}%

[OUTGOING ENDING STATE (Final Shot Anchor)]:
${bridge.outgoingAnchorState}

[AUDIO / J-CUT FOLEY BRIDGE]:
${bridge.transitionAudioBridge}

[PROP & INJURY CARRYOVER (Zero-Drift Law)]:
${bridge.propCarryover}

[INCOMING NEXT SCENE LEAD-IN (Shot 1 Anchor)]:
${bridge.incomingLeadIn}
        """.trimIndent()
    }

    /**
     * Generates a 4-view character turnaround model sheet prompt
     * (Front, 3/4 Hero Profile, Lateral Profile, Back View) for --cref training
     */
    fun buildCharacterModelSheetPrompt(
        bible: UniverseBibleEntity,
        character: CharacterEntity
    ): String {
        val bodyPart = if (character.bodySomatotypeAnchor.isNotBlank()) "Stature & Somatotype: ${character.bodySomatotypeAnchor}. " else ""
        val facePart = if (character.craniofacialAnchor.isNotBlank()) "Craniofacial Bone Structure: ${character.craniofacialAnchor}. " else ""
        return "Master character turnaround reference model sheet, 4-angle sequential line-up on neutral studio grey background: [1. Front full-body view, 2. Three-quarter hero angle, 3. Strict 90-degree lateral profile, 4. Back view]. " +
                "Subject: ${character.codenameToken} (${character.name} - ${character.role}). " +
                "$bodyPart$facePart" +
                "Physical & Hair: ${character.physicalAnchor}. " +
                "Signature Invariant Wardrobe: ${character.wardrobeAnchor}. " +
                "Facial Expression: Neutral resting expression, eyes forward at camera, mouth relaxed. " +
                "Lighting: Flat neutral white diffuse studio key lighting, zero cast shadows, even exposure across all 4 angles. " +
                "Zero morphological drift, identical height, identical clothing seams and zippers across all 4 views. " +
                "Ultra-sharp character turnaround sheet, hyper-detailed textures, 8k resolution, shot on 85mm prime lens --ar 16:9 --seed ${bible.masterSeed} --style raw"
    }

    /**
     * Evaluates continuity completeness (0 to 100%) for a given shot
     */
    fun evaluateShotContinuityHealth(
        shot: SceneShotEntity,
        character: CharacterEntity?,
        location: LocationEntity?
    ): Pair<Int, List<String>> {
        var score = 0
        val warnings = mutableListOf<String>()

        // 1. Character Check (30 pts)
        if (character != null) {
            score += 10
            if (character.referenceImageUris.isNotBlank()) {
                score += 10
            } else {
                warnings.add("No character reference photos loaded for ${character.codenameToken}")
            }
            if (character.bodySomatotypeAnchor.isNotBlank() && character.craniofacialAnchor.isNotBlank()) {
                score += 10
            } else {
                warnings.add("Incomplete body somatotype or craniofacial bone anchors")
            }
        } else {
            warnings.add("No linked character model anchor")
        }

        // 2. Set / Location Check (25 pts)
        if (location != null) {
            score += 15
            if (location.referenceImageUris.isNotBlank()) {
                score += 10
            } else {
                warnings.add("No set reference photo for ${location.setToken}")
            }
        } else if (shot.locationToken.isNotBlank()) {
            score += 10
        } else {
            warnings.add("No master set bound to shot")
        }

        // 3. Continuity Vectors & Environment Locking (25 pts)
        if (shot.eyelineVector.isNotBlank()) score += 7 else warnings.add("Missing eyeline trajectory vector")
        if (shot.keyLightAngle.isNotBlank()) score += 6 else warnings.add("Missing key light source angle")
        if (shot.timeOfDay.isNotBlank() && shot.weatherAtmosphere.isNotBlank()) score += 7 else warnings.add("Missing locked time of day or weather state")
        if (shot.cameraFramingIntent.isNotBlank()) score += 5 else warnings.add("Undefined camera framing intent")

        // 4. Lip-Sync & Audio (20 pts)
        if (shot.dialogueSpoken.isBlank() || (shot.dialogueSpoken.isNotBlank() && shot.dialogueDelivery.isNotBlank())) {
            score += 20
        } else {
            score += 10
            warnings.add("Dialogue present without lip-sync phoneme delivery guidance")
        }

        return Pair(score.coerceIn(0, 100), warnings)
    }
}
