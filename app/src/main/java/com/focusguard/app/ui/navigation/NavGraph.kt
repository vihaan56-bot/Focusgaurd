package com.focusguard.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.focusguard.app.ui.apps.AppLimitsScreen
import com.focusguard.app.ui.apps.AppsViewModel
import com.focusguard.app.ui.dashboard.DashboardScreen
import com.focusguard.app.ui.dashboard.DashboardViewModel
import com.focusguard.app.ui.focus.FocusModeScreen
import com.focusguard.app.ui.focus.FocusViewModel
import com.focusguard.app.ui.history.HistoryScreen
import com.focusguard.app.ui.history.HistoryViewModel
import com.focusguard.app.ui.onboarding.PermissionOnboardingScreen
import com.focusguard.app.ui.settings.SettingsScreen
import com.focusguard.app.ui.settings.SettingsViewModel

@Composable
fun MainScreenNavHost(navController: NavHostController = rememberNavController()) {
    val items = listOf(
        Screen.Dashboard,
        Screen.Apps,
        Screen.Focus,
        Screen.History,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Onboarding.route) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = viewModel()
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) },
                    onNavigateToFocus = { navController.navigate(Screen.Focus.route) }
                )
            }
            composable(Screen.Apps.route) {
                val appsViewModel: AppsViewModel = viewModel()
                AppLimitsScreen(viewModel = appsViewModel)
            }
            composable(Screen.Focus.route) {
                val focusViewModel: FocusViewModel = viewModel()
                FocusModeScreen(viewModel = focusViewModel)
            }
            composable(Screen.History.route) {
                val historyViewModel: HistoryViewModel = viewModel()
                HistoryScreen(viewModel = historyViewModel)
            }
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) }
                )
            }
            composable(Screen.Onboarding.route) {
                PermissionOnboardingScreen(
                    onPermissionsCompleted = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
