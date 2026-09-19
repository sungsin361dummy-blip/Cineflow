package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "universe_bible")
data class UniverseBibleEntity(
    @PrimaryKey
    val id: Long = 1,
    val title: String = "Neo-Kyoto 2088",
    val genre: String = "Cyberpunk Tech-Noir",
    val visualTone: String = "Bleak, high-contrast, desaturated cool tones with warm sodium-vapor rim lights",
    val aspectRatio: String = "9:16 Vertical Series (Mobile Cinema)",
    val cameraLensSpec: String = "Arri Alexa LF, Cooke Anamorphic /i Full Frame Plus lenses, T/2.3",
    val filmStockSimulation: String = "Kodak Vision3 500T 35mm grain structure, 24fps motion cadence, photorealistic",
    val colorGrade: String = "Deep emerald blacks, muted skin tones, tungsten accents, subtle chromatic aberration",
    val masterSeed: Long = 849201L,
    val negativeConstraints: String = "3D render, CGI look, plastic skin, waxy texture, airbrushed, cartoon, exaggerated morphing, extra limbs, deformed fingers, flickering, chaotic fast pans, oversaturated colors"
)
