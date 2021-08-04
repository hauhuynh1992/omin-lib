package com.bda.omnilibrary.ui.voucherV2

import com.bda.omnilibrary.model.BestVoucherForCartResponse
import com.bda.omnilibrary.model.Voucher

class VoucherV2Contract {

    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendVaucherSuccess(response: BestVoucherForCartResponse)
        fun sendVaucherFalsed(message: String)
        fun sendVouchersFromDatabase(list: ArrayList<Voucher>)
        fun sendTotalMoney(total: String)
        fun newAddress()
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadVaucher(vaucher:String)
        fun disposeAPI()
    }

}