package com.example.drinkit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Cocktail(
    val idDrink: String,
    val strDrink: String,
    val strDrinkThumb: String
)

data class CocktailResponse(val drinks: List<Cocktail>?)

object ApiClient {
    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    val api: CocktailApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CocktailApi::class.java)
    }
}
