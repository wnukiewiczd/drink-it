package com.example.drinkit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FindScreenViewModel(
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {
    var query by mutableStateOf(TextFieldValue(savedStateHandle.get<String>("query") ?: ""))
    var searching by mutableStateOf(savedStateHandle.get<Boolean>("searching") ?: false)
    var cocktails by mutableStateOf<List<Cocktail>>(savedStateHandle.get("cocktails") ?: emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    
    var selectedCocktail by mutableStateOf<Cocktail?>(null)
    var isDrawerOpen by mutableStateOf(false)
    
    // Przetrzymujemy ostatnią wartość resetSignal, aby nie resetować przy obrotach ekranu
    private var lastResetSignal: Int = -1
    
    init {
        // Odtwarzamy stan wyszukiwania jeśli były wcześniej wyniki
        if (searching && query.text.isNotBlank() && cocktails.isEmpty()) {
            searchCocktails()
        }
    }

    fun handleResetSignal(resetSignal: Int) {
        if (resetSignal != lastResetSignal && resetSignal != 0) {
            lastResetSignal = resetSignal
            resetSearch()
        }
    }
    
    fun resetSearch() {
        searching = false
        query = TextFieldValue("")
        cocktails = emptyList()
        errorMessage = null
        saveState()
    }
    
    fun searchCocktails() {
        if (query.text.isNotBlank()) {
            isLoading = true
            errorMessage = null
            searching = true
            saveState()
            
            viewModelScope.launch {
                try {
                    val response = ApiClient.api.getCocktailsByName(query.text)
                    cocktails = response.drinks ?: emptyList()
                    saveState()
                } catch (e: Exception) {
                    errorMessage = "Error while searching"
                } finally {
                    isLoading = false
                }
            }
        }
    }
    
    fun openCocktailDetails(cocktail: Cocktail) {
        selectedCocktail = cocktail
        isDrawerOpen = true
    }
    
    fun closeCocktailDetails() {
        isDrawerOpen = false
    }
    
    private fun saveState() {
        savedStateHandle["query"] = query.text
        savedStateHandle["searching"] = searching
        savedStateHandle["cocktails"] = cocktails
    }
}
