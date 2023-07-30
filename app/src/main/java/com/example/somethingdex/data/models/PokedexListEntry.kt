package com.example.somethingdex.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PokedexListEntry(
    @PrimaryKey val number: Int,
    val pokemonName: String,
    val imageUrl: String,
    //@ColumnInfo(name = "types")
    //val typeOfPokemon: List<String> = listOf()
    val type: String
)
