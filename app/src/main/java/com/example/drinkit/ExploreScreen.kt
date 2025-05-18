package com.example.drinkit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

data class Cocktail(
    val name: String,
    val imageUrl: String
)

private val sampleCocktails = listOf(
    Cocktail("Mojito", "https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg"),
    Cocktail("Margarita", "https://www.thecocktaildb.com/images/media/drink/5noda61589575158.jpg"),
    Cocktail("Cosmopolitan", "https://www.thecocktaildb.com/images/media/drink/kpsajh1504368362.jpg"),
    Cocktail("Daiquiri", "https://www.thecocktaildb.com/images/media/drink/mrz9091589574515.jpg"),
    Cocktail("Old Fashioned", "https://www.thecocktaildb.com/images/media/drink/vrwquq1478252802.jpg"),
    Cocktail("Negroni", "https://www.thecocktaildb.com/images/media/drink/qgdu971561574065.jpg"),
    Cocktail("Whiskey Sour", "https://www.thecocktaildb.com/images/media/drink/hbkfsh1589574990.jpg"),
    Cocktail("Pina Colada", "https://www.thecocktaildb.com/images/media/drink/cpf4j51504371346.jpg"),
    Cocktail("Mai Tai", "https://www.thecocktaildb.com/images/media/drink/twyrrp1439907470.jpg"),
    Cocktail("Long Island Iced Tea", "https://www.thecocktaildb.com/images/media/drink/wx7hsg1504370510.jpg")
)

@Composable
fun ExploreScreen(
    selectedLetter: Char,
    onLetterSelected: (Char) -> Unit
) {
    val letters = ('A'..'Z').toList()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Pasek liter na górze - tło primary, wyśrodkowany, zaokrąglony
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    letters.forEach { letter ->
                        val isSelected = letter == selectedLetter
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .size(56.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable { onLetterSelected(letter) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.toString(),
                                fontSize = 32.sp,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onBackground
                                else
                                    MaterialTheme.colorScheme.onPrimary,
                                fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        // ...reszta ekranu...
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleCocktails) { cocktail ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val painter = rememberAsyncImagePainter(
                            model = cocktail.imageUrl,
                            error = painterResource(android.R.drawable.ic_menu_report_image),
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                        )
                        val state = painter.state
                        Image(
                            painter = painter,
                            contentDescription = cocktail.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentScale = ContentScale.Crop
                        )
                        if (state is AsyncImagePainter.State.Error) {
                            Text(
                                text = "Błąd obrazka",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cocktail.name,
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
