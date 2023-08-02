package com.example.somethingdex.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.somethingdex.data.models.PokedexListEntry
import com.example.somethingdex.data.pokemon.PokemonDao

@Database(
    entities = [PokedexListEntry::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class PokemonDatabase: RoomDatabase() {
    abstract val dao: PokemonDao
}

class Converters{
    @TypeConverter
    fun stringToList(str: String?): List<String>? {
        return str?.split(",")
    }

    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}
