package de.handler.mobile.pokemoncards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.handler.mobile.core.CardProvider
import de.handler.mobile.core.PokemonCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object Repository : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val cardProvider = CardProvider()
    private val allCards = mutableMapOf<String, PokemonCard>()
    private val allCardsLiveData = MutableLiveData<List<PokemonCard?>>()

    private var allCardsJob = Job()

    fun getAllPokemonCards(forceReload: Boolean = false): LiveData<List<PokemonCard?>> {
        if (allCardsLiveData.value == null || forceReload) {
            launch {
                if (allCardsJob.isActive) {
                    allCardsJob.join()
                    return@launch
                }
            }

            allCardsJob = launch {
                val pokemonCardWrapper = cardProvider.getPokemonCards().await()

                pokemonCardWrapper?.cards?.forEach { card ->
                    card.id?.let { allCards.put(it, card) }
                }

                allCardsLiveData.postValue(pokemonCardWrapper?.cards)
            }
        }
        return allCardsLiveData
    }

    fun getPokemonCard(id: String): Deferred<PokemonCard?> {
        return async {
            if (allCardsJob.isActive) {
                allCardsJob.join()
            }

            return@async if (allCards.contains(id)) {
                allCards[id]
            } else {
                null
            }
        }
    }
}