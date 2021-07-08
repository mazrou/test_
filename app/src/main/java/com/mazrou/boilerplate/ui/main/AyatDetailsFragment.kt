package com.mazrou.boilerplate.ui.main

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mazrou.boilerplate.R
import com.mazrou.boilerplate.network.ReadQuranWebService
import com.mazrou.boilerplate.ui.main.state.MainStateEvent.GetTafseerAyat
import kotlinx.android.synthetic.main.fragment_ayat_details.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


@ExperimentalCoroutinesApi
@FlowPreview
class AyatDetailsFragment : BaseMainFragment(R.layout.fragment_ayat_details) {


    private var isPlaying : Boolean? = null

    private lateinit  var mediaPlayer :MediaPlayer
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        initView()
        mediaPlayer = MediaPlayer()


    }

    private fun initView() {


        searchable__reader_spinner.setTitle("إختر القارئ");
        searchable__reader_spinner.setPositiveButton("إختر");

        searchable__tafseer_spinner.setTitle("إختر تفسيرا");
        searchable__tafseer_spinner.setPositiveButton("إختر");

        searchable__tafseer_spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.getCurrentViewStateOrNew().tafseerBooks!!.toMutableList()
        )
        Log.e("Ar" ,  viewModel.getCurrentViewStateOrNew().readers.toString() )
        viewModel.getCurrentViewStateOrNew().readers?.let {
            searchable__reader_spinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                viewModel.getCurrentViewStateOrNew().readers!!.toMutableList()
            )
        }

        searchable__tafseer_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setStateEvent(GetTafseerAyat(
                        ayat = viewModel.getCurrentViewStateOrNew().selectedAyat!!.ayat!! ,
                        tafseerId=  viewModel.getCurrentViewStateOrNew().tafseerBooks!![position].id
                     ))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        searchable__reader_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    read(viewModel.getCurrentViewStateOrNew().selectedAyat!!.ayatId!! ,
                    viewModel.getCurrentViewStateOrNew().readers!![position].identifier)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun subscribeObservers(){
       // read (viewModel.getCurrentViewStateOrNew().selectedAyat!!.ayat!!.ayatNumber)
        var khlas = false

        play_btn.setOnClickListener {
            if (!khlas){
                read(viewModel.getCurrentViewStateOrNew().selectedAyat!!.ayatId!!)
                khlas = true
            }

            isPlaying?.let {
                if (it){
                    play_btn.setImageResource(R.drawable.ic_play)
                    mediaPlayer.pause()
                }else{
                    play_btn.setImageResource(R.drawable.ic_pause)
                    mediaPlayer.start()
                }
                isPlaying = !it
            }

        }

        viewModel.viewState.observe(viewLifecycleOwner ,{viewState->
            viewState.selectedAyat?.let {
                surah_name_txt_view.text = "سورة${it.ayat?.surahName}"
                ayat_txt_view.text = it.ayat?.text + " (${it.ayat?.ayatNumber})"
                tafseer_txt_view.text = it.tafseer?.text
                it.ayatId?.let {ayatId->

                }
            }
        })
    }


    private fun read (ayatId : Int , identifier : String = "ar.alafasy"){
        if (ayatId == -1){
            return
        }
        progress_bar.visibility = View.VISIBLE
        play_btn.visibility = View.GONE

        val httpClient = OkHttpClient.Builder()
            .callTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
            .connectTimeout(1000, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(1000, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(1000, java.util.concurrent.TimeUnit.SECONDS)

        val retrofitBuilder =  Retrofit.Builder()
            .baseUrl("http://cdn.alquran.cloud")
            .client(httpClient.build())
            .build()
        val downloadService: ReadQuranWebService =
            retrofitBuilder.create(ReadQuranWebService::class.java)




        val call: Call<ResponseBody> = downloadService.downloadFileWithFixedUrl(ayat = ayatId , reader = identifier )

        call.enqueue(object : Callback<ResponseBody?> {


            override fun onFailure(call: Call<ResponseBody?>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            playMp3(response.body()!!.bytes())
                            progress_bar.visibility = View.GONE
                            play_btn.visibility = View.VISIBLE
                            isPlaying = true
                            mediaPlayer.start()
                            play_btn.setImageResource(R.drawable.ic_pause)

                        }
                    }
                }
        })
    }
    private fun playMp3(mp3SoundByteArray: ByteArray) {

        try {
            val path = File(requireActivity().cacheDir.toString() + "/musicfile.3gp")
            val fos = FileOutputStream(path)
            fos.write(mp3SoundByteArray)
            fos.close()
            isPlaying = false
            val fis = FileInputStream(path)
            mediaPlayer.setDataSource(requireActivity().cacheDir.toString() + "/musicfile.3gp")
            mediaPlayer.prepare()

           mediaPlayer.setOnCompletionListener {
               play_btn.setImageResource(R.drawable.ic_play)
               isPlaying = false
           }

        } catch (ex: Exception) {
            val s = ex.toString()
            ex.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        viewModel.setSelectedAyatIt(-1)
    }
}

