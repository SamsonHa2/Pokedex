package com.example.somethingdex.data.pokemon

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.somethingdex.data.models.PokedexListEntry

@Dao
interface PokemonDao {

    @Upsert
    suspend fun upsertPokemon(pokemon: PokedexListEntry)

    @Query ("SELECT * FROM pokedexlistentry ORDER BY number ASC")
    suspend fun getAll(): List<PokedexListEntry>

}