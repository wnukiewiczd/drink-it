package com.example.drinkit

import androidx.room.*

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favourite: FavouriteEntity)

    @Delete
    suspend fun delete(favourite: FavouriteEntity)

    @Query("SELECT * FROM favourites")
    suspend fun getAll(): List<FavouriteEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE idDrink = :idDrink)")
    suspend fun isFavourite(idDrink: String): Boolean
}
