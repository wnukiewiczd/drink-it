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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
                    // Pasek górny z przyciskiem zamykania - zmieniony na background
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
                        // Dodane zaokrąglenie do obrazu za pomocą clip i RoundedCornerShape
                        Image(
                            painter = painter,
                            contentDescription = cocktail.strDrink,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)), // Dodane zaokrąglenie 16dp
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        // Usunięta nazwa drinka - pozostaje tylko w pasku górnym
                        
                        Text(
                            text = cocktail.strCategory ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = cocktail.strInstructions ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
