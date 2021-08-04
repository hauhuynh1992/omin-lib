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

class ConfirmDialog(
    val activity: Activity,
    val title: String,
    val yes: () -> Unit,
    val no: () -> Unit
) {

    private var dialog: AlertDialog? = null

    private var mView =
        activity.layoutInflater.inflate(R.layout.dialog_comfirm, null) as ViewGroup
    private val bn_no: LinearLayout
    private val item_bn_no: MaterialCardView
    private val text_bn_no: SfTextView

    private val bn_yes: LinearLayout
    private val item_bn_yes: MaterialCardView
    private val text_bn_yes: SfTextView

    private val mTitle: SfTextView

    init {
        bn_no = mView.findViewById(R.id.bn_no)
        bn_yes = mView.findViewById(R.id.bn_yes)

        item_bn_no = mView.findViewById(R.id.item_bn_no)
        text_bn_no = mView.findViewById(R.id.text_bn_no)

        item_bn_yes = mView.findViewById(R.id.item_bn_yes)
        text_bn_yes = mView.findViewById(R.id.text_bn_yes)

        mTitle = mView.findViewById(R.id.title)

        mTitle.text = title

        bn_yes.setOnClickListener {
            yes.invoke()
            dismiss()
        }

        bn_no.setOnFocusChangeListener { _, hasFocus ->
            text_bn_no.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_no.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.color_shadow)
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_no.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.text_black_70)
                }
            }
        }

        bn_no.setOnClickListener {
            no.invoke()
            dismiss()
        }

        bn_yes.setOnFocusChangeListener { _, hasFocus ->
            text_bn_yes.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_yes.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.color_shadow)
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_yes.outlineSpotShadowColor =
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

        bn_yes.requestFocus()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}