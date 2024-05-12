package io.chthonic.mechanicuslovecraft.presentation

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.chthonic.mechanicuslovecraft.presentation.theme.DraculaPurple

private const val LABEL_BACK = "Back"
private const val LABEL_SETTINGS = "Settings"

@Preview
@Composable
fun AppContainer() {
    val appContainerState = rememberAppContainerState()
    Scaffold(
        scaffoldState = appContainerState.scaffoldState,
        topBar = {
            // your top bar
            TopAppBar(
                title = {
                    Text(
                        appContainerState.appBarState.value.appBarTitle,
                        color = DraculaPurple,
                    )
                },
                navigationIcon = if (appContainerState.appBarState.value.showBackButton) {
                    {
                        IconButton(onClick = { appContainerState.navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                tint = DraculaPurple,
                                contentDescription = LABEL_BACK,
                            )
                        }
                    }
                } else {
                    null
                },
                actions = {
                    if (appContainerState.appBarState.value.showSettingsButton) {
                        IconButton(onClick = {
                            appContainerState.navigateToSettings()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                tint = DraculaPurple,
                                contentDescription = LABEL_SETTINGS,
                            )
                        }
                    }
                })
        },
        floatingActionButton = {
            // your floating action button
        },
        drawerContent = null,
        content = { padding ->
            // your page content
            AppContainerNavHost(
                appContainerState = appContainerState,
                padding = padding,
            )
        },
        bottomBar = {
            // your bottom bar composable
        }
    )
}