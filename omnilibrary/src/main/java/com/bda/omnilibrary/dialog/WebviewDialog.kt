package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.webkit.WebView
import com.bda.omnilibrary.R

class WebviewDialog(
    val activity: Activity,
    val link: String
) {

    private var dialog: AlertDialog? = null

    private var mView =
        activity.layoutInflater.inflate(R.layout.webview_dialog, null) as ViewGroup
    private val wb_content: WebView


    init {
        wb_content = mView.findViewById(R.id.wb_content)
        wb_content.loadUrl(link)
        dialog = AlertDialog.Builder(activity)
            .setOnCancelListener { }
            .create().apply {
                setView(mView)
                setCanceledOnTouchOutside(false)
                show()
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        dialog!!.window!!.setDimAmount(0f)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0));
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}