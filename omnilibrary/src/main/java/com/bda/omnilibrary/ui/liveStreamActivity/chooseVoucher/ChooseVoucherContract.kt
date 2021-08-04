package com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher

import com.bda.omnilibrary.model.*

class ChooseVoucherContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendListVoucherSuccess(vouchers: ArrayList<Voucher>)
        fun sendApplyVoucherSuccess(response: BestVoucherForCartResponse)
        fun sendApplyVoucherFalsed(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun checkVoucher(p: Product, code: String)
        fun loadVaucher(uid: String)
        fun disposeAPI()
    }
}