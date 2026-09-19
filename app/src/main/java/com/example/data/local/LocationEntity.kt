package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val setToken: String, // e.g. [SET_RAIN_ALLEYWAY]
    val environmentType: String, // Interior, Exterior, Urban, Sci-Fi Lab, etc.
    val referenceImageUris: String = "", // Comma-separated or local image paths
    val hasReferenceAuthority: Boolean = true,
    val architecturalAnchor: String, // Permanent architecture, walls, spatial boundaries
    val lightingAtmosphereAnchor: String, // Ambient, volumetric, key lighting, color temp
    val propsAndFoleyAnchor: String = "", // Specific props, ambient audio/foley
    val timeOfDay: String = "Night / Rain",
    val createdAt: Long = System.currentTimeMillis()
)
