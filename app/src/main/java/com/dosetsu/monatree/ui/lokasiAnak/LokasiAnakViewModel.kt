package com.dosetsu.monatree.ui.lokasiAnak

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LokasiAnakViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Lokasi Anak"
    }
    val text: LiveData<String> = _text
}