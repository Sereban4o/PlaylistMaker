package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
    suspend fun isConnected(): Boolean
}