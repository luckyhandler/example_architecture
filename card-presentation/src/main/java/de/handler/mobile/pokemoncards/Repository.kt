package de.handler.mobile.pokemoncards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.handler.mobile.core.CardProvider
import de.handler.mobile.core.PokemonCard
import kotlinx.coroutines.experimental.*

object Repository {
    private val cardProvider = CardProvider()
    private val allCards = mutableMapOf<String, PokemonCard>()
    private val allCardsLiveData = MutableLiveData<List<PokemonCard?>>()

    private var allCardsJob = Job()

    fun getAllPokemonCards(forceReload: Boolean = false): LiveData<List<PokemonCard?>> {
        if (allCardsLiveData.value == null || forceReload) {
            GlobalScope.launch {
                if (allCardsJob.isActive) {
                    allCardsJob.join()
                    return@launch
                }
            }

            allCardsJob = GlobalScope.launch {
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
        return GlobalScope.async {
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