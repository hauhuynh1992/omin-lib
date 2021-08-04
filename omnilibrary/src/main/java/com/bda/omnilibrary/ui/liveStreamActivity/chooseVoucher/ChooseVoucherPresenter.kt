package com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChooseVoucherPresenter(
    view: ChooseVoucherContract.View,
    private val context: Context
) : ChooseVoucherContract.Presenter {
    private var mView: ChooseVoucherContract.View = view
    private var mSubscription: Disposable? = null
    private var uid: String = ""

    override fun checkVoucher(p: Product, code: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getApplyVoucherForCart(
                mappingCartToRequest(
                    p,
                    code
                )!!
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
    }

    override fun loadVaucher(uid: String) {
        this.uid = uid

        mSubscription = APIManager.getInstance().getApi()
            .postGetListVoucher(VoucherRequest(uid))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveVoucherSuccess, this::onRetrieveVoucherFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun onRetrieveVoucherSuccess(model: VoucherResponse) {
        @Suppress("SENSELESS_COMPARISON")
        if (model.data != null && model.status == 200) {
            mView.sendListVoucherSuccess(model.data)
        }
    }

    private fun onRetrieveVoucherFailed(message: Throwable) {
    }

    private fun mappingCartToRequest(
        product: Product,
        voucher: String,
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        items.add(PaymentRequest.Item(product.uid, product.order_quantity))

        if (uid.isNotBlank() && items.size > 0) {
            return PaymentRequest(
                platform = "fptplaybox",
                payType = "momo",
                voucher_code = voucher,
                voucher_uid = "",
                cid = uid,
                phone = "",
                address = "",
                province = "",
                district = "",
                items = items,
                name = "",
                requestDeliveryTime = "",
                note = "",
                created_phone_number = "",
                created_province = "",
                created_district = "",
                created_customer_name = "",
                created_address_des = "",
                create_address_type = 1
            )
        }
        return null
    }

    private fun onRetrieveDataSuccess(model: BestVoucherForCartResponse?) {
        if (model != null) {
            if (model.data != null && model.data.applied_value > 0 && model.status == 200) {
                mView.sendApplyVoucherSuccess(model)
            } else {
                mView.sendApplyVoucherFalsed(model.message)
            }
        } else {
            mView.sendApplyVoucherFalsed(context.getString(R.string.error_voucher_1))
        }
    }

    private fun onRetrieveDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendApplyVoucherFalsed(/*context.getString(R.string.error_voucher_1)*/ message.message
            ?: ""
        )
    }
}