package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bda.omnilibrary.R


class ProductDescriptionDialog(
    val activity: Activity,
    val description: String,
) {

    private var dialog: AlertDialog? = null
    private var webview: WebView? = null
    private var mView =
        activity.layoutInflater.inflate(R.layout.product_description_dialog, null) as ViewGroup


    init {
        webview = mView.findViewById(R.id.webview)
        webview?.settings?.javaScriptEnabled = true;
        webview?.settings?.loadWithOverviewMode = true;
        webview?.settings?.useWideViewPort = true;
        webview?.settings?.defaultFontSize = activity.resources.getDimension(R.dimen._7ssp).toInt()
        webview?.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                webview?.loadUrl("javascript:document.body.style.margin=\"8%\"; void 0")
            }
        })
        webview?.loadData(description, "text/html; charset=utf-8", "UTF-8");
        dialog = AlertDialog.Builder(activity)
            .setOnCancelListener { }
            .create().apply {
                setView(mView)
                setCanceledOnTouchOutside(false)
                show()
                //window?.setBackgroundDrawable(activity.resources.getDrawable(R.mipmap.bg_transparent))
                //window?.setBackgroundDrawable(ColorDrawable(mView.resources.getColor(R.color.title_white_transparent_20)))
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        dialog?.window?.setDimAmount(0f)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0))
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}