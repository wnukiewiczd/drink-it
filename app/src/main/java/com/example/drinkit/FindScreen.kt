package com.example.drinkit

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun FindScreen(resetSignal: Int) {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var searching by remember { mutableStateOf(false) }
    var cocktails by remember { mutableStateOf<List<Cocktail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Stan do obsługi szczegółów drinka
    var selectedCocktail by remember { mutableStateOf<Cocktail?>(null) }
    var isDrawerOpen by remember { mutableStateOf(false) }

    // Resetuj stan po zmianie resetSignal
    LaunchedEffect(resetSignal) {
        searching = false
        query = TextFieldValue("")
        cocktails = emptyList()
        errorMessage = null
        focusManager.clearFocus()
    }

    // Funkcja do wyszukiwania drinków
    fun searchCocktails() {
        if (query.text.isNotBlank()) {
            isLoading = true
            errorMessage = null
            searching = true
            keyboardController?.hide()
            focusManager.clearFocus()

            coroutineScope.launch {
                try {
                    val response = ApiClient.api.getCocktailsByName(query.text)
                    cocktails = response.drinks ?: emptyList()
                } catch (e: Exception) {
                    errorMessage = "Error while searching"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // Animacje rozmiaru i położenia
    val boxWidth by animateDpAsState(
        targetValue = if (searching) 320.dp else 280.dp,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing), label = ""
    )
    val boxHeight by animateDpAsState(
        targetValue = if (searching) 56.dp else 80.dp,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing), label = ""
    )
    val boxTopPadding by animateDpAsState(
        targetValue = if (searching) 32.dp else 0.dp,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing), label = ""
    )
    val boxVerticalArrangement = if (searching) Arrangement.Top else Arrangement.Center

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp),
            verticalArrangement = boxVerticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(boxTopPadding))
            Surface(
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .width(boxWidth)
                    .height(boxHeight)
                    .clip(RoundedCornerShape(32.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text(
                                "Find a drink...",
                                color = MaterialTheme.colorScheme.background
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.background
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        IconButton(
                            onClick = { searchCocktails() },
                            enabled = query.text.isNotBlank(),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Wyświetlanie wyników wyszukiwania
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Loading...", color = MaterialTheme.colorScheme.onBackground)
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            } else if (cocktails.isEmpty() && searching) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nothing found",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (searching) {
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
    
    // Szuflada ze szczegółami drinka umieszczona poza głównym układem
    if (selectedCocktail != null) {
        DetailedDrinkDrawer(
            cocktail = selectedCocktail,
            isOpen = isDrawerOpen,
            onClose = { isDrawerOpen = false }
        )
    }
}
