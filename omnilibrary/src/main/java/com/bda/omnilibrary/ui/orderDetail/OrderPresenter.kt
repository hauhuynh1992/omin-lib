package com.bda.omnilibrary.ui.orderDetail

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.util.Functions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OrderPresenter(view: OrderContract.View, @Suppress("UNUSED_PARAMETER") val context: Context) :
    OrderContract.Presenter {

    private var mView: OrderContract.View = view
    private var mSubscription: Disposable? = null
    private var uid: String = ""
    private var addressData: ContactInfo? = null

    override fun loadVoucher(request: VoucherRequest) {
        mSubscription = APIManager.getInstance().getApi()
            .postGetListVoucher(request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveVoucherSuccess, this::onRetrieveVoucherFailed)
    }

    override fun checkVoucher(list: ArrayList<Product>, voucher: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getApplyVoucherForCart(
                mappingCartToRequest(
                    list,
                    voucher
                )!!
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun fetchProfile(uid: String) {
        this.uid = uid
        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {
        addressData = Functions.getDefaultAddress(model)
        mView.sendProfileSuccess(addressData)
    }

    private fun onRetrieveProfileDataFailed(message: Throwable) {
        mView.sendProfileFalsed(message.message.toString())
    }


    private fun onRetrieveVoucherSuccess(model: VoucherResponse) {
        @Suppress("SENSELESS_COMPARISON")
        if (model.data != null && model.status == 200) {
            mView.sendListVoucherSuccess(model)
        }
    }

    private fun onRetrieveVoucherFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
    }

    private fun mappingCartToRequest(
        products: ArrayList<Product>,
        voucher: String
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                items.add(PaymentRequest.Item(item.uid, item.order_quantity))
            }
        }

        if (uid.isNotBlank() && items.size > 0) {
            return PaymentRequest(
                platform = "fptplaybox",
                payType = "momo",
                voucher_code = voucher,
                voucher_uid = "",
                cid = uid,
                phone = addressData?.phone_number ?: "",
                address = addressData?.address?.address_des ?: "",
                province = addressData?.address?.customer_province?.uid ?: "",
                district = addressData?.address?.customer_district?.uid ?: "",
                items = items,
                addressType = addressData!!.address.address_type,
                name = addressData!!.customer_name,
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
                mView.sendVoucherSuccess(model)
            } else {
                mView.sendVoucherFalsed(model.message)
            }
        } else {
            mView.sendVoucherFalsed(context.getString(R.string.error_voucher_1))
        }
    }


    private fun onRetrieveDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendVoucherFalsed(context.getString(R.string.error_voucher_1))
    }
}