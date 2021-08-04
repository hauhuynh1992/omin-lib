package com.bda.omnilibrary.ui.cartActivity

import com.bda.omnilibrary.model.*

class CartContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendEditFalsed(message: Int)
        fun sendHightlightSuccess(list: ArrayList<Product>)
        fun sendHighlightFail(erroMessage: Int)

        fun sendProfileSuccess(profile: CustomerProfileResponse)
        fun sendProfileFalsed(message: String)

        fun sendListVoucherSuccess(vouchers: VoucherResponse)
        fun sendApplyVoucherSuccess(response: BestVoucherForCartResponse)
        fun sendApplyVoucherFalsed(message: String)

        fun sendApplyVoucherSuccessContinue(response: BestVoucherForCartResponse)
        fun sendApplyVoucherFalsedContinue(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun disposeAPI()
        fun callHightlight()
        fun fetchProfile(uid: String)

        fun updateCartOnline(cardRequest: CartRequest)
        fun loadVoucher(uid: String)
        fun checkVoucher(list: ArrayList<Product>, voucher:String)
        fun checkVoucherForContinueShopping(list: ArrayList<Product>, voucher:String)
        fun sendListVoucherSuccess(vouchers: VoucherResponse)

    }

}