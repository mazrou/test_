package com.mazrou.boilerplate.repository.main


import android.util.Log
import com.mazrou.boilerplate.model.database.*
import com.mazrou.boilerplate.model.ui.Ayat
import com.mazrou.boilerplate.model.ui.Racine
import com.mazrou.boilerplate.model.ui.Tafseer
import com.mazrou.boilerplate.model.ui.TafseerBook
import com.mazrou.boilerplate.network.ReadQuranWebService
import com.mazrou.boilerplate.network.TafseerWebService
import com.mazrou.boilerplate.network.WebService
import com.mazrou.boilerplate.network.network_response.ReaderResponse
import com.mazrou.boilerplate.perssistance.QuranDao
import com.mazrou.boilerplate.repository.NetworkBoundResource
import com.mazrou.boilerplate.repository.safeApiCall
import com.mazrou.boilerplate.repository.safeCacheCall
import com.mazrou.boilerplate.ui.main.state.AyatDetails
import com.mazrou.boilerplate.ui.main.state.MainViewState
import com.mazrou.boilerplate.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class RepositoryImpl(
    private val webService: WebService,
    val quranDao: QuranDao,
    val readQuranWebService: ReadQuranWebService,
    val tafseerWebService: TafseerWebService
) : Repository {

    private val TAG: String = "AppDebug"

    override fun getAllSurah(
        stateEvent: StateEvent,
    ): Flow<DataState<MainViewState>> = flow {

        val apiResult = safeApiCall(IO) {
            webService.getAllSurah()
        }
        emit(
            object : ApiResponseHandler<MainViewState, List<Surah>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<Surah>): DataState<MainViewState> {

                    CoroutineScope(IO).launch {
                        for (surah in resultObj) {
                            launch {
                                try {
                                    quranDao.insertSurah(surah)
                                } catch (e: Exception) {
                                    Log.e(TAG, "error on inserting surah ${surah.surahName}")

                                }
                            }
                        }
                    }
                    return DataState.data(
                        data = MainViewState(),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun getAllAyat(stateEvent: StateEvent): Flow<DataState<MainViewState>>  =flow {

        val apiResult = safeApiCall(IO) {
            webService.getAllAyat()
        }
        emit(
            object : ApiResponseHandler<MainViewState, List<AyatModel>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<AyatModel>): DataState<MainViewState> {
                    CoroutineScope(IO).launch {
                        for (ayat in resultObj) {
                            launch {
                                try {
                                    quranDao.insertAyat(ayat)
                                } catch (e: Exception) {
                                    Log.e(TAG, "error on inserting ayat ${ayat.id}")
                                }
                            }
                        }
                    }
                    return DataState.data(
                        data = MainViewState(),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun getAllWorlds(stateEvent: StateEvent) =flow {

        val apiResult = safeApiCall(IO) {
            webService.getAllWorlds()
        }
        emit(
            object : ApiResponseHandler<MainViewState, List<World>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<World>): DataState<MainViewState> {
                    for (world in resultObj) {
                        try {
                            quranDao.insertWorld(world)
                        } catch (e: Exception) {
                            Log.e(TAG, "error on inserting word ${world.id}")
                        }
                    }
                    return DataState.data(
                        data = MainViewState(),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }


    override fun getAllRacine(stateEvent: StateEvent) =flow {

        val apiResult = safeApiCall(IO) {
            webService.getAllRacine()
        }
        emit(
            object : ApiResponseHandler<MainViewState, List<RacineModel>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: List<RacineModel>): DataState<MainViewState> {
                   CoroutineScope(IO).launch {
                       for (racine in resultObj) {
                           launch {
                               try {
                                   quranDao.insertRacine(racine)
                               } catch (e: Exception) {
                                   Log.e(TAG, "error on inserting racine ${racine.id}")
                               }
                           }
                       }
                   }
                    return DataState.data(
                        data = MainViewState(),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun searchByRacine(
        stateEvent: StateEvent,
        query: String
    ): Flow<DataState<MainViewState>> = flow{
         val cacheRequest =  safeCacheCall(IO){
             quranDao.searchByRacineUi(query)
         }
        emit(
            object : CacheResponseHandler<MainViewState , List<Racine>?>(
                stateEvent = stateEvent ,
                response = cacheRequest
            ){
                override suspend fun handleSuccess(resultObj: List<Racine>?): DataState<MainViewState> {
                    return DataState.data(
                        data = MainViewState(
                            racineList =  resultObj
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun searchAyatByRacine(
        stateEvent: StateEvent,
        racineId: String
    ): Flow<DataState<MainViewState>> = flow{
        val cacheRequest =  safeCacheCall(IO){
            quranDao.searchAyatByRacine(racineId)
        }
        emit(
            object : CacheResponseHandler<MainViewState , List<Ayat>?>(
                stateEvent = stateEvent ,
                response = cacheRequest
            ){
                override suspend fun handleSuccess(resultObj: List<Ayat>?): DataState<MainViewState> {

                    return DataState.data(
                        data = MainViewState(
                            ayatRacineList =  resultObj
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )

    }

    override fun getAyatTafseer(
        stateEvent: StateEvent,
        ayat: Ayat ,
        tafseerBookId: Int
    ): Flow<DataState<MainViewState>> = flow{
        val webRequest =  safeApiCall(IO){
            tafseerWebService.getAyatTafseer(
                tafseerId = tafseerBookId ,
                surahNumber = ayat.idSurah,
                ayatNumber = ayat.ayatNumber
            )
        }
        emit(
            object : ApiResponseHandler<MainViewState ,Tafseer >(
                stateEvent = stateEvent ,
                response = webRequest
            ){
                override suspend fun handleSuccess(resultObj: Tafseer): DataState<MainViewState> {
                    return DataState.data(
                        data = MainViewState(
                            selectedAyat = AyatDetails(tafseer = resultObj)
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )

    }

    @FlowPreview
    override fun getTafseerBook(stateEvent: StateEvent): Flow<DataState<MainViewState>> {
        return object :NetworkBoundResource<List<TafseerBook> ,List<TafseerBook> ,MainViewState >(
            dispatcher = IO ,
            stateEvent = stateEvent ,
            apiCall = {tafseerWebService.getAllTafseer()} ,
            cacheCall = {quranDao.getAllTafseerBook()}
        ){
            override suspend fun updateCache(networkObject: List<TafseerBook>) {
              CoroutineScope(IO).launch {
                  for (item in networkObject){
                        launch {
                            quranDao.insertTafseerBook(tafseerBook = item)
                        }
                  }

              }
            }
            override fun handleCacheSuccess(resultObj: List<TafseerBook>): DataState<MainViewState> {
                return DataState.data(
                    data = MainViewState(
                        tafseerBooks = resultObj
                    ),
                    stateEvent = stateEvent,
                    response = null
                )
            }

        }.result
    }

    override fun getAyatId(
        stateEvent: StateEvent,
        surahId: String,
        ayatId: Int
    ): Flow<DataState<MainViewState>> = flow{

        val webRequest =  safeCacheCall(IO){
            quranDao.getAyatId(
                surahId = surahId.toInt()
            )
        }
        emit(
            object : CacheResponseHandler<MainViewState ,Int >(
                stateEvent = stateEvent ,
                response = webRequest
            ){
                override suspend fun handleSuccess(resultObj: Int): DataState<MainViewState> {
                    Log.e(TAG , "hada surah = ${surahId} o hada aya = $ayatId = ${resultObj + ayatId}" )
                    return DataState.data(
                        data = MainViewState(
                            selectedAyat = AyatDetails(ayatId = resultObj + ayatId)
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun getAllReaders(stateEvent: StateEvent): Flow<DataState<MainViewState>> {

        return object :NetworkBoundResource<ReaderResponse ,List<Reader> ,MainViewState >(
            dispatcher = IO ,
            stateEvent = stateEvent ,
            apiCall = {readQuranWebService.getAllReaders()} ,
            cacheCall = {quranDao.getAllReaders()}
        ){
            override suspend fun updateCache(networkObject: ReaderResponse) {
                CoroutineScope(IO).launch {
                    for (item in networkObject.data){
                        launch {
                            quranDao.insertReader(reader = item)
                        }
                    }
                }
            }
            override fun handleCacheSuccess(resultObj: List<Reader>): DataState<MainViewState> {
                return DataState.data(
                    data = MainViewState(
                        readers = resultObj
                    ),
                    stateEvent = stateEvent,
                    response = null
                )
            }

        }.result
    }
}

