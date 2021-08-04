package com.bda.omnilibrary.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.ImageUtils
import kotlinx.android.synthetic.main.activity_landing_page.*

class LandingPageDialog : DialogFragment() {

    private var url: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_landing_page, container).apply {
            url = arguments?.getString("URL_LANDING")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        url?.let {
            ImageUtils.loadImage(requireActivity(), img_landing, url, ImageUtils.TYPE_LANDING)

        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            // Set gravity of dialog
            dialog.setCanceledOnTouchOutside(true)
            val window = dialog.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.CENTER
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = wlp
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val lp = window.attributes
            lp.dimAmount = 0.6f
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dialog.window?.setWindowAnimations(R.style.DialogAnimation)

            dialog.window?.attributes = lp
            dialog.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        dismiss()
                    }
                }
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (MainActivity.getMaiActivity() != null && !MainActivity.getMaiActivity()!!
                .checkIsPlayingBackground()
        ) {
            MainActivity.getMaiActivity()?.playMusicBackground()
        }
    }
}
