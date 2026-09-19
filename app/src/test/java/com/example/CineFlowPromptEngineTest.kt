package com.example

import com.example.data.local.CharacterEntity
import com.example.data.local.SceneShotEntity
import com.example.data.local.UniverseBibleEntity
import com.example.domain.CineFlowPromptEngine
import org.junit.Assert.assertTrue
import org.junit.Test

class CineFlowPromptEngineTest {

    private val sampleBible = UniverseBibleEntity(
        title = "Neo-Kyoto 2088",
        genre = "Cyberpunk Tech-Noir",
        visualTone = "Bleak, high-contrast cool cyan with warm sodium vapor",
        aspectRatio = "2.39:1 Anamorphic Cinema",
        cameraLensSpec = "Arri Alexa LF, Cooke Anamorphic /i",
        filmStockSimulation = "Kodak Vision3 500T 35mm",
        colorGrade = "Deep emerald blacks, muted skin tones",
        negativeConstraints = "3D render, CGI look, plastic skin"
    )

    private val sampleCharacter = CharacterEntity(
        name = "Rei Tanaka",
        codenameToken = "[REI]",
        role = "Lead Detective",
        referenceImageUris = "",
        hasReferenceAuthority = true,
        physicalAnchor = "28-year-old Japanese woman, angular jawline, obsidian black blunt bob",
        wardrobeAnchor = "Matte charcoal ballistic trench coat",
        signatureAura = "Intense piercing gaze, stoic posture"
    )

    private val sampleShot = SceneShotEntity(
        sceneTitle = "Scene 1: Alleyway Midnight Meeting",
        shotNumber = 1,
        shotType = "Medium Close-Up",
        cameraMotion = "Slow Dolly In",
        lensMm = "65mm Anamorphic",
        characterToken = "[REI]",
        actionDescription = "Turns head slowly toward the doorway with a guarded expression",
        environment = "Rain-streaked dystopian alleyway with neon bokeh",
        lighting = "Amber key light with cool cyan rim light",
        audioFoley = "Heavy rainfall, distant turbine"
    )

    @Test
    fun testOmniFlashSystemPromptContainsAnchorsAndBible() {
        val prompt = CineFlowPromptEngine.buildOmniFlashSystemPrompt(
            sampleBible,
            listOf(sampleCharacter)
        )

        assertTrue(prompt.contains("CINEFLOW OMNI"))
        assertTrue(prompt.contains("Neo-Kyoto 2088"))
        assertTrue(prompt.contains("[REI]"))
        assertTrue(prompt.contains("OFFICIALLY VERIFIED REFERENCE MODEL"))
        assertTrue(prompt.contains("Matte charcoal ballistic trench coat"))
    }

    @Test
    fun testFlowSceneJsonValidStructure() {
        val json = CineFlowPromptEngine.buildFlowSceneJson(
            "Scene 1: Alleyway Midnight Meeting",
            sampleBible,
            listOf(sampleShot),
            listOf(sampleCharacter)
        )

        assertTrue(json.contains("\"project_title\": \"Neo-Kyoto 2088\""))
        assertTrue(json.contains("\"scene_title\": \"Scene 1: Alleyway Midnight Meeting\""))
        assertTrue(json.contains("\"shot_number\": 1"))
        assertTrue(json.contains("\"character_token\": \"[REI]\""))
        assertTrue(json.contains("\"image_generation_prompt\""))
        assertTrue(json.contains("\"video_generation_prompt\""))
    }

    @Test
    fun testImageAndVideoPromptGeneration() {
        val imagePrompt = CineFlowPromptEngine.buildImagePrompt(sampleBible, sampleShot, sampleCharacter)
        assertTrue(imagePrompt.contains("--ar 2.39:1"))
        assertTrue(imagePrompt.contains("[REI]"))
        assertTrue(imagePrompt.contains("Cooke Anamorphic"))

        val videoPrompt = CineFlowPromptEngine.buildVideoMotionPrompt(sampleBible, sampleShot, sampleCharacter)
        assertTrue(videoPrompt.contains("Slow Dolly In"))
        assertTrue(videoPrompt.contains("Rei Tanaka [REI]"))
    }
}
