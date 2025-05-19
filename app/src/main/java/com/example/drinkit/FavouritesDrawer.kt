package com.example.drinkit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerTopBar(onCloseClick: () -> Unit) {
    TopAppBar(
        title = { Text("Favourites") },
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Close favourites drawer",
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    onCloseClick: () -> Unit,
    isDrawerOpen: Boolean,
    onFavouriteClick: (String) -> Unit
) {
    val context = LocalContext.current
    var favourites by remember { mutableStateOf<List<FavouriteEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(isDrawerOpen) {
        if (isDrawerOpen) {
            isLoading = true
            val favs = kotlinx.coroutines.withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(context)
                db.favouriteDao().getAll()
            }
            favourites = favs
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.75f),
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            DrawerTopBar(onCloseClick = onCloseClick)
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (favourites.isEmpty()) {
                Text(
                    text = "You have no favourite drinks.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    favourites.forEach { fav ->
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onFavouriteClick(fav.idDrink) }
                        ) {
                            Text(
                                text = fav.drinkName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
