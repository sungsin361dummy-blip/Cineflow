package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scene_shots")
data class SceneShotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sceneTitle: String,
    val shotNumber: Int,
    val shotType: String,
    val cameraMotion: String,
    val lensMm: String,
    val characterToken: String,
    val locationToken: String = "",
    val actionDescription: String,
    val dialogueSpoken: String = "", // Exact dialogue line for lip-sync & script sync
    val dialogueDelivery: String = "", // Lip-sync phoneme anchor & mouth jaw mechanics (e.g. "whispered teeth-clenched, subtle jaw motion")
    val cameraFramingIntent: String = "Speaker Direct (A-Cam)", // Speaker Direct, Reaction Shot (B-Cam), Over-The-Shoulder (OTS Favoring), Two-Shot, Insert/Cutaway, POV
    val timeOfDay: String = "Night / Rain (02:00 AM)", // Time lock across scene cuts
    val weatherAtmosphere: String = "Heavy Rain & Wet Asphalt Specular", // Weather lock across cuts
    val extrasCrowdDensity: String = "Zero Extras (Desolate)", // Zero Extras, Sparse Distant Silhouettes, Busy Bokeh Crowd
    val environment: String,
    val lighting: String,
    val audioFoley: String,
    val eyelineVector: String = "Center-Right 15°",
    val keyLightAngle: String = "45° Camera Left Key",
    val durationSeconds: Int = 8,
    val generatedPrompt: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
