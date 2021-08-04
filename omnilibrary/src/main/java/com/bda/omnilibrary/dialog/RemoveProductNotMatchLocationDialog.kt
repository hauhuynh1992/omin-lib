package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.views.SfTextView

class RemoveProductNotMatchLocationDialog(
    val activity: Activity,
    val data: ArrayList<Product>,
    val province: String,
    private val clickChangeAddress: () -> Unit
) {
    private var dialog: AlertDialog? = null

    private var mView =
        activity.layoutInflater.inflate(
            R.layout.dialog_remove_product_not_match_location,
            null
        ) as ViewGroup

    private var name: SfTextView

    private var bn_cancel: LinearLayout
    private var text_bn_cancel: SfTextView

    private var bn_accept: LinearLayout
    private var text_bn_accept: SfTextView

    init {
        name = mView.findViewById(R.id.product_name)

        bn_cancel = mView.findViewById(R.id.bn_cancel)
        text_bn_cancel = mView.findViewById(R.id.text_bn_cancel)

        bn_accept = mView.findViewById(R.id.bn_accept)
        text_bn_accept = mView.findViewById(R.id.text_bn_accept)

        name.text = data[0].name

        dialog = AlertDialog.Builder(activity)
            .setOnCancelListener { }
            .create().apply {
                setView(mView)
                setCanceledOnTouchOutside(false)
                show()

                window?.setBackgroundDrawable(ColorDrawable(mView.resources.getColor(R.color.default_background_color)))
                window?.setGravity(Gravity.CENTER)
                window?.setLayout(
                    activity.resources.getDimensionPixelSize(R.dimen._230sdp),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

        /*dialog?.setOnKeyListener { dialog, keyCode, event ->

            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP ->
                        if (vg_alt_address.size > 0 && vg_alt_address[0].hasFocus()) {
                            bn_back.requestFocus()
                        }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {

                    }
                }
            }

            return@setOnKeyListener false
        }*/


        bn_cancel.setOnFocusChangeListener { view, hasFocus ->
            text_bn_cancel.isSelected = hasFocus
        }

        bn_accept.setOnFocusChangeListener { view, hasFocus ->
            text_bn_accept.isSelected = hasFocus
        }

        bn_accept.setOnClickListener {
            dismiss()
            clickChangeAddress.invoke()
        }

        bn_cancel.setOnClickListener {
            dismiss()
        }

        focusNew()
    }

    private fun focusNew() {
        Handler().postDelayed({
            bn_accept.requestFocus()
        }, 0)
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}