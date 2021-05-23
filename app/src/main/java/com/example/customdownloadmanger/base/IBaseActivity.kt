package com.example.customdownloadmanger.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class IBaseActivity<VB : ViewBinding, VM : IBaseViewModel> : AppCompatActivity() {

    protected abstract val viewModel: VM
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        setupViews()
        setupObserver()
        setupListener()
    }

    protected abstract fun getViewBinding(): VB

    protected abstract fun setupViews()

    protected abstract fun setupListener()

    open fun goBack() = onBackPressed()

    protected abstract fun setupObserver()

    open fun startDialog(dialogFragment: DialogFragment, addToBackStack: Boolean) {
        try {
            val fm = supportFragmentManager
            while (fm.backStackEntryCount > 0) {
                fm.popBackStackImmediate()
            }
            val ft = fm.beginTransaction()
            if (addToBackStack) {
                ft.addToBackStack(dialogFragment.javaClass.simpleName)
            }
            dialogFragment.show(ft, dialogFragment.javaClass.simpleName)
        } catch (exception: IllegalStateException) {
            Timber.e(exception)
        }
    }
}