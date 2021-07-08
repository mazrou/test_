package com.mazrou.boilerplate.ui.main.state

import com.mazrou.boilerplate.model.ui.Ayat
import com.mazrou.boilerplate.model.ui.Racine
import com.mazrou.boilerplate.model.ui.Tafseer
import com.mazrou.boilerplate.model.ui.TafseerBook


class MainViewState(
    var racineList: List<Racine>? = null ,
    var ayatRacineList:    List<Ayat> ? = null ,
    var selectedAyat : AyatDetails? = null,
    var tafseerBooks : List<TafseerBook>? = null


) //: Parcelable

data class AyatDetails (
    var ayat : Ayat? = null,
    var tafseer: Tafseer? = null
    )


