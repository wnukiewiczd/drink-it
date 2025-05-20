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
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
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
    onClose: () -> Unit,
    onPrepareNow: (Int) -> Unit
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
        if (kotlin.math.abs(offsetX) > dismissThreshold.value) {
            onClose()
            offsetX = 0f
        }
    }

    val context = LocalContext.current
    var isFavourite by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(cocktail.idDrink) {
        isFavourite = withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            db.favouriteDao().isFavourite(cocktail.idDrink)
        }
    }

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
                            if (kotlin.math.abs(offsetX) > dismissThreshold.value) {
                                onClose()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = { offsetX = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX += dragAmount
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
                                    contentDescription = "Close details",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        title = {
                            Text(
                                cocktail.strDrink,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 22.sp,
                                maxLines = 1
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
                                    imageVector = if (isFavourite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(cocktail.strDrinkThumb),
                            contentDescription = cocktail.strDrink,
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = cocktail.strDrink,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (!cocktail.strCategory.isNullOrBlank()) {
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Category,
                                                contentDescription = "Category",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                cocktail.strCategory ?: "",
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                            )
                                        }
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Color(0xFF7C4DFF)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 6.dp)
                                )
                            }
                            if (!cocktail.strAlcoholic.isNullOrBlank()) {
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.LocalBar,
                                                contentDescription = "Alcoholic",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                cocktail.strAlcoholic ?: "",
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                            )
                                        }
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Color(0xFF00BFAE)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }
                        }
                        if (!cocktail.strGlass.isNullOrBlank()) {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.WineBar,
                                            contentDescription = "Glass",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            cocktail.strGlass ?: "",
                                            color = Color.White,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                        )
                                    }
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFF6D4C41)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            )
                        }
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        cocktail.getIngredientsMeasures().forEach { (ingredient, measure) ->
                            Text(
                                text = "${ingredient ?: ""} ${measure ?: ""}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = cocktail.strInstructions ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val prepareTime = calculatePrepareTime(cocktail.strInstructions)
                        Text(
                            text = "Prepare time",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatPrepareTime(prepareTime),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onPrepareNow(prepareTime) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Prepare Now")
                        }
                    }
                }
            }
        }
    }
}

fun calculatePrepareTime(instructions: String?): Int {
    val timeMap = mapOf(
        "shake" to 30, // 30 seconds
        "stir" to 20,  // 20 seconds
        "blend" to 45, // 45 seconds
        "pour" to 10,  // 10 seconds
        "mix" to 25,  // 25 seconds
        "add" to 5,   // 5 seconds
        "garnish" to 20, // 20 seconds
        "layer" to 40, // 40 seconds
        "muddle" to 45, // 45 seconds
        "crush" to 35,  // 35 seconds
        "top" to 15, // 15 seconds
        "next" to 10, // 10 seconds
        "after" to 10, // 10 seconds
        "combine" to 15, // 15 seconds
        "well" to 5, // 5 seconds
        "repeat" to 30, // 30 seconds
        "serve" to 5, // 5 seconds
        "gently" to 10, // 10 seconds
        "minutes" to 120, // 120 seconds
        "seconds" to 20, // 20 seconds
        "heat" to 60, // 60 seconds
        "cool" to 30, // 30 seconds
        "slowly" to 15, // 15 seconds
        "fill" to 10, // 10 seconds
        "place" to 10, // 10 seconds
        "squeeze" to 20, // 20 seconds
        "cover" to 15, // 15 seconds
        "whiz" to 20, // 30 seconds
    )

    if (instructions == null) return 0

    val lowerCaseInstructions = instructions.lowercase()
    var totalTime = 0

    timeMap.forEach { (keyword, time) ->
        val count = lowerCaseInstructions.split(keyword).size - 1
        totalTime += count * time
    }

    return maxOf(30, totalTime)
}

fun formatPrepareTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return when {
        minutes == 0 -> "$seconds seconds"
        seconds == 0 -> "$minutes minutes"
        else -> "$minutes minutes $seconds seconds"
    }
}
