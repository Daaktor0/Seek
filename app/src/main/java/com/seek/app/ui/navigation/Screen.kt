package com.seek.app.ui.navigation

/**
 * Navigation destinations for the Seek app.
 * Using sealed class for type-safe navigation.
 */
sealed class Screen(val route: String) {
    
    data object Home : Screen("home")
    
    data object AddApplication : Screen("add_application") {
        const val INPUT_TYPE_ARG = "inputType"
        fun createRoute(inputType: String = "manual") = "add_application?$INPUT_TYPE_ARG=$inputType"
    }
    
    data object ApplicationDetail : Screen("application_detail/{applicationId}") {
        const val APPLICATION_ID_ARG = "applicationId"
        fun createRoute(applicationId: String) = "application_detail/$applicationId"
    }
    
    data object Settings : Screen("settings")
    
    data object GotJob : Screen("got_job/{applicationId}") {
        const val APPLICATION_ID_ARG = "applicationId"
        fun createRoute(applicationId: String) = "got_job/$applicationId"
    }
    
    data object Paywall : Screen("paywall")
}
