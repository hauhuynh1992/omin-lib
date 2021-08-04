package com.bda.omnilibrary.ui.promotionDetail

import com.bda.omnilibrary.model.Promotion

class PromotionDetailContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSuccess(promotion: Promotion)
        fun sendFalsed(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun getPromotion(
            id: String
        )

        fun disposeAPI()

    }
}