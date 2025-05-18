package com.example.drinkit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerTopBar(onCloseClick: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary // pasek: primary
        ),
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Zamknij szufladę",
                    tint = MaterialTheme.colorScheme.onPrimary // zawartość: onPrimary
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Favourites",
                    color = MaterialTheme.colorScheme.onPrimary, // zawartość: onPrimary
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
