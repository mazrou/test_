package com.mazrou.boilerplate.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mazrou.boilerplate.model.database.Reader
import com.mazrou.boilerplate.network.network_response.ReaderResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming


interface ReadQuranWebService {

    @GET("/media/audio/ayah/{reader}/{ayat}")
    @Streaming
    fun downloadFileWithFixedUrl(
        @Path("ayat") ayat : Int ,
        @Path("reader") reader : String = "ar.alafasy"
    ): Call<ResponseBody>


    @GET("/edition/format/audio")
    suspend fun getAllReaders(
    ): ReaderResponse


    companion object {

        fun invoke(): ReadQuranWebService {

            val gson: Gson = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val loggingHeader = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.HEADERS

            val httpClient = OkHttpClient.Builder()
                .callTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
                .connectTimeout(1000, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(1000, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(1000, java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(loggingHeader)


            return Retrofit.Builder()
                .baseUrl("http://api.alquran.cloud")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ReadQuranWebService::class.java)
        }
    }

}