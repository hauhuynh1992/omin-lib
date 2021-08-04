package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfHtmlTextView
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView

class PaymentErrorDialog(
    val activity: Activity,
    stringResourceId: Int = -1,
    message: String = "",
    private val onOkClick: () -> Unit
) {
    private var dialog: AlertDialog? = null

    private var mView =
        activity.layoutInflater.inflate(R.layout.dialog_comfirm_info, null) as ViewGroup

    private val bnOk: LinearLayout
    private val item_bn_accept: MaterialCardView
    private val text_bn_accept: SfTextView

    private val description: SfHtmlTextView

    init {
        description = mView.findViewById(R.id.description)
        bnOk = mView.findViewById(R.id.bn_accept)
        item_bn_accept = mView.findViewById(R.id.item_bn_accept)
        text_bn_accept = mView.findViewById(R.id.text_bn_accept)

        if (stringResourceId != -1) {
            description.setText(stringResourceId)
        } else if (message != "") {
            description.setText(message)
        }

        bnOk.setOnClickListener {
            onOkClick.invoke()
            dismiss()
        }

        bnOk.setOnFocusChangeListener { _, hasFocus ->
            text_bn_accept.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_accept.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.color_shadow)
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_accept.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.text_black_70)
                }
            }
        }

        dialog = AlertDialog.Builder(activity)
            .setOnCancelListener { }
            .create().apply {
                setView(mView)
                setCanceledOnTouchOutside(false)
                show()

                window?.setBackgroundDrawable(ColorDrawable(mView.resources.getColor(R.color.color_transparent)))
                window?.setGravity(Gravity.CENTER)
                window?.setLayout(
                    activity.resources.getDimensionPixelSize(R.dimen._240sdp),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}