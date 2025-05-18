package com.example.drinkit

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedDrinkDrawer(
    cocktail: Cocktail?,
    isOpen: Boolean,
    onClose: () -> Unit
) {
    if (cocktail == null) return

    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp }
    val density = LocalDensity.current

    var offsetX by remember { mutableStateOf(0f) }
    val dismissThreshold = screenWidth * 0.25f
    val position by animateDpAsState(
        targetValue = if (isOpen) 0.dp else screenWidth,
        animationSpec = tween(durationMillis = 300),
        label = "drawerPosition"
    )
    LaunchedEffect(offsetX) {
        if (offsetX > dismissThreshold.value) {
            onClose()
            offsetX = 0f
        }
    }

    // --- GWIAZDKA: obsługa ulubionych ---
    val context = LocalContext.current
    var isFavourite by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Sprawdź czy drink jest w ulubionych
    LaunchedEffect(cocktail.idDrink) {
        isFavourite = withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            db.favouriteDao().isFavourite(cocktail.idDrink)
        }
    }
    // --- KONIEC GWIAZDKI ---

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f)
    ) {
        if (isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(11f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = position + with(density) { offsetX.toDp() })
                .zIndex(12f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > dismissThreshold.value) {
                                onClose()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = { offsetX = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            if (dragAmount > 0) {
                                offsetX += dragAmount
                            }
                        }
                    )
                }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Pasek górny z przyciskiem zamykania i gwiazdką
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(0.dp)
                            ),
                        navigationIcon = {
                            IconButton(onClick = onClose) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Zamknij szczegóły",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        title = {
                            Text(
                                cocktail.strDrink,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 22.sp
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val db = AppDatabase.getDatabase(context)
                                        if (isFavourite) {
                                            withContext(Dispatchers.IO) {
                                                db.favouriteDao().delete(
                                                    FavouriteEntity(
                                                        idDrink = cocktail.idDrink,
                                                        drinkName = cocktail.strDrink
                                                    )
                                                )
                                            }
                                            isFavourite = false
                                        } else {
                                            withContext(Dispatchers.IO) {
                                                db.favouriteDao().insert(
                                                    FavouriteEntity(
                                                        idDrink = cocktail.idDrink,
                                                        drinkName = cocktail.strDrink
                                                    )
                                                )
                                            }
                                            isFavourite = true
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isFavourite) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = if (isFavourite) "Usuń z ulubionych" else "Dodaj do ulubionych",
                                    tint = if (isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    )

                    // --- Szczegóły drinka ---
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Obrazek drinka
                        Image(
                            painter = rememberAsyncImagePainter(cocktail.strDrinkThumb),
                            contentDescription = cocktail.strDrink,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = cocktail.strDrink,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cocktail.strCategory ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Typ: ${cocktail.strAlcoholic ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Szkło: ${cocktail.strGlass ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Składniki:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        cocktail.getIngredientsMeasures().forEach { (ingredient, measure) ->
                            Text(
                                text = "- $ingredient${if (!measure.isNullOrBlank()) " (${measure})" else ""}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Instrukcje:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cocktail.strInstructions ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
