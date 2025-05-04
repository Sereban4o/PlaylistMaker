package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.utils.SingleEventLiveData


class TabViewModel(number: Int): ViewModel() {

    private val currentTab = SingleEventLiveData<Int>()

    init {
        currentTab.value = number
    }

    fun getCurrentTab(): LiveData<Int> = currentTab


}