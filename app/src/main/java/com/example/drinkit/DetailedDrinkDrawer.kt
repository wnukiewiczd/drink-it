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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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
    
    // Zapamiętaj offset dla gestów przeciągnięcia
    var offsetX by remember { mutableStateOf(0f) }
    
    // Wartość progowa do zamknięcia szuflady (25% szerokości ekranu)
    val dismissThreshold = screenWidth * 0.25f

    // Animacja pozycji szuflady
    val position by animateDpAsState(
        targetValue = if (isOpen) 0.dp else screenWidth,
        animationSpec = tween(durationMillis = 300),
        label = "drawerPosition"
    )

    // Efekt który wywołuje onClose gdy dragging przekroczy próg
    LaunchedEffect(offsetX) {
        if (offsetX > dismissThreshold.value) {
            onClose()
            offsetX = 0f
        }
    }

    // Drawer nakładający się na cały ekran, włącznie z paskiem statusu i paskiem nawigacji
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f) // Upewniamy się, że drawer jest na wierzchu
    ) {
        if (isOpen) {
            // Przezroczyste tło pod szufladą
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(11f)
            )
        }
        
        // Właściwa szuflada
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
                            if (dragAmount > 0) {  // Tylko w prawo
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
                    // Pasek górny z przyciskiem zamykania
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
                        }
                    )
                    
                    // Szczegóły drinka
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val painter = rememberAsyncImagePainter(
                            model = cocktail.strDrinkThumb,
                            error = painterResource(android.R.drawable.ic_menu_report_image),
                            placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                        )
                        // Zdjęcie drinka z zaokrąglonymi rogami
                        Image(
                            painter = painter,
                            contentDescription = cocktail.strDrink,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Badge'e z informacjami typu, kategorii i szkła
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Badge dla typu (alkoholowy/niealkoholowy)
                            Badge(
                                icon = Icons.Default.LocalBar,
                                text = cocktail.strAlcoholic ?: "Unknown"
                            )
                            
                            // Badge dla kategorii
                            Badge(
                                icon = Icons.Default.Category,
                                text = cocktail.strCategory ?: "Unknown"
                            )
                            
                            // Badge dla szkła
                            Badge(
                                icon = Icons.Default.WineBar,
                                text = cocktail.strGlass ?: "Unknown"
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Sekcja ze składnikami - teraz z tym samym tłem co badge
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)  // To samo tło co badge
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Ingredients",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                
                                // Lista składników z miarami
                                val ingredients = cocktail.getIngredientsMeasures()
                                ingredients.forEachIndexed { index, (ingredient, measure) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Ingredient name with bullet point
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(
                                                text = ingredient,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                        
                                        // Measure (if available)
                                        if (!measure.isNullOrBlank()) {
                                            Text(
                                                text = measure,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                    
                                    // Add divider except for the last item
                                    if (index < ingredients.size - 1) {
                                        Divider(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Sekcja z instrukcją przygotowania - teraz z tym samym tłem co badge
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)  // To samo tło co badge
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Instructions",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                
                                Text(
                                    text = cocktail.strInstructions ?: "No instructions available.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
