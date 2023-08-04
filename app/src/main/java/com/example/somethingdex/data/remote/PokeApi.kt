package com.example.somethingdex.data.remote

import com.example.somethingdex.data.remote.responses.Pokemon
import com.example.somethingdex.data.remote.responses.SpeciesX
import retrofit2.http.GET
import retrofit2.http.Path
interface PokeApi {
    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(
        @Path("id") name: Int
    ): Pokemon

    @GET("pokemon-species/{id}")
    suspend fun getPokemonDescription(
        @Path("id") name: Int
    ): SpeciesX
}