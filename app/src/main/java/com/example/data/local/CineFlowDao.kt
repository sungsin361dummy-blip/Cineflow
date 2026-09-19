package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CineFlowDao {

    // Characters
    @Query("SELECT * FROM characters ORDER BY createdAt ASC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Long): CharacterEntity?

    @Query("SELECT * FROM characters WHERE codenameToken = :token LIMIT 1")
    suspend fun getCharacterByToken(token: String): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacterById(id: Long)

    // Locations / Master Sets
    @Query("SELECT * FROM locations ORDER BY createdAt ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Long): LocationEntity?

    @Query("SELECT * FROM locations WHERE setToken = :token LIMIT 1")
    suspend fun getLocationByToken(token: String): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteLocationById(id: Long)

    // Universe Bible
    @Query("SELECT * FROM universe_bible WHERE id = 1")
    fun getUniverseBible(): Flow<UniverseBibleEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUniverseBible(bible: UniverseBibleEntity)

    // Scene Shots
    @Query("SELECT * FROM scene_shots ORDER BY sceneTitle ASC, shotNumber ASC")
    fun getAllShots(): Flow<List<SceneShotEntity>>

    @Query("SELECT DISTINCT sceneTitle FROM scene_shots ORDER BY sceneTitle ASC")
    fun getAllScenes(): Flow<List<String>>

    @Query("SELECT * FROM scene_shots WHERE sceneTitle = :sceneTitle ORDER BY shotNumber ASC")
    fun getShotsForScene(sceneTitle: String): Flow<List<SceneShotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShot(shot: SceneShotEntity): Long

    @Update
    suspend fun updateShot(shot: SceneShotEntity)

    @Query("DELETE FROM scene_shots WHERE id = :id")
    suspend fun deleteShotById(id: Long)

    @Query("DELETE FROM scene_shots WHERE sceneTitle = :sceneTitle")
    suspend fun deleteScene(sceneTitle: String)

    // Scene Transition Bridges
    @Query("SELECT * FROM scene_bridges WHERE sceneTitle = :sceneTitle LIMIT 1")
    fun getBridgeForScene(sceneTitle: String): Flow<SceneBridgeEntity?>

    @Query("SELECT * FROM scene_bridges")
    fun getAllSceneBridges(): Flow<List<SceneBridgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSceneBridge(bridge: SceneBridgeEntity)

    @Query("DELETE FROM scene_bridges WHERE sceneTitle = :sceneTitle")
    suspend fun deleteSceneBridge(sceneTitle: String)
}
