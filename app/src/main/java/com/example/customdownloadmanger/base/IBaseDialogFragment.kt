package com.example.customdownloadmanger.base

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class IBaseDialogFragment<VB: ViewBinding>: DialogFragment() {

    lateinit var binding: VB

    /**
     * This is used to calculate the width of dialog.
     * override according to your use case.
     */
    open var dialogWidth = 0.75

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //this is used to show the rounded corner of dialog.
        //it will make your dialog look transparent, always set the background color to your xml layout.
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        binding = getViewBinding(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListener()
        setupObserver()
    }

    override fun onResume() {
        val window: Window? = dialog!!.window
        val size = Point()
        val display: Display? = window?.windowManager?.defaultDisplay
        display?.getSize(size)
        window?.setLayout((size.x * dialogWidth).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        super.onResume()
    }

    abstract fun getViewBinding(inflater: LayoutInflater): VB

    protected abstract fun setupView()

    protected open fun setupListener() {}

    protected abstract fun setupObserver()
}