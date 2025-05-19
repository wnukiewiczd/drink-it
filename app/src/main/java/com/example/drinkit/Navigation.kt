package com.example.drinkit

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavBar(navController: androidx.navigation.NavHostController, currentIndex: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf("Settings", "Countdown", "Home", "Explore", "Find")
    val icons = listOf(
        Icons.Default.Settings,
        Icons.Default.Timer,
        Icons.Default.Home,
        Icons.Default.Explore,
        Icons.Default.Search
    )

    NavigationBar(
        modifier = Modifier.height(64.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEachIndexed { index, item ->
            val selected = currentIndex == index
            val iconSize = 32.dp
            val iconScale by animateFloatAsState(
                if (selected) 1.2f else 1.0f,
                label = ""
            )
            val circleSize = if (selected) 48.dp else iconSize

            NavigationBarItem(
                icon = {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(circleSize)
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    icons[index],
                                    contentDescription = item,
                                    modifier = Modifier
                                        .scale(iconScale)
                                        .size(iconSize),
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        } else {
                            Icon(
                                icons[index],
                                contentDescription = item,
                                modifier = Modifier
                                    .scale(iconScale)
                                    .size(iconSize),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                selected = selected,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = Color.Transparent
                ),
                alwaysShowLabel = false
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AppNavigation(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeChange: ((ThemeMode) -> Unit)? = null
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf("Settings", "Countdown", "Home", "Explore", "Find")
    val pagerState = rememberPagerState(initialPage = 2)

    var currentThemeMode by remember { mutableStateOf(themeMode) }
    var selectedLetter by remember { mutableStateOf('A') }
    var findScreenResetSignal by remember { mutableStateOf(0) }

    // Stan do detailed drawer z ulubionych
    var selectedFavouriteDrink by remember { mutableStateOf<Cocktail?>(null) }
    var isDetailedDrawerOpen by remember { mutableStateOf(false) }
    var isLoadingFavouriteDetails by remember { mutableStateOf(false) }

    // Stan dla czasu przygotowania
    var prepareTime by remember { mutableStateOf(0) }

    // Funkcja do zmiany zakładki
    val switchTab: (Int) -> Unit = { page ->
        scope.launch { pagerState.animateScrollToPage(page) }
        if (items[page] == "Find") {
            findScreenResetSignal++
        }
    }

    // Zamykaj detailed drawer przy każdej zmianie zakładki
    LaunchedEffect(pagerState.currentPage) {
        isDetailedDrawerOpen = false
        selectedFavouriteDrink = null
    }

    val onTabSelected: (Int) -> Unit = { page ->
        scope.launch { pagerState.animateScrollToPage(page) }
        if (items[page] == "Find") {
            findScreenResetSignal++
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    onCloseClick = { scope.launch { drawerState.close() } },
                    isDrawerOpen = drawerState.isOpen,
                    onFavouriteClick = { idDrink ->
                        isLoadingFavouriteDetails = true
                        scope.launch {
                            drawerState.close()
                            val cocktail = try {
                                withContext(Dispatchers.IO) {
                                    ApiClient.api.getCocktailsById(idDrink).drinks?.firstOrNull()
                                }
                            } catch (e: Exception) {
                                null
                            }
                            selectedFavouriteDrink = cocktail
                            isDetailedDrawerOpen = cocktail != null
                            isLoadingFavouriteDetails = false
                        }
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    AppTopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        title = items[pagerState.currentPage]
                    )
                },
                bottomBar = {
                    BottomNavBar(
                        navController = navController,
                        currentIndex = pagerState.currentPage,
                        onTabSelected = onTabSelected
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    HorizontalPager(
                        count = items.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (items[page]) {
                                "Settings" -> SettingsScreen(
                                    themeMode = currentThemeMode,
                                    onThemeChange = {
                                        currentThemeMode = it
                                        onThemeChange?.invoke(it)
                                    }
                                )
                                "Countdown" -> CountdownScreen(initialTime = prepareTime)
                                "Home" -> HomeScreen(
                                    onExploreClick = { onTabSelected(3) },
                                    onFindClick = { onTabSelected(4) },
                                    onPrepareTimeChange = { time -> prepareTime = time },
                                    onTabSelected = { switchTab(it) }
                                )
                                "Explore" -> ExploreScreen(
                                    selectedLetter = selectedLetter,
                                    onLetterSelected = { selectedLetter = it },
                                    onPrepareTimeChange = { time -> prepareTime = time },
                                    onTabSelected = { switchTab(it) }
                                )
                                "Find" -> FindScreen(
                                    resetSignal = findScreenResetSignal,
                                    onPrepareTimeChange = { time -> prepareTime = time },
                                    onTabSelected = { switchTab(it) }
                                )
                            }
                        }
                    }
                    // Drawer szczegółów drinka z ulubionych - tylko w content!
                    if (isDetailedDrawerOpen && selectedFavouriteDrink != null) {
                        DetailedDrinkDrawer(
                            cocktail = selectedFavouriteDrink,
                            isOpen = isDetailedDrawerOpen,
                            onClose = { isDetailedDrawerOpen = false },
                            onPrepareNow = { time ->
                                prepareTime = time
                                isDetailedDrawerOpen = false
                                switchTab(1) // Przejdź do zakładki Countdown
                            }
                        )
                    }
                }
            }
        }
    }
}
