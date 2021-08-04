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
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView

class VoucherPopupDialog(
    val activity: Activity
) {
    private var dialog: AlertDialog? = null

    private var mView =
        activity.layoutInflater.inflate(R.layout.dialog_voucher_popup, null) as ViewGroup
    private val bnCancel: LinearLayout
    private val item_bn_cancel: MaterialCardView
    private val text_bn_cancel: SfTextView

    init {
        bnCancel = mView.findViewById(R.id.bn_cancel)
        item_bn_cancel = mView.findViewById(R.id.item_bn_cancel)
        text_bn_cancel = mView.findViewById(R.id.text_bn_cancel)

        bnCancel.setOnClickListener {
            dismiss()
        }

        bnCancel.setOnFocusChangeListener { _, hasFocus ->
            text_bn_cancel.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_cancel.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.color_shadow)
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_cancel.outlineSpotShadowColor =
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

                window?.setBackgroundDrawable(ColorDrawable(mView.resources.getColor(R.color.default_background_color)))
                window?.setGravity(Gravity.CENTER)
                window?.setLayout(
                    activity.resources.getDimensionPixelSize(R.dimen._244sdp),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}