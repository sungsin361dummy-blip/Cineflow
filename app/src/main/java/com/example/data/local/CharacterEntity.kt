package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val codenameToken: String,
    val role: String,
    val referenceImageUris: String, // Comma separated or file paths
    val hasReferenceAuthority: Boolean = true,
    val physicalAnchor: String,
    val bodySomatotypeAnchor: String = "", // Stature, height, shoulder-to-hip ratio, posture build
    val craniofacialAnchor: String = "", // Bone landmarks, zygomatic cheekbones, nose bridge, jawline
    val wardrobeAnchor: String,
    val signatureAura: String,
    val voiceAudioProfile: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
