package de.handler.mobile.core

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.coroutines.CoroutineContext

class CardProvider : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.pokemontcg.io/")
        // Adds json to object mapper to retrofit object
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val restService = retrofit.create(RestService::class.java)

    fun getPokemonCards(): Deferred<PokemonCardWrapper?> {
        return async {
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