package io.chthonic.mechanicuslovecraft.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.chthonic.mechanicuslovecraft.presentation.console.ConsoleScreen
import io.chthonic.mechanicuslovecraft.presentation.nav.Destination

@Composable
fun AppContainerNavHost(
    appContainerState: AppContainerState,
    padding: PaddingValues
) = NavHost(
    navController = appContainerState.navController,
    startDestination = Destination.Console.route,
    modifier = androidx.compose.ui.Modifier.padding(padding)
) {
    composable(Destination.Console.route) {
        ConsoleScreen()
    }
}