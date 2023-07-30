package com.example.somethingdex.data.remote

import com.example.somethingdex.data.remote.responses.Pokemon
import retrofit2.http.GET
import retrofit2.http.Path
interface PokeApi {
    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(
        @Path("id") name: Int
    ): Pokemon
}