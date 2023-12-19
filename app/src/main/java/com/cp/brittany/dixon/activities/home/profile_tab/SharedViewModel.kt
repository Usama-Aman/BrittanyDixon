package com.cp.brittany.dixon.activities.home.profile_tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel : ViewModel() {
    private val selected: MutableLiveData<String> = MutableLiveData<String>()
    private val observeApiResponse: MutableLiveData<String> = MutableLiveData<String>()
    fun select(item: String) {
        selected.value = item
    }

    fun getSelected(): LiveData<String> {
        return selected
    }

    fun setResponse(item: String) {
        observeApiResponse.value = item
    }

    fun getApiResponse(): LiveData<String> {
        return observeApiResponse
    }
}