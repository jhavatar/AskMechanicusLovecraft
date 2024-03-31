package io.chthonic.mechanicuslovecraft.presentation.nav

import android.os.Bundle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Destination(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {

    companion object {
        private const val CHAR_ID_ARGUMENT: String = "charId"
    }

    data object Console : Destination(route = "console")
}