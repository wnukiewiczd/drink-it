package com.example.drinkit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun CountdownScreen() {
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }
    var timeInSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) } // Dodane

    val hoursState = rememberLazyListState(initialFirstVisibleItemIndex = hours)
    val minutesState = rememberLazyListState(initialFirstVisibleItemIndex = minutes)
    val secondsState = rememberLazyListState(initialFirstVisibleItemIndex = seconds)
    val coroutineScope = rememberCoroutineScope()

    // Synchronizuj timeInSeconds z licznikami tylko gdy NIE trwa odliczanie
    LaunchedEffect(hours, minutes, seconds) {
        if (!isRunning && !isFinished) {
            timeInSeconds = hours * 3600 + minutes * 60 + seconds
        }
    }

    // Synchronizuj pickery z wartościami, gdy reset
    LaunchedEffect(isRunning) {
        if (!isRunning && !isFinished) {
            coroutineScope.launch { hoursState.scrollToItem(hours) }
            coroutineScope.launch { minutesState.scrollToItem(minutes) }
            coroutineScope.launch { secondsState.scrollToItem(seconds) }
        }
    }

    // Synchronizuj wartości z pickerów, gdy użytkownik scrolluje
    LaunchedEffect(hoursState.firstVisibleItemIndex) {
        if (!isRunning && !isFinished) hours = hoursState.firstVisibleItemIndex
    }
    LaunchedEffect(minutesState.firstVisibleItemIndex) {
        if (!isRunning && !isFinished) minutes = minutesState.firstVisibleItemIndex
    }
    LaunchedEffect(secondsState.firstVisibleItemIndex) {
        if (!isRunning && !isFinished) seconds = secondsState.firstVisibleItemIndex
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // dodano background z motywu
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayHeight = 120.dp
            val itemHeight = 50.dp

            if (!isRunning && !isFinished) {
                // Pickery dostępne zawsze, gdy minutnik nie jest uruchomiony i nie jest finished
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.height(displayHeight)
                ) {
                    TimePickerSection(
                        valueRange = 0..23,
                        state = hoursState,
                        enabled = true,
                        highlightColor = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        fontSize = 48.sp,
                        itemHeight = itemHeight
                    )
                    Text(
                        text = ":",
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    TimePickerSection(
                        valueRange = 0..59,
                        state = minutesState,
                        enabled = true,
                        highlightColor = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        fontSize = 48.sp,
                        itemHeight = itemHeight
                    )
                    Text(
                        text = ":",
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    TimePickerSection(
                        valueRange = 0..59,
                        state = secondsState,
                        enabled = true,
                        highlightColor = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        fontSize = 48.sp,
                        itemHeight = itemHeight
                    )
                }
            } else {
                // Statyczny wyświetlacz czasu podczas odliczania lub po zakończeniu
                Text(
                    text = "%02d:%02d:%02d".format(
                        timeInSeconds / 3600,
                        (timeInSeconds % 3600) / 60,
                        timeInSeconds % 60
                    ),
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .height(displayHeight)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            // Tekst "Finished" po zakończeniu odliczania
            if (isFinished) {
                Text(
                    text = "Finished",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            // ...przyciski start/pauza/reset...
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (isRunning) {
                            isRunning = false
                        } else if (isFinished) {
                            // Po zakończeniu minutnika play nie robi nic
                        } else if (timeInSeconds > 0) {
                            isRunning = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .size(width = 112.dp, height = 112.dp)
                ) {
                    if (isRunning && timeInSeconds > 0) {
                        Icon(
                            Icons.Default.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.size(64.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                Button(
                    onClick = {
                        isRunning = false
                        isFinished = false
                        hours = 0
                        minutes = 0
                        seconds = 0
                        timeInSeconds = 0
                        coroutineScope.launch { hoursState.scrollToItem(0) }
                        coroutineScope.launch { minutesState.scrollToItem(0) }
                        coroutineScope.launch { secondsState.scrollToItem(0) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .size(width = 112.dp, height = 112.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Restart",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(isRunning, timeInSeconds) {
        if (isRunning && timeInSeconds > 0) {
            delay(1000L)
            timeInSeconds -= 1
            if (timeInSeconds == 0) {
                isRunning = false
                isFinished = true
            }
        }
    }
}

@Composable
private fun TimePickerSection(
    valueRange: IntRange,
    state: androidx.compose.foundation.lazy.LazyListState,
    enabled: Boolean,
    highlightColor: Color,
    textColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    itemHeight: Dp
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }

    // Automatyczne centrowanie po zakończeniu scrollowania
    LaunchedEffect(enabled) {
        if (enabled) {
            snapshotFlow { state.isScrollInProgress }
                .filter { !it }
                .distinctUntilChanged()
                .collectLatest {
                    val target = state.firstVisibleItemIndex +
                        if (state.firstVisibleItemScrollOffset > itemHeightPx / 2) 1 else 0
                    coroutineScope.launch {
                        state.animateScrollToItem(target.coerceIn(0, valueRange.last))
                    }
                }
        }
    }

    Box(
        modifier = Modifier.width(56.dp), // zmniejszona szerokość
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .height(itemHeight)
                .background(
                    color = if (enabled) Color.Transparent else Color.LightGray.copy(alpha = 0.2f)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            userScrollEnabled = enabled
        ) {
            items(valueRange.count()) { index: Int -> // dodano typ parametru
                val value = valueRange.first + index
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "%02d".format(value),
                        fontSize = fontSize,
                        color = if (state.firstVisibleItemIndex == index) highlightColor else textColor.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp) // brak paddingu
                    )
                }
            }
        }
    }
}
