package com.mazrou.boilerplate.repository.main

import com.mazrou.boilerplate.model.ui.Ayat
import com.mazrou.boilerplate.model.ui.Racine
import com.mazrou.boilerplate.ui.main.state.MainViewState
import com.mazrou.boilerplate.util.DataState
import com.mazrou.boilerplate.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface Repository  {

    fun getAllSurah(
        stateEvent: StateEvent,
    ): Flow<DataState<MainViewState>>

    fun getAllAyat(
        stateEvent: StateEvent,
    ): Flow<DataState<MainViewState>>

    fun getAllWorlds(
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>>

    fun getAllRacine(
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>>

    fun searchByRacine(
        stateEvent: StateEvent ,
        query : String
    ): Flow<DataState<MainViewState>>

    fun searchAyatByRacine(
        stateEvent: StateEvent ,
        racineId : String
    ): Flow<DataState<MainViewState>>

    fun getAyatTafseer(
        stateEvent: StateEvent ,
        ayat: Ayat,
        tafseerBookId : Int
    ): Flow<DataState<MainViewState>>

    fun getTafseerBook(
        stateEvent: StateEvent
    ): Flow<DataState<MainViewState>>

    fun getAyatId(
        stateEvent: StateEvent ,
        surahId: String ,
        ayatId : Int
    ): Flow<DataState<MainViewState>>

    fun getAllReaders(
        stateEvent: StateEvent ,
    ): Flow<DataState<MainViewState>>

}