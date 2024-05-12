package io.chthonic.mechanicuslovecraft.presentation

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

data class AppContainerState(
    val scaffoldState: ScaffoldState,
    val snackbarScope: CoroutineScope,
    val navController: NavHostController,
    val appBarState: MutableState<AppBarState>,
) {
    data class AppBarState(
        val appBarTitle: String = "",
        val showBackButton: Boolean = true,
        val showSettingsButton: Boolean = false,
    )

    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        snackbarScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }

    fun updateAppBarTitle(title: String) {
        appBarState.value = appBarState.value.copy(
            appBarTitle = title,
        )
    }

    fun updateShowBackButton(showBackButton: Boolean) {
        Timber.v("D3V updateShowSettingsButton = $showBackButton")
        appBarState.value = appBarState.value.copy(
            showBackButton = showBackButton,
        )
    }

    fun updateShowSettingsButton(showSettingsButton: Boolean) {
        Timber.v("D3V updateShowSettingsButton = $showSettingsButton")
        appBarState.value = appBarState.value.copy(
            showSettingsButton = showSettingsButton,
        )
    }
}

@Composable
fun rememberAppContainerState(
    scaffoldState: ScaffoldState = rememberScaffoldState(
        snackbarHostState = remember {
            SnackbarHostState()
        }
    ),
    navController: NavHostController = rememberNavController(),
    snackbarScope: CoroutineScope = rememberCoroutineScope(),
): AppContainerState {
    val appBarTitle: String = stringResource(R.string.app_name)
    val appBarState: MutableState<AppContainerState.AppBarState> = remember {
        mutableStateOf(
            AppContainerState.AppBarState(
                appBarTitle = appBarTitle
            )
        )
    }
    return remember(scaffoldState, navController, snackbarScope, appBarState) {
        AppContainerState(
            scaffoldState = scaffoldState,
            navController = navController,
            snackbarScope = snackbarScope,
            appBarState = appBarState
        )
    }
}