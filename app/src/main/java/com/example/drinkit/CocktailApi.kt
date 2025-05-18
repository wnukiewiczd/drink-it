package com.example.drinkit

import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApi {
    @GET("search.php")
    suspend fun getCocktailsByLetter(@Query("f") letter: String): CocktailResponse

    @GET("search.php")
    suspend fun getCocktailsByName(@Query("s") name: String): CocktailResponse
}
