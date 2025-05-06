package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel() : ViewModel() {

    private val stateData = MutableLiveData<Any>()
    fun observeState(): LiveData<Any> = stateData
}