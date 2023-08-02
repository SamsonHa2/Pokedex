package com.example.somethingdex.di

import android.content.Context
import androidx.room.Room
import com.example.somethingdex.data.PokemonDatabase
import com.example.somethingdex.data.pokemon.PokemonDao
import com.example.somethingdex.data.remote.PokeApi
import com.example.somethingdex.repository.PokemonRepository
import com.example.somethingdex.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokeApi,
        dao: PokemonDao
    ) = PokemonRepository(api, dao)

    @Singleton
    @Provides
    fun providePokeApi(): PokeApi{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }

    @Provides
    fun providePokemonDao(database: PokemonDatabase): PokemonDao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): PokemonDatabase {
        return Room
            .databaseBuilder(
                appContext,
                PokemonDatabase::class.java,
                "pokemon.db"
            )
            .build()
    }
}