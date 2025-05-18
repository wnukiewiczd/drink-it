package com.example.drinkit

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Explore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.example.drinkit.CountdownScreen
import com.example.drinkit.ExploreScreen
import com.example.drinkit.AppTopBar
import com.example.drinkit.HomeScreen
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ArrowBack
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.zIndex
import com.example.drinkit.SettingsScreen
import com.example.drinkit.ThemeMode
import com.example.drinkit.DrawerTopBar
import com.example.drinkit.FindScreen

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavBar(navController: NavHostController, currentIndex: Int, onTabSelected: (Int) -> Unit) {
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
            val circleSize = if (selected) 48.dp else iconSize // małe kółko pod ikonką

            NavigationBarItem(
                icon = {
                    Box(
                        contentAlignment = Alignment.Center // USUNIĘTO fillMaxHeight i wrapContentSize!
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
                    indicatorColor = Color.Transparent // nie używaj indicatorColor!
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

    // Dodaj stan wybranej litery na poziomie AppNavigation
    var selectedLetter by remember { mutableStateOf('A') }

    // Dodaj licznik do resetowania FindScreen
    var findScreenResetSignal by remember { mutableStateOf(0) }

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
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.75f),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp), // zaokrąglenie prawej krawędzi
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        DrawerTopBar(onCloseClick = { scope.launch { drawerState.close() } })
                        // Możesz dodać tu kolejne elementy szuflady poniżej paska
                    }
                }
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
                HorizontalPager(
                    count = items.size,
                    state = pagerState,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
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
                            "Countdown" -> CountdownScreen()
                            "Home" -> HomeScreen(
                                onExploreClick = { onTabSelected(3) },
                                onFindClick = { onTabSelected(4) }
                            )
                            "Explore" -> ExploreScreen(
                                selectedLetter = selectedLetter,
                                onLetterSelected = { selectedLetter = it }
                            )
                            "Find" -> FindScreen(resetSignal = findScreenResetSignal)
                        }
                    }
                }
            }
        }
    }
}
