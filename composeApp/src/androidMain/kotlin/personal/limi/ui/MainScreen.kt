package personal.limi.ui

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import personal.limi.R
import personal.limi.ui.screen.HomeScreen
import personal.limi.ui.screen.RuleScreen
import personal.limi.ui.screen.SettingScreen


enum class MainScreen(val titleResId: Int, val icon: ImageVector) {
    Home(titleResId = R.string.home, icon = Icons.Outlined.Home), Rule(
        titleResId = R.string.rule, icon = Icons.AutoMirrored.Outlined.ListAlt
    ),
    Setting(titleResId = R.string.setting, icon = Icons.Outlined.Settings)
}

@Composable
@Preview
fun LimiApp(
    viewModel: MainViewModel = viewModel { MainViewModel() },
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MainScreen.valueOf(
        backStackEntry?.destination?.route ?: MainScreen.Home.name
    )

    Scaffold(
        bottomBar = {
            AppNavBar(currentScreen, navController)
        }) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MainScreen.Home.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            composable(MainScreen.Home.name) { HomeScreen() }
            composable(MainScreen.Rule.name) {
                RuleScreen(MainScreen.Rule.titleResId)
            }
            composable(MainScreen.Setting.name) {
                SettingScreen(MainScreen.Setting.titleResId)
            }
        }
    }


}

@Composable
fun AppNavBar(currentScreen: MainScreen, navController: NavHostController) {
    NavigationBar {
        MainScreen.entries.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                label = { Text(stringResource(id = screen.titleResId)) },
                selected = currentScreen == screen,
                onClick = {
                    navController.navigate(screen.name) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}