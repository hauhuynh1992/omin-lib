package com.bda.omnilibrary.ui.searchActivity

import com.bda.omnilibrary.model.BrandShop
import com.bda.omnilibrary.model.Product

class SearchContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendFail(erroMessage: Int)
        fun sendSuccess(list: ArrayList<Product>, stores: ArrayList<BrandShop>)
        fun sendKeywordSuggest(list: ArrayList<String>)
        fun sendHightlightSuccess(list: ArrayList<Product>)
        fun sendHighlightFail(erroMessage: Int)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun disposeAPI()
        fun callSearchProduct(key: String)
        fun callSuggestKeywords(key: String)
    }
}