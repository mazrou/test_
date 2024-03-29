package com.mazrou.boilerplate.ui.main.state

import com.mazrou.boilerplate.model.ui.Ayat
import com.mazrou.boilerplate.model.ui.Racine
import com.mazrou.boilerplate.util.StateEvent

sealed class MainStateEvent : StateEvent {

    class GetAyatFromNetwork: MainStateEvent()

    class GetSurahFromNetwork: MainStateEvent()

    class GetWorldFromNetwork(): MainStateEvent()

    class GetRacineFromNetwork(): MainStateEvent()

    data class SearchByRacine(
        val query : String = ""
    ): MainStateEvent()

    class GetAyatRacine(
        val racineId : String
    ): MainStateEvent()

    class GetTafseerAyat(
        val ayat : Ayat ,
        val tafseerId : Int = 1
    ): MainStateEvent()

    class GetTafseerBooks(
    ): MainStateEvent()

  class GetReaders(
    ): MainStateEvent()

    class GetAyatId(
        val surahId : String ,
        val ayatNumber : Int
    ): MainStateEvent()


    class None: MainStateEvent()
}