package com.example.customdownloadmanger

import android.Manifest
import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.example.customdownloadmanger.base.IBaseActivity
import com.example.customdownloadmanger.databinding.ActivityMainBinding
import com.example.customdownloadmanger.service.PROGRESS
import com.example.customdownloadmanger.ui.DownloadProgressDialog
import com.example.customdownloadmanger.ui.ImagePreviewDialog
import com.example.customdownloadmanger.util.DownloadViewModel
import com.example.customdownloadmanger.util.DownloadViewModelFactory
import com.example.customdownloadmanger.util.hide
import com.example.customdownloadmanger.util.show
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*


private const val WRITE_REQUEST_PERMISSION_CODE = 111

class MainActivity : IBaseActivity<ActivityMainBinding, DownloadViewModel>() , DownloadProgressDialog.DownloadProgressDialogListener {

    private var downloadFileName: String = ""

    override val viewModel: DownloadViewModel by lazy {
        ViewModelProvider(this, DownloadViewModelFactory(App.getInstance())).get(DownloadViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.downloadButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_REQUEST_PERMISSION_CODE
                )
            } else {
                startDownload()
            }
        }
    }

    @Throws(IOException::class)
    private fun startDownload() {
        val file = createDownloadLocalFile() ?: return
        binding.downloadProgress.show()
        val workStatus: LiveData<WorkInfo> = viewModel.startDownload(
            file.absolutePath,
            "https://www.businessinsider.in/thumb.cms?msid=74095911&width=1200&height=900"
        )
        workStatus.observe(this, {
            val progress = it.progress.getInt(PROGRESS, 0)
            binding.downloadProgress.progress = progress
            if (progress == 100) {
                binding.downloadProgress.hide()
                showDownloadedSnackBar()
            }
        })
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun setupViews() {}

    override fun setupListener() {}

    override fun setupObserver() {}

    private fun createDownloadLocalFile(): File? {
        var ret: File? = null
        try {
            val downloadDirectory = applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val downloadDirectoryPath = downloadDirectory?.path
            val date = Date().time
            downloadFileName = "$downloadDirectoryPath/${date}_google_image.png"
            ret = File(downloadFileName)
            if (!ret.exists()) {
                ret.createNewFile()
            }
        } catch (ex: IOException) {
            Timber.e(ex)
        } finally {
            return ret
        }
    }

    private fun showDownloadedSnackBar() {
        Snackbar.make(binding.root, "Download completed..", Snackbar.LENGTH_LONG)
            .setAction("SHOW") {
                startDialog(
                    ImagePreviewDialog.newInstance(downloadFileName),
                    false
                )
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.holo_red_light))
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_REQUEST_PERMISSION_CODE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDownload()
        } else {
            showMessageOKCancel(getString(com.example.customdownloadmanger.R.string.permission_request)) { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_REQUEST_PERMISSION_CODE
                    )
                } else {
                    finish()
                }
            }
        }
    }

    private fun showMessageOKCancel(message: String, listener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(com.example.customdownloadmanger.R.string.btn_ok), listener)
            .setNegativeButton(getString(com.example.customdownloadmanger.R.string.skip), listener)
            .create()
            .show()
    }

    override fun onCancelClicked() {}

    override fun onPauseClicked() {}
}