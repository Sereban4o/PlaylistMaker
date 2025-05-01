package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") expression: String): Call<SearchResponse>
}