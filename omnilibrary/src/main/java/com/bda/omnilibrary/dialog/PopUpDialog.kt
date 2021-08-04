package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.ImageView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.PopUpResponse
import com.bda.omnilibrary.util.ImageUtils


class PopUpDialog(
    val activity: Activity,
    val popup: PopUpResponse.ResultPopup,
    val done: () -> Unit,
    val cancel: () -> Unit
) {

    private var dialog: AlertDialog? = null
    private var mView =
        activity.layoutInflater.inflate(R.layout.pop_up_dialog, null) as ViewGroup
    private val image_popup: ImageView

    init {
        image_popup = mView.findViewById(R.id.image_popup)
        ImageUtils.loadImage(
            activity,
            image_popup,
            popup.popup_image,
            ImageUtils.TYPE_PRIVIEW_LAGE
        )

        dialog = AlertDialog.Builder(activity)
            .setOnCancelListener { }
            .create().apply {
                setView(mView)
                setCanceledOnTouchOutside(false)
                show()
                window?.setBackgroundDrawable(ColorDrawable(mView.resources.getColor(R.color.default_background_color)))
                window?.setGravity(Gravity.CENTER)
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

            }

        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    dismiss()
                    done.invoke()
                    true
                } else false

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    cancel.invoke()
                    true
                } else false

            }
            false
        }

    }

    fun dismiss() {

        dialog?.dismiss()
    }
}