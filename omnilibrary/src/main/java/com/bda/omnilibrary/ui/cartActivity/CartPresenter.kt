package com.bda.omnilibrary.ui.cartActivity

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CartPresenter(
    view: CartContract.View,
    @Suppress("UNUSED_PARAMETER") private val context: Context,
) :
    CartContract.Presenter {
    private var mView: CartContract.View = view
    private var mSubscription: Disposable? = null
    private var uid: String = ""

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun callHightlight() {
        mSubscription = APIManager.getInstance().getApi().getSuggestedProducts()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveHightlightSuccess, this::failedHightlight)
    }

    override fun fetchProfile(uid: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    override fun updateCartOnline(cardRequest: CartRequest) {
        mSubscription = APIManager.getInstance().getApi().postSaveCart(cardRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveCartSuccess, this::onRetrieveCartFailed)
    }

    override fun loadVoucher(uid: String) {
        this.uid = uid

        mSubscription = APIManager.getInstance().getApi()
            .postGetListVoucher(VoucherRequest(uid))
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

    override fun checkVoucherForContinueShopping(list: ArrayList<Product>, voucher: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getApplyVoucherForCart(
                mappingCartToRequest(
                    list,
                    voucher
                )!!
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccessContinue, this::onRetrieveDataFailedContinue)
    }

    override fun sendListVoucherSuccess(vouchers: VoucherResponse) {
    }

    private fun onRetrieveCartSuccess(model: CartModel) {
    }

    private fun onRetrieveCartFailed(message: Throwable) {
    }

    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {
        mView.sendProfileSuccess(model)
    }

    private fun onRetrieveProfileDataFailed(message: Throwable) {
        mView.sendProfileFalsed(message.message.toString())
    }


    private fun onRetrieveHightlightSuccess(response: HightlightModel) {
        @Suppress("SENSELESS_COMPARISON")
        if (response != null || response.statusCode == 200) {
            mView.sendHightlightSuccess(response.products)
        }
    }

    private fun failedHightlight(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        //mView.sendFail(R.string.error)
        mView.sendHighlightFail(R.string.error)
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
        voucher: String,
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                items.add(PaymentRequest.Item(item.uid, item.order_quantity))
            }
        }

        if (uid.isNotBlank() && items.size > 0) {
            //todo api chi check Customer id va list product
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
        mView.sendApplyVoucherFalsed(context.getString(R.string.error_voucher_1))
    }

    private fun onRetrieveDataSuccessContinue(model: BestVoucherForCartResponse?) {
        if (model != null) {
            if (model.data!!.applied_value > 0 && model.status == 200) {
                mView.sendApplyVoucherSuccessContinue(model)
            } else {
                mView.sendApplyVoucherFalsedContinue(model.message)
            }
        } else {
            mView.sendApplyVoucherFalsed(context.getString(R.string.error_voucher_1))
        }
    }


    private fun onRetrieveDataFailedContinue(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendApplyVoucherFalsedContinue(message.message ?: "")

    }
}