package com.bda.omnilibrary.dialog.assitant

import android.app.Activity
import com.bda.omnilibrary.model.AssistantAIResponse
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.model.Product

class AssistantContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendFail(error_code: String, langCode: String)
        fun showLoading()
        fun gotoCart()
        fun loadCart()
        fun gotoInvoice()
        fun gotoDetailProduct(product: Product, isShowQuickPay: Boolean)

        fun showResult(
            products: ArrayList<Product>,
            suggession: ArrayList<String>,
            speechText: String,
            title: String
        )

        fun speakOut(url: String, langCode: String)
        fun instant(): Activity?
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun disposeAPI()
        fun getAssistant(speechText: String)
        fun speechText(text: String)
        fun getDefaultLanguageCode(): String
    }
}
