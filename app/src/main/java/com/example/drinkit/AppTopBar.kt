package com.example.drinkit

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme as M3Theme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onMenuClick: () -> Unit, title: String) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = M3Theme.colorScheme.primary
        ),
        title = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title,
                    color = M3Theme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight()
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = M3Theme.colorScheme.onPrimary, modifier = Modifier.fillMaxHeight())
                }
            }
        }
    )
}
