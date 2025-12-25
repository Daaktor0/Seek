package com.seek.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.seek.app.ui.screen.add.AddApplicationScreen
import com.seek.app.ui.screen.detail.ApplicationDetailScreen
import com.seek.app.ui.screen.home.HomeScreen
import com.seek.app.ui.screen.offboarding.GotJobScreen
import com.seek.app.ui.screen.paywall.PaywallScreen
import com.seek.app.ui.screen.settings.SettingsScreen

@Composable
fun SeekNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAddClick = { inputType ->
                    navController.navigate(Screen.AddApplication.createRoute(inputType))
                },
                onApplicationClick = { applicationId ->
                    navController.navigate(Screen.ApplicationDetail.createRoute(applicationId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onUpgradeClick = {
                    navController.navigate(Screen.Paywall.route)
                }
            )
        }
        
        composable(
            route = "${Screen.AddApplication.route}?${Screen.AddApplication.INPUT_TYPE_ARG}={${Screen.AddApplication.INPUT_TYPE_ARG}}",
            arguments = listOf(
                navArgument(Screen.AddApplication.INPUT_TYPE_ARG) {
                    type = NavType.StringType
                    defaultValue = "manual"
                }
            )
        ) { backStackEntry ->
            val inputType = backStackEntry.arguments?.getString(Screen.AddApplication.INPUT_TYPE_ARG) ?: "manual"
            AddApplicationScreen(
                inputType = inputType,
                onNavigateBack = { navController.popBackStack() },
                onApplicationAdded = { applicationId ->
                    navController.popBackStack()
                    navController.navigate(Screen.ApplicationDetail.createRoute(applicationId))
                },
                onUpgradeClick = {
                    navController.navigate(Screen.Paywall.route)
                }
            )
        }
        
        composable(
            route = Screen.ApplicationDetail.route,
            arguments = listOf(
                navArgument(Screen.ApplicationDetail.APPLICATION_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Screen.ApplicationDetail.APPLICATION_ID_ARG) ?: return@composable
            ApplicationDetailScreen(
                applicationId = applicationId,
                onNavigateBack = { navController.popBackStack() },
                onGotJob = {
                    navController.navigate(Screen.GotJob.createRoute(applicationId))
                }
            )
        }
        
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.GotJob.route,
            arguments = listOf(
                navArgument(Screen.GotJob.APPLICATION_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString(Screen.GotJob.APPLICATION_ID_ARG) ?: return@composable
            GotJobScreen(
                applicationId = applicationId,
                onNavigateHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
        
        composable(route = Screen.Paywall.route) {
            PaywallScreen(
                onNavigateBack = { navController.popBackStack() },
                onPurchaseComplete = { navController.popBackStack() }
            )
        }
    }
}
