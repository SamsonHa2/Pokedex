package com.example.somethingdex.data.models

import com.example.somethingdex.data.remote.responses.Type

data class PokedexListEntry(
    val pokemonName: String,
    val imageUrl: String,
    val number: Int,
    val pokemonTypes: List<Type>
)
