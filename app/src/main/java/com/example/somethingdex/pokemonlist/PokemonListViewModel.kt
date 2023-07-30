package com.example.somethingdex.pokemonlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.somethingdex.data.models.PokedexListEntry
import com.example.somethingdex.data.pokemon.PokemonDao
import com.example.somethingdex.repository.PokemonRepository
import com.example.somethingdex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val dao: PokemonDao
): ViewModel(){

    var pokemonList:List<PokedexListEntry> by mutableStateOf(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    private fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate{ palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

    fun fetchColors(url: String, context: Context, onCalculated: (Color) -> Unit) {
        viewModelScope.launch {
            // Requesting the image using coil's ImageRequest
            val req = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()

            val result = req.context.imageLoader.execute(req)

            if (result is SuccessResult) {
                // Save the drawable as a state in order to use it on the composable
                // Converting it to bitmap and using it to calculate the palette
                calcDominantColor(result.drawable) { color ->
                    onCalculated(color)
                }
            }
        }
    }


    fun searchPokemonList(query: String) {
        val listToSearch = pokemonList
        viewModelScope.launch(Dispatchers.Default){
            if (query.isEmpty()) {
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.take(query.length).contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if (isSearchStarting) {
                isSearchStarting = false
            }
            pokemonList = results
            isSearching.value = true
        }
    }
    fun loadPokemonPaginated() {
        viewModelScope.launch {
            if (pokemonList.isEmpty()) {
                for (id in 1..100) {
                    when (val result = repository.getPokemonInfo(id)) {
                        is Resource.Success -> {
                            dao.upsertPokemon(
                                PokedexListEntry(
                                    result.data!!.id,
                                    result.data.name,
                                    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${result.data.id}.png",
                                    result.data.types[0].type.name
                                )
                            )
                        }

                        is Resource.Error -> {
                            TODO()
                        }

                        else -> {
                            TODO()
                        }
                    }
                }
                pokemonList = repository.getAllPokemon()
            }
        }
    }
}