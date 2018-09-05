package de.adorsys.mobile.core

import android.util.Log
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CardProvider {
    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pokemontcg.io/")
            // Adds json to object mapper to retrofit object
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    private val restService = retrofit.create(RestService::class.java)

    fun getPokemonCards(): Deferred<PokemonCardWrapper?> {
        return GlobalScope.async {
            try {
                val response = restService.getAllPokemonCards().execute()
                return@async if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: HttpException) {
                Log.e("NETWORK_ERROR", e.message())
                return@async null
            }
        }
    }
}