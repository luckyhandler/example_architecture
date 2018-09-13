package de.handler.mobile.core

import retrofit2.Call
import retrofit2.http.GET

interface RestService {
    @GET("v1/cards")
    fun getAllPokemonCards(): Call<PokemonCardWrapper>
}