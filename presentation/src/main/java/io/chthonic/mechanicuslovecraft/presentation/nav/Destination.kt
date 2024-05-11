package io.chthonic.mechanicuslovecraft.presentation.nav

import androidx.navigation.NamedNavArgument

sealed class Destination(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {

    companion object {
        private const val CHAR_ID_ARGUMENT: String = "charId"
    }

    data object Console : Destination(route = "console")
}