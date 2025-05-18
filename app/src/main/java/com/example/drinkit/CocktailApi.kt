//package com.example.drinkit
//
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface CocktailApi {
//    @GET("search.php")
//    suspend fun getCocktailsByLetter(@Query("f") letter: String): CocktailResponse
//}
//
//data class CocktailResponse(val drinks: List<Cocktail>)
//data class Cocktail(val idDrink: String, val strDrink: String, val strDrinkThumb: String)
