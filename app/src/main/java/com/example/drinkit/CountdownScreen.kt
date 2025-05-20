package com.example.drinkit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext

// --- ViewModel ---
class CountdownViewModel : ViewModel() {
    var hours by mutableStateOf(0)
    var minutes by mutableStateOf(0)
    var seconds by mutableStateOf(0)
    var timeInSeconds by mutableStateOf(0)
    var isRunning by mutableStateOf(false)
    var isFinished by mutableStateOf(false)
    var showTimePicker by mutableStateOf(true)
    var lastTimeInSeconds by mutableStateOf(0)
    var initialTime by mutableStateOf(0)
    var initialized by mutableStateOf(false)

    // Zmieniono nazwę metody, aby uniknąć konfliktu z setterem
    fun applyInitialTime(newTime: Int) {
        if (newTime > 0) {
            hours = newTime / 3600
            minutes = (newTime % 3600) / 60
            seconds = newTime % 60
            timeInSeconds = newTime
            lastTimeInSeconds = newTime
            showTimePicker = false
            isFinished = false
        } else {
            hours = 0
            minutes = 0
            seconds = 0
            timeInSeconds = 0
            lastTimeInSeconds = 0
            showTimePicker = true
            isFinished = false
        }
        initialTime = newTime
        initialized = true
    }

    fun reset() {
        isRunning = false
        isFinished = false
        showTimePicker = true
        hours = 0
        minutes = 0
        seconds = 0
        timeInSeconds = 0
        lastTimeInSeconds = 0
    }
}

@Composable
fun CountdownScreen(initialTime: Int = 0) {
    // Domyślny scoping ViewModelu (do Composable)
    val viewModel: CountdownViewModel = viewModel()

    // --- Picker states ---
    val hoursState = rememberLazyListState(initialFirstVisibleItemIndex = viewModel.hours)
    val minutesState = rememberLazyListState(initialFirstVisibleItemIndex = viewModel.minutes)
    val secondsState = rememberLazyListState(initialFirstVisibleItemIndex = viewModel.seconds)
    val coroutineScope = rememberCoroutineScope()

    // Synchronizuj initialTime z ViewModelem TYLKO jeśli użytkownik nie zaczął odliczania ani nie edytuje czasu
    LaunchedEffect(initialTime) {
        if (viewModel.showTimePicker && (viewModel.initialTime != initialTime || !viewModel.initialized)) {
            viewModel.applyInitialTime(initialTime)
        }
    }

    // Synchronizuj timeInSeconds z licznikami tylko podczas edycji czasu (showTimePicker == true)
    LaunchedEffect(viewModel.hours, viewModel.minutes, viewModel.seconds) {
        if (viewModel.showTimePicker) {
            viewModel.timeInSeconds = viewModel.hours * 3600 + viewModel.minutes * 60 + viewModel.seconds
            viewModel.lastTimeInSeconds = viewModel.timeInSeconds
        }
    }

    // Aktualizacja wyświetlanych wartości gdy zmieni się timeInSeconds (np. podczas odliczania lub po pauzie)
    LaunchedEffect(viewModel.timeInSeconds) {
        if (!viewModel.showTimePicker) {
            viewModel.lastTimeInSeconds = viewModel.timeInSeconds
        }
    }

    // Synchronizuj pickery z wartościami, tylko gdy pokazujemy picker
    LaunchedEffect(viewModel.showTimePicker) {
        if (viewModel.showTimePicker) {
            viewModel.hours = viewModel.timeInSeconds / 3600
            viewModel.minutes = (viewModel.timeInSeconds % 3600) / 60
            viewModel.seconds = viewModel.timeInSeconds % 60

            coroutineScope.launch { hoursState.scrollToItem(viewModel.hours) }
            coroutineScope.launch { minutesState.scrollToItem(viewModel.minutes) }
            coroutineScope.launch { secondsState.scrollToItem(viewModel.seconds) }
        }
    }

    // Synchronizuj wartości z pickerów, gdy użytkownik scrolluje
    LaunchedEffect(hoursState.firstVisibleItemIndex) {
        if (viewModel.showTimePicker) viewModel.hours = hoursState.firstVisibleItemIndex
    }
    LaunchedEffect(minutesState.firstVisibleItemIndex) {
        if (viewModel.showTimePicker) viewModel.minutes = minutesState.firstVisibleItemIndex
    }
    LaunchedEffect(secondsState.firstVisibleItemIndex) {
        if (viewModel.showTimePicker) viewModel.seconds = secondsState.firstVisibleItemIndex
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val displayHeight = 120.dp
            val itemHeight = 50.dp

            if (viewModel.showTimePicker) {
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
                Text(
                    text = "%02d:%02d:%02d".format(
                        viewModel.timeInSeconds / 3600,
                        (viewModel.timeInSeconds % 3600) / 60,
                        viewModel.timeInSeconds % 60
                    ),
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .height(displayHeight)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            if (viewModel.isFinished) {
                Text(
                    text = "Finished",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (viewModel.isRunning) {
                            viewModel.isRunning = false
                        } else if (viewModel.isFinished) {
                            // Po zakończeniu minutnika play nie robi nic
                        } else if (viewModel.timeInSeconds > 0) {
                            viewModel.isRunning = true
                            viewModel.showTimePicker = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .size(width = 112.dp, height = 112.dp)
                ) {
                    if (viewModel.isRunning && viewModel.timeInSeconds > 0) {
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
                        viewModel.reset()
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

    LaunchedEffect(viewModel.isRunning, viewModel.timeInSeconds) {
        if (viewModel.isRunning && viewModel.timeInSeconds > 0) {
            delay(1000L)
            viewModel.timeInSeconds -= 1
            if (viewModel.timeInSeconds == 0) {
                viewModel.isRunning = false
                viewModel.isFinished = true
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
