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

    private var cachedPokemonList = listOf<PokedexListEntry>()
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
        val listToSearch = if(isSearchStarting) {
            pokemonList
        } else {
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default){

            if (query.isEmpty()) {
                pokemonList = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.take(query.length).contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if (isSearchStarting) {
                cachedPokemonList = pokemonList
                isSearchStarting = false
            }
            pokemonList = results
            isSearching.value = true
        }
    }
    fun loadPokemonPaginated() {
        viewModelScope.launch {
            if (pokemonList.isEmpty()) {
                for (id in 1..20) {
                    val pokemonDescription = when (val result = repository.getPokemonDescription(id)) {
                        is Resource.Success -> {
                            result.data!!.flavor_text_entries[1].flavor_text.replace("\n", " ")
                        }

                        is Resource.Error -> {
                            "error loading description"
                        }

                        is Resource.Loading -> {
                            TODO()
                        }
                    }
                    when (val result = repository.getPokemonInfo(id)) {
                        is Resource.Success -> {
                            val pokemonTypes = emptyList<String>().toMutableList()
                            val pokemonEntry: PokedexListEntry
                            result.data.let { pokemon ->
                                for (type in pokemon!!.types){
                                    pokemonTypes += type.type.name
                                }
                                pokemonEntry = PokedexListEntry(
                                    number = pokemon.id,
                                    pokemonName = pokemon.name.replaceFirstChar{it.titlecase()},
                                    description = pokemonDescription,
                                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${pokemon.id}.png",
                                    types = pokemonTypes,
                                    hp = pokemon.stats[0].base_stat,
                                    attack = pokemon.stats[1].base_stat,
                                    defense = pokemon.stats[2].base_stat,
                                    specialAttack = pokemon.stats[3].base_stat,
                                    specialDefense = pokemon.stats[4].base_stat,
                                    speed = pokemon.stats[5].base_stat,
                                    height = pokemon.height.toDouble() / 10,
                                    weight = pokemon.weight.toDouble() / 10
                                )
                            }
                            dao.upsertPokemon(
                                pokemonEntry
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