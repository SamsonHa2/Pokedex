package com.example.somethingdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.somethingdex.pokemondetail.PokemonDetailScreen
import com.example.somethingdex.pokemonlist.PokemonListScreen
import com.example.somethingdex.ui.theme.SomethingDexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {

            SomethingDexTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "pokemon_list_screen"
                ) {

                    composable("pokemon_list_screen") {
                        PokemonListScreen(navController = navController)
                    }
                    composable(
                        "pokemon_detail_screen/{id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val id = remember {
                            it.arguments?.getString("id")
                        }
                        PokemonDetailScreen(
                            id = id?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
