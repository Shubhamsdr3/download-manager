package com.example.customdownloadmanger.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.customdownloadmanger.App
import java.lang.RuntimeException

class DownloadViewModelFactory(private val app: App): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
            return DownloadViewModel(app) as T
        }
        throw RuntimeException("wrong viewmodel type")
    }
}