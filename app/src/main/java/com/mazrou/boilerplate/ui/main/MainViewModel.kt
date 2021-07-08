package com.mazrou.boilerplate.ui.main


import android.util.Log
import com.mazrou.boilerplate.model.database.Reader
import com.mazrou.boilerplate.model.ui.Ayat
import com.mazrou.boilerplate.model.ui.Racine
import com.mazrou.boilerplate.model.ui.Tafseer
import com.mazrou.boilerplate.model.ui.TafseerBook
import com.mazrou.boilerplate.repository.main.Repository
import com.mazrou.boilerplate.ui.BaseViewModel
import com.mazrou.boilerplate.ui.main.state.AyatDetails
import com.mazrou.boilerplate.ui.main.state.MainStateEvent.*
import com.mazrou.boilerplate.ui.main.state.MainViewState
import com.mazrou.boilerplate.util.*
import com.mazrou.boilerplate.util.Constants.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModel(
   private val repository: Repository
)  : BaseViewModel<MainViewState>() {

    override fun handleNewData(data: MainViewState) {
        data.let {
            it.racineList?.let {
                setRacine(it)
            }
            it.ayatRacineList?.let {
                setAyatList(it)
            }
            it.selectedAyat?.let {
                it.tafseer?.let { tafseer ->
                    setTafseer(tafseer)
                }
                it.ayatId?.let { ayatId ->
                    setSelectedAyatIt(ayatId)
                }
            }
            it.tafseerBooks?.let{
                setTafseerBook(it)
            }
            it.readers?.let {
                setReaders(it)
            }
        }
    }

    private fun setTafseerBook(newData: List<TafseerBook>) {
        val update = getCurrentViewStateOrNew()
        if (update.tafseerBooks == newData){
            return
        }
        update.tafseerBooks = newData
        setViewState(update)
    }
    private fun setReaders(newData: List<Reader>) {
        val update = getCurrentViewStateOrNew()
        if (update.readers == newData){
            return
        }
        update.readers = newData
        setViewState(update)
    }

    private fun setRacine(newData : List<Racine>){
        val update = getCurrentViewStateOrNew()
        if (update.racineList == newData){
            return
        }
        update.racineList = newData
        setViewState(update)
    }

    private fun setAyatList(newData : List<Ayat>){
        val update = getCurrentViewStateOrNew()
        if (update.ayatRacineList == newData){
            return
        }
        update.ayatRacineList= newData
        setViewState(update)
    }
    fun setAyat(newData : Ayat){
        val update = getCurrentViewStateOrNew()

        update.selectedAyat?.let{
            if (update.selectedAyat?.ayat == newData){
                return
            }
            it.ayat= newData
            setViewState(update)
        }?: run {
            update.selectedAyat= AyatDetails(ayat = newData)
            setViewState(update)
        }
    }

    private fun setTafseer(newData : Tafseer){
        val update = getCurrentViewStateOrNew()

        update.selectedAyat?.let{
            if (update.selectedAyat?.tafseer == newData){
                return
            }
            it.tafseer= newData
            setViewState(update)
        }?: run {
            update.selectedAyat= AyatDetails(tafseer = newData)
            setViewState(update)
        }
    }

     fun setSelectedAyatIt(newData : Int){
        val update = getCurrentViewStateOrNew()
        Log.e("AppDEbuging", newData.toString())
        update.selectedAyat?.let{
            if (update.selectedAyat?.ayatId == newData){
                return
            }
            it.ayatId= newData
            setViewState(update)
        }?: run {
            update.selectedAyat= AyatDetails(ayatId = newData)
            setViewState(update)
        }
    }

    fun clearRacineSearch(){
        setRacine(emptyList())
    }
    override fun setStateEvent(stateEvent: StateEvent) {

        val job: Flow<DataState<MainViewState>> = when(stateEvent){
            is GetAyatFromNetwork -> {
                repository.getAllAyat(stateEvent = stateEvent)
            }
            is GetSurahFromNetwork -> {
                repository.getAllSurah(stateEvent = stateEvent)
            }
            is GetWorldFromNetwork -> {
                repository.getAllWorlds(stateEvent =stateEvent)
            }
            is GetRacineFromNetwork -> {
                repository.getAllRacine(stateEvent =stateEvent)
            }
            is SearchByRacine ->{
                repository.searchByRacine(
                    stateEvent = stateEvent ,
                    query = stateEvent.query
                )
            }
            is GetAyatRacine ->{
                repository.searchAyatByRacine(
                    stateEvent = stateEvent ,
                    racineId = stateEvent.racineId
                )
            }

            is GetTafseerAyat -> {
                repository.getAyatTafseer(
                    stateEvent =  stateEvent ,
                    ayat = stateEvent.ayat ,
                    tafseerBookId = stateEvent.tafseerId
                )
            }
            is GetReaders -> {
                repository.getAllReaders(
                    stateEvent = stateEvent
                )
            }

            is GetTafseerBooks -> {
                repository.getTafseerBook(
                    stateEvent = stateEvent
                )
            }
            is GetAyatId -> {
                repository.getAyatId(
                    stateEvent = stateEvent ,
                    surahId = stateEvent.surahId ,
                    ayatId = stateEvent.ayatNumber
                )
            }

            else -> {
                flow{
                    emit(
                        DataState.error<MainViewState>(
                            response = Response(
                                message = INVALID_STATE_EVENT,
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Error()
                            ),
                            stateEvent = stateEvent
                        )
                    )
                }
            }
        }
        launchJob(stateEvent, job)
    }
    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }
    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}