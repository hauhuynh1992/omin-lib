package com.bda.omnilibrary.ui.orderDetail

import com.bda.omnilibrary.model.*

class OrderContract {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendListVoucherSuccess(vouchers: VoucherResponse)
        fun sendProfileSuccess(contact: ContactInfo?)
        fun sendProfileFalsed(message: String)
        fun sendVoucherSuccess(response: BestVoucherForCartResponse)
        fun sendVoucherFalsed(message: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadVoucher(request: VoucherRequest)
        fun checkVoucher(list: ArrayList<Product>, voucher:String)
        fun disposeAPI()
        fun fetchProfile(uid: String)

    }
}