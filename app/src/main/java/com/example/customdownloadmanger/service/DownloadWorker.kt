package com.example.customdownloadmanger.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.customdownloadmanger.R
import com.example.customdownloadmanger.util.AppConstants.TAG
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


const val DOWNLOAD_FILE_PATH = "DOWNLOAD_FILE_PATH"
const val DOWNLOAD_URL = "DOWNLOAD_URL"
const val PROGRESS = "PROGRESS"

class DownloadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        val downloadFilePath = inputData.getString(DOWNLOAD_FILE_PATH)
        val downloadUrl = inputData.getString(DOWNLOAD_URL)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var urlConnection: HttpURLConnection? = null

        try {
            val url = URL(downloadUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.doOutput = true
            urlConnection.connect()

            if (urlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(
                    TAG,
                    " Download failed.......${urlConnection.responseCode} ${urlConnection.responseMessage}"
                )
                return Result.failure()
            }

//            val progress = "Starting Download"
//            setForegroundAsync(createForegroundInfo(progress))
            setProgress(workDataOf(PROGRESS to 0))
            val fileLength = urlConnection.contentLength
            Log.d(TAG, " Download loading.......${fileLength}")
            inputStream = BufferedInputStream(urlConnection.inputStream)
            outputStream = FileOutputStream(downloadFilePath)
            val byteArray = ByteArray(1024)
            var total = 0
            var count: Int
            var progress = 0
            while (inputStream.read(byteArray).also { count = it } != -1) {
                total += count
                Log.d(TAG, " Download loading.......${count}")
                outputStream.write(byteArray, 0, count)
                progress++
                setProgress(workDataOf(PROGRESS to progress))
            }
            outputStream.flush()
            Log.d(TAG, " Download successful.......")
            setProgress(workDataOf(PROGRESS to 100))
            return Result.success()
        } catch (exc: IOException) {
            exc.printStackTrace()
            Log.e(TAG, "Download failed.......${exc.message}")
            return Result.failure()
        } finally {
            inputStream?.close()
            outputStream?.close()
            urlConnection?.disconnect()
        }
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val channelId = applicationContext.getString(R.string.notification_channel_id)
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Custom Download manager")
            .setTicker("Custom Download manager")
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Cancel", intent)
            .build()
        val uniqueId = Date().time.toInt()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "channelName", NotificationManager.IMPORTANCE_NONE)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(uniqueId, notification)
        return ForegroundInfo(uniqueId, notification)
    }
}