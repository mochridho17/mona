package com.dosetsu.monatree.ui.homeAnak

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeAnakViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home anak Fragment"
    }
    val text: LiveData<String> = _text
}