package com.example.somethingdex.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.somethingdex.data.models.PokedexListEntry
import com.example.somethingdex.repository.PokemonRepository
import com.example.somethingdex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository,
): ViewModel() {
    suspend fun getPokemonInfo(id: String): Resource<PokedexListEntry>{
        return repository.getPokemon(id.toInt())
    }
}