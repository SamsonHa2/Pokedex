package com.example.somethingdex.data.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PokedexListEntry(
    @PrimaryKey val number: Int,
    val pokemonName: String,
    val imageUrl: String,
    val types: List<String>,
    val color: Int = Color.White.toArgb(),
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val specialAttack: Int,
    val specialDefense: Int,
    val speed: Int,
    val height: Double,
    val weight: Double,
)
