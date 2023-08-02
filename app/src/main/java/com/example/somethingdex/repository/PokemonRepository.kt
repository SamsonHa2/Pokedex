package com.example.somethingdex.repository

import com.example.somethingdex.data.models.PokedexListEntry
import com.example.somethingdex.data.pokemon.PokemonDao
import com.example.somethingdex.data.remote.PokeApi
import com.example.somethingdex.data.remote.responses.Pokemon
import com.example.somethingdex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi,
    private val dao: PokemonDao
){
    suspend fun getPokemonInfo(id: Int): Resource<Pokemon> {
        val response = try{
            api.getPokemonInfo(id)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }

    suspend fun getAllPokemon(): List<PokedexListEntry> {
        return dao.getAll()
    }

    suspend fun getPokemon(id: Int): Resource<PokedexListEntry>{
        val response = try{
            dao.getPokemon(id)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occurred.")
        }
        return Resource.Success(response)
    }
}