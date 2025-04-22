package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private val service = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TrackApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
                val resp = service.searchTracks(dto.expression).execute()

                val body = resp.body() ?: Response()

                return body.apply { isSuccessful = true }

        } else {
            return Response().apply { isSuccessful }
        }
    }

}