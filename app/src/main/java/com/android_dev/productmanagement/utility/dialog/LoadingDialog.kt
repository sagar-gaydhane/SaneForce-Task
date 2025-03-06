package com.android_dev.productmanagement.utility.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.android_dev.productmanagement.R
import com.android_dev.productmanagement.databinding.LoadingDialogBinding

class LoadingDialog(context: Context) : AlertDialog(context)  {

    private lateinit var binding: LoadingDialogBinding
    private lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoadingDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.CENTER)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        animationView = binding.animationView
        animationView.setAnimation(R.raw.loading_animation)
        animationView.playAnimation()
        animationView.loop(true)


    }

}