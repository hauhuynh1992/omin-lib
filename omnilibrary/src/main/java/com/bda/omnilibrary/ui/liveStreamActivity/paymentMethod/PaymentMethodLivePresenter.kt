package com.bda.omnilibrary.ui.liveStreamActivity.paymentMethod

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.MomoPaymentResponce
import com.bda.omnilibrary.model.PaymentRequest
import com.bda.omnilibrary.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class PaymentMethodLivePresenter(view: PaymentMethodLiveContract.View, context: Context) :
    PaymentMethodLiveContract.Presenter {
    private var mView: PaymentMethodLiveContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var request: PaymentRequest? = null
    private var userInfo: CheckCustomerResponse? = null
    private var requestType: String = "work_day"

    private var list: ArrayList<Product>? = null

    private var voucher = ""
    private var voucherId = ""

    init {
        this.mHelper = PreferencesHelper(mContext)

        if (mHelper.userInfo != null) {
            userInfo = Gson().fromJson(
                mHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
    }

    /////////////// public function ///////////////

    override fun loadPresenter(
        name: String,
        phone: String,
        vaucher: String,
        voucher_uid: String,
        requestType: Int,
        list: ArrayList<Product>?
    ) {

        this.voucher = vaucher
        this.voucherId = voucher_uid
        this.list = list

        if (requestType == 1) {
            this.requestType = "work_day"
        } else {
            this.requestType = "all_day"
        }

        submitOrder(voucher, voucherId, name, phone)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }


    /////////////// private handle retrieve data ///////////////

    private fun onRetrieveDataSuccess(model: MomoPaymentResponce?) {
        if (model!!.statusCode == 200) {
            mView.sendSuccess(model.order_id)
        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment))
        }
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        if (message is HttpException) {
            when {
                message.code() == 400 -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment) + "400")
                }
                message.code() == 500 -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment) + "500")
                }
                else -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment))
                }
            }

        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment))
        }

    }

    private fun submitOrder(voucher: String, voucher_uid: String, name: String, phone: String) {
        // request
        val data: ArrayList<Product> = list!!


        request = mappingCartToRequest(data, voucher, voucher_uid, name, phone)

        if (request != null) {
            mSubscription = APIManager.getInstance().getApi()
                .postResponce(request!!)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .doOnTerminate { }
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_payment))
        }
    }

    private fun mappingCartToRequest(
        products: ArrayList<Product>,
        voucher: String,
        voucher_uid: String,
        name: String,
        phone: String
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                items.add(PaymentRequest.Item(item.uid, item.order_quantity))
            }
        }

        if (userInfo != null && items.size > 0) {
            var districtCode = ""
            var provinceCode = ""
            var addressType = 0
            var addressDes = ""


            return PaymentRequest(
                platform = "fptplaybox",
                payType = "cod",
                cid = userInfo!!.data.uid,
                voucher_code = voucher, voucher_uid = voucher_uid,
                phone = phone,
                address = addressDes,
                province = "",
                district = "",
                items = items,
                addressType = addressType,
                name = name,
                requestDeliveryTime = requestType,
                note = "",
                create_address_type = addressType,
                created_address_des = addressDes,
                created_customer_name = name,
                created_district = districtCode,
                created_province = provinceCode,
                created_phone_number = phone
            )
        }
        return null
    }
}