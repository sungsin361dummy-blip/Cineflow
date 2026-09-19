package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scene_bridges")
data class SceneBridgeEntity(
    @PrimaryKey
    val sceneTitle: String, // e.g. "Scene 1: The Hook"
    val nextSceneTitle: String = "", // e.g. "Scene 2: Inciting Incident"
    val outgoingAnchorState: String = "", // e.g. "Kai steps through alleyway threshold drenched in rain, holding encrypted drive in left hand"
    val transitionCutType: String = "Match Cut (Action / Sound)", // Match Cut, Hard Cut, Audio J-Cut Lead, L-Cut Audio Trail, Smash Cut to Black, Whip Pan Drift
    val transitionAudioBridge: String = "Thunderclap on cut triggers sudden silence in next interior", // J-Cut / L-Cut audio foley bridge
    val timeElapsedDelta: String = "Continuous (0s - Immediate Next Action)", // Continuous, 5 Minutes Later, Same Night (+2 Hours), Next Morning
    val propCarryover: String = "Encrypted drive in left hand, soaked leather collar", // Props & injuries that must be preserved
    val incomingLeadIn: String = "Shot 1 begins with damp leather collar dripping onto warehouse desk", // Initial hook of next scene
    val dramaticTensionRating: Int = 85, // 0-100% cliffhanger/hook intensity
    val updatedAt: Long = System.currentTimeMillis()
)
