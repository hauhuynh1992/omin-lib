package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView

class RemainQrDialog(
    val activity: Activity,
    val time: String,
    val yes: () -> Unit
) {

    private var dialog: AlertDialog? = null

    private var mView =
        activity.layoutInflater.inflate(R.layout.dialog_remain_qr, null) as ViewGroup
    private val bn_yes: LinearLayout

    private val title: SfTextView
    private val item_bn_accept: MaterialCardView
    private val text_bn_accept: SfTextView

    init {
        bn_yes = mView.findViewById(R.id.bn_yes)
        title = mView.findViewById(R.id.lb_quantity)

        item_bn_accept = mView.findViewById(R.id.item_bn_accept)
        text_bn_accept = mView.findViewById(R.id.text_bn_accept)

        dialog = AlertDialog.Builder(activity)
            .setOnCancelListener { }
            .create().apply {
                setView(mView)
                setCanceledOnTouchOutside(false)
                show()
                window?.setBackgroundDrawable(ColorDrawable(mView.resources.getColor(R.color.default_background_color)))
                window?.setGravity(Gravity.CENTER)
                window?.setLayout(
                    activity.resources.getDimensionPixelSize(R.dimen._264sdp),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

        dialog!!.setCancelable(false)

        bn_yes.setOnClickListener {
            yes.invoke()
            dismiss()
        }

        bn_yes.setOnFocusChangeListener { _, hasFocus ->
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

        Handler().postDelayed({
            bn_yes.requestFocus()
        }, 100)

    }

    fun updateTime(s: String) {
        title.text = s//activity.resources.getString(R.string.title_dialog_remain_qr, s)
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}