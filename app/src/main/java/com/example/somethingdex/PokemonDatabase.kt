package com.example.somethingdex

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.somethingdex.data.models.PokedexListEntry
import com.example.somethingdex.data.pokemon.PokemonDao
import com.example.somethingdex.data.remote.responses.Pokemon

@Database(
    entities = [PokedexListEntry::class],
    version = 1
)
abstract class PokemonDatabase: RoomDatabase() {

    abstract val dao: PokemonDao
}
