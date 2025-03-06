package com.android_dev.productmanagement.utility.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.android_dev.productmanagement.R
import com.android_dev.productmanagement.databinding.DialogCustomBinding


class CustomDialog(context: Context, private val dialogType: DialogType ,private val message: String) : Dialog(context) {

    private lateinit var binding: DialogCustomBinding
    private lateinit var animationView: LottieAnimationView

    enum class DialogType {
        SUCCESS, FAILURE, NETWORK_FAILURE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogCustomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
//        window?.setBackgroundDrawableResource(R.color.)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.CENTER)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        animationView = binding.animationView

        when (dialogType) {
            DialogType.SUCCESS -> {
                animationView.setAnimation(R.raw.success_animation)
                binding.textViewMessage.text = message
            }
            DialogType.FAILURE -> {
                animationView.setAnimation(R.raw.fail_to_load_animation)
                binding.textViewMessage.text = message
            }
            DialogType.NETWORK_FAILURE -> {
                animationView.setAnimation(R.raw.network_error_animation)
                binding.textViewMessage.text = message
            }
        }

        animationView.playAnimation()
        animationView.loop(false)

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
}
