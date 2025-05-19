package com.example.drinkit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Cocktail(
    val idDrink: String,
    val strDrink: String,
    val strDrinkThumb: String,
    val strCategory: String? = null,
    val strInstructions: String? = null,
    val strAlcoholic: String? = null,
    val strGlass: String? = null,
    val strIngredient1: String? = null,
    val strIngredient2: String? = null,
    val strIngredient3: String? = null,
    val strIngredient4: String? = null,
    val strIngredient5: String? = null,
    val strIngredient6: String? = null,
    val strIngredient7: String? = null,
    val strIngredient8: String? = null,
    val strIngredient9: String? = null,
    val strIngredient10: String? = null,
    val strIngredient11: String? = null,
    val strIngredient12: String? = null,
    val strIngredient13: String? = null,
    val strIngredient14: String? = null,
    val strIngredient15: String? = null,
    val strMeasure1: String? = null,
    val strMeasure2: String? = null,
    val strMeasure3: String? = null,
    val strMeasure4: String? = null,
    val strMeasure5: String? = null,
    val strMeasure6: String? = null,
    val strMeasure7: String? = null,
    val strMeasure8: String? = null,
    val strMeasure9: String? = null,
    val strMeasure10: String? = null,
    val strMeasure11: String? = null,
    val strMeasure12: String? = null,
    val strMeasure13: String? = null,
    val strMeasure14: String? = null,
    val strMeasure15: String? = null
) {
    fun getIngredientsMeasures(): List<Pair<String, String?>> {
        val result = mutableListOf<Pair<String, String?>>()
        
        if (!strIngredient1.isNullOrBlank()) result.add(Pair(strIngredient1, strMeasure1))
        if (!strIngredient2.isNullOrBlank()) result.add(Pair(strIngredient2, strMeasure2))
        if (!strIngredient3.isNullOrBlank()) result.add(Pair(strIngredient3, strMeasure3))
        if (!strIngredient4.isNullOrBlank()) result.add(Pair(strIngredient4, strMeasure4))
        if (!strIngredient5.isNullOrBlank()) result.add(Pair(strIngredient5, strMeasure5))
        if (!strIngredient6.isNullOrBlank()) result.add(Pair(strIngredient6, strMeasure6))
        if (!strIngredient7.isNullOrBlank()) result.add(Pair(strIngredient7, strMeasure7))
        if (!strIngredient8.isNullOrBlank()) result.add(Pair(strIngredient8, strMeasure8))
        if (!strIngredient9.isNullOrBlank()) result.add(Pair(strIngredient9, strMeasure9))
        if (!strIngredient10.isNullOrBlank()) result.add(Pair(strIngredient10, strMeasure10))
        if (!strIngredient11.isNullOrBlank()) result.add(Pair(strIngredient11, strMeasure11))
        if (!strIngredient12.isNullOrBlank()) result.add(Pair(strIngredient12, strMeasure12))
        if (!strIngredient13.isNullOrBlank()) result.add(Pair(strIngredient13, strMeasure13))
        if (!strIngredient14.isNullOrBlank()) result.add(Pair(strIngredient14, strMeasure14))
        if (!strIngredient15.isNullOrBlank()) result.add(Pair(strIngredient15, strMeasure15))
        
        return result
    }
}

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

fun Cocktail.isAlcoholic(): Boolean {
    return this.strAlcoholic?.lowercase() == "alcoholic"
}
