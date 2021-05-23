package com.example.customdownloadmanger.ui

import android.content.Context
import android.view.LayoutInflater
import com.example.customdownloadmanger.base.IBaseDialogFragment
import com.example.customdownloadmanger.databinding.DialogDownloadProgressBinding
import java.lang.RuntimeException

class DownloadProgressDialog: IBaseDialogFragment<DialogDownloadProgressBinding>() {

    private var downloadProgressDialogListener: DownloadProgressDialogListener? = null

    companion object {

        fun newInstance(): DownloadProgressDialog {
            return DownloadProgressDialog()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        downloadProgressDialogListener =
            if (parentFragment != null && parentFragment is DownloadProgressDialogListener) {
            parentFragment as DownloadProgressDialogListener
        } else if(context is DownloadProgressDialogListener) {
            context
        } else {
            throw RuntimeException("$context must implement fragment context")
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): DialogDownloadProgressBinding {
        return DialogDownloadProgressBinding.inflate(inflater)
    }

    override fun setupView() {
        binding.cancelButton.setOnClickListener {
            downloadProgressDialogListener?.onCancelClicked()
        }

        binding.pauseButton.setOnClickListener {
            downloadProgressDialogListener?.onPauseClicked()
        }
    }

    override fun setupObserver() {}

    interface DownloadProgressDialogListener {

        fun onCancelClicked()

        fun onPauseClicked()
    }
}