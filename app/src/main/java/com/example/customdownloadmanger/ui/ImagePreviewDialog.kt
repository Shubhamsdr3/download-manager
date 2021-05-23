package com.example.customdownloadmanger.ui

import android.view.LayoutInflater
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.customdownloadmanger.base.IBaseDialogFragment
import com.example.customdownloadmanger.databinding.DialogImagePreviewBinding

class ImagePreviewDialog: IBaseDialogFragment<DialogImagePreviewBinding>() {

    companion object {

        private const val IMAGE_URL = "IMAGE_URL"

        fun newInstance(imageUrl: String): ImagePreviewDialog {
            return ImagePreviewDialog().apply {
                arguments = bundleOf(
                    Pair(IMAGE_URL, imageUrl)
                )
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): DialogImagePreviewBinding {
        return DialogImagePreviewBinding.inflate(inflater)
    }

    override fun setupView() {
        arguments?.let {
            val imageUrl = it.getString(IMAGE_URL)
            Glide.with(requireContext()).load(imageUrl).into(binding.downloadImagePreview)
        }
    }

    override fun setupObserver() {

    }
}