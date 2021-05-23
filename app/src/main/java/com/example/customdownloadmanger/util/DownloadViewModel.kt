package com.example.customdownloadmanger.util

import androidx.lifecycle.LiveData
import androidx.work.*
import com.example.customdownloadmanger.App
import com.example.customdownloadmanger.base.IBaseViewModel
import com.example.customdownloadmanger.service.DOWNLOAD_FILE_PATH
import com.example.customdownloadmanger.service.DOWNLOAD_URL
import com.example.customdownloadmanger.service.DownloadWorker

class DownloadViewModel(app: App) : IBaseViewModel(app) {

    private val workManager = WorkManager.getInstance(app)

    fun startDownload(filePath: String, downloadUrl: String): LiveData<WorkInfo> {
        val workData = workDataOf(
            DOWNLOAD_FILE_PATH to filePath,
            DOWNLOAD_URL to downloadUrl
        )
        val work = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(workData)
            .build()
        workManager.enqueue(work)
        return workManager.getWorkInfoByIdLiveData(work.id)
    }
}