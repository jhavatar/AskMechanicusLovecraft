package io.chthonic.mechanicuslovecraft.presentation.ktx

import android.os.Bundle
import androidx.core.net.toUri
import androidx.lifecycle.asFlow
import androidx.navigation.*

//sets value to previous savedStateHandle unless route is specified
fun <T> NavController.setNavigationResult(route: String? = null, key: String, result: T) {
    if (route == null) {
        previousBackStackEntry?.savedStateHandle?.set(key, result)
    } else {
        getBackStackEntry(route).savedStateHandle.set(key, result)
    }
}

fun <T> NavController.getNavigationResult(key: String) =
    currentBackStackEntry?.savedStateHandle?.get<T>(key)

fun <T> NavController.observeNavigationResultLiveData(key: String) =
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> NavController.observeNavigationResult(key: String) =
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.asFlow()

fun NavController.navigateWithObject(
    route: String,
    navOptions: NavOptions? = null,
    extras: Navigator.Extras? = null,
    arguments: Bundle? = null
) {
    val routeLink = NavDeepLinkRequest.Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null && arguments != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, arguments, navOptions, extras)
    } else {
        navigate(route, navOptions, extras)
    }
}