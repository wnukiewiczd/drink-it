package com.example.drinkit

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey val idDrink: String,
    val drinkName: String
)
