package com.example.drinkit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun ExploreScreen(
    onPrepareTimeChange: (Int) -> Unit,
    onTabSelected: (Int) -> Unit
) {
    val letters = ('A'..'Z').toList()
    val scrollState = rememberScrollState()

    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedCocktail by remember { mutableStateOf<Cocktail?>(null) }
    var isDrawerOpen by remember { mutableStateOf(false) }

    var isAlcoholic by remember { mutableStateOf<Boolean?>(null) }

    var selectedLetter by rememberSaveable { mutableStateOf('A') }

    LaunchedEffect(selectedLetter, isAlcoholic) {
        isLoading = true
        errorMessage = null
        try {
            val response = ApiClient.api.getCocktailsByLetter(selectedLetter.toString())
            var allCocktails = response.drinks ?: emptyList()

            if (isAlcoholic != null) {
                allCocktails = allCocktails.filter { it.isAlcoholic() == isAlcoholic }
            }

            cocktails = allCocktails
        } catch (e: Exception) {
            errorMessage = "Error with downloading drink"
            cocktails = emptyList()
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                letters.forEach { letter ->
                    val isSelected = letter == selectedLetter
                    Text(
                        text = letter.toString(),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { selectedLetter = letter }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(vertical = 4.dp, horizontal = 12.dp),
                        style = TextStyle(
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = isAlcoholic == true,
                    onClick = { isAlcoholic = if (isAlcoholic == true) null else true },
                    label = { Text("Alcoholic") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                FilterChip(
                    selected = isAlcoholic == false,
                    onClick = { isAlcoholic = if (isAlcoholic == false) null else false },
                    label = { Text("Non-alcoholic") },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    cocktails.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nothing found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(cocktails) { cocktail ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.85f)
                                        .clickable {
                                            selectedCocktail = cocktail
                                            isDrawerOpen = true
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surface),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val painter = rememberAsyncImagePainter(
                                            model = cocktail.strDrinkThumb,
                                            error = painterResource(android.R.drawable.ic_menu_report_image),
                                            placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                                        )
                                        val state = painter.state
                                        Image(
                                            painter = painter,
                                            contentDescription = cocktail.strDrink,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        if (state is AsyncImagePainter.State.Error) {
                                            Text(
                                                text = "Image error",
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = cocktail.strDrink,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedCocktail != null) {
        DetailedDrinkDrawer(
            cocktail = selectedCocktail,
            isOpen = isDrawerOpen,
            onClose = { isDrawerOpen = false },
            onPrepareNow = { time ->
                onPrepareTimeChange(time)
                onTabSelected(1)
                isDrawerOpen = false
            }
        )
    }
}
