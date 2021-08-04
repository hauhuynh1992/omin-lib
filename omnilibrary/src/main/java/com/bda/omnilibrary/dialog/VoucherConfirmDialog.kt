package com.bda.omnilibrary.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfButton
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView

@SuppressLint("SetTextI18n")
class VoucherConfirmDialog(
    val activity: Activity,
    val voucher_code: String,
    val onOkClick: () -> Unit,
    val onNoClick: () -> Unit
) {
    private var dialog: AlertDialog? = null

    private var mView =
        activity.layoutInflater.inflate(R.layout.dialog_voucher_comfirm, null) as ViewGroup
    private val title: SfTextView
    private val bnOk: LinearLayout
    private val item_bnOk: MaterialCardView
    private val text_bnOk: SfTextView

    private var mVoucherCode = voucher_code

    private val bnCancel: LinearLayout
    private val item_bnCancel: MaterialCardView
    private val text_bnCancel: SfTextView

    init {
        bnOk = mView.findViewById(R.id.bn_accept)
        item_bnOk = mView.findViewById(R.id.item_bn_accept)
        text_bnOk = mView.findViewById(R.id.text_bn_accept)

        bnOk.setOnClickListener {
            onOkClick.invoke()
            dismiss()
        }

        bnOk.setOnFocusChangeListener { _, hasFocus ->
            text_bnOk.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bnOk.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.color_shadow)
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bnOk.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.text_black_70)
                }
            }
        }

        bnCancel = mView.findViewById(R.id.bn_cancel)
        item_bnCancel = mView.findViewById(R.id.item_bn_cancel)
        text_bnCancel = mView.findViewById(R.id.text_bn_cancel)

        bnCancel.setOnClickListener {
            onNoClick.invoke()
            dismiss()

        }

        bnCancel.setOnFocusChangeListener { _, hasFocus ->
            text_bnCancel.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bnCancel.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.color_shadow)
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bnCancel.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.text_black_70)
                }
            }
        }

        title = mView.findViewById(R.id.title)
        title.text = activity.getString(R.string.current_voucher) + " " + mVoucherCode
        android.os.Handler().postDelayed({
            bnOk.requestFocus()
        }, 100)

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