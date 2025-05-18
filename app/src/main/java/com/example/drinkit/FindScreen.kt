package com.example.drinkit

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FindScreen(resetSignal: Int) {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    var searching by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    // Resetuj stan po zmianie resetSignal
    LaunchedEffect(resetSignal) {
        searching = false
        query = TextFieldValue("")
        focusManager.clearFocus()
    }

    // Animacje rozmiaru i położenia
    val boxWidth by animateDpAsState(
        targetValue = if (searching) 320.dp else 280.dp, // zmniejsz początkową szerokość
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
                // Kliknięcie poza inputem chowa kursor i klawiaturę
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
                color = MaterialTheme.colorScheme.onBackground, // zmiana tła kontenera
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
                                color = MaterialTheme.colorScheme.background // kolor placeholdera
                            ) 
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .focusRequester(focusRequester),
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
                            color = MaterialTheme.colorScheme.background // kolor wpisywanego tekstu
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary) // tło ikony lupki
                    ) {
                        IconButton(
                            onClick = {
                                searching = true
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            enabled = query.text.isNotBlank(),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary, // kolor ikony
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
            // Animacja pojawiania się napisu po wyszukiwaniu
            AnimatedVisibility(
                visible = searching,
                enter = fadeIn(tween(300, delayMillis = 200)),
                exit = fadeOut(tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nothing found",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
        }
    }
}
