package com.bda.omnilibrary.ui.ewallet

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class EwalletPresenter(
    view: EwalletContract.View,
    context: Context,
    private val type: String,
    requestType: Int
) :
    EwalletContract.Presenter {

    private var mView: EwalletContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var databaseHandler: DatabaseHandler?

    private var request: PaymentRequest? = null
    private var response: MomoPaymentResponce? = null

    private var mTimerSubscription: Disposable? = null

    private var addressData: ContactInfo? = null

    private var userInfo: CheckCustomerResponse? = null

    private var startTime = ""
    private var voucher = ""
    private var voucherId = ""
    private var requestType: String = "work_day"

    private var isInitTransaction = false

    private var list: ArrayList<Product>? = null

    @Volatile
    private var requestStatusCount: Long = -1

    init {
        this.mHelper = PreferencesHelper(mContext)
        this.databaseHandler = DatabaseHandler(context)

        if (mHelper.userInfo != null) {
            userInfo = Gson().fromJson(
                mHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }

        if (requestType == 1) {
            this.requestType = "work_day"
        } else {
            this.requestType = "all_day"
        }
    }

    ////////////// override function contract /////////////////

    override fun loadPresenter(
        vaucher: String,
        voucher_uid: String,
        list: ArrayList<Product>?
    ) {

        this.voucher = vaucher
        this.voucherId = voucher_uid
        this.list = list
        fetchProfile(userInfo!!.data.uid)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }

        if (mTimerSubscription != null && !mTimerSubscription!!.isDisposed) {
            mTimerSubscription!!.dispose()
        }

        databaseHandler = null
    }

    //////////////// cal api ////////////////////
    private fun checkTransactionStatus() {
        mSubscription!!.dispose()
        mSubscription = null

        val map = HashMap<String, Any>()
        map["transaction_id"] = response!!.transaction_id
        map["init"] = isInitTransaction

        isInitTransaction = false

        mSubscription = APIManager.getInstance().getApi()
            .postTransactionStatus(map)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                this::onRetrieveTransactionStatusSuccess,
                this::onRetrieveTransactionStatusFailed
            )
    }

    private fun fetchProfile(uid: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {

        startTime = System.currentTimeMillis().toString()

        val add = Functions.getDefaultAddress(model)

        if (add != null) {
            this.addressData = add

        } else {
            onRetrieveDataFailed(Throwable(""))
        }

        submitOrder(voucher, voucherId, model)
    }

    private fun onRetrieveProfileDataFailed(message: Throwable) {
        onRetrieveDataFailed(message)
    }

    /////////////// private handle retrieve data ///////////////

    private fun onRetrieveDataSuccess(model: MomoPaymentResponce?) {
        if (model!!.status) {
            this.response = model
            mView.sendQrCodeBase64(model.qrCode)

            mTimerSubscription = Observable.interval(5, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (requestStatusCount != it) {
                        requestStatusCount = it
                        checkTransactionStatus()
                    }
                }

        } else {
            mView.sendFalsed(mContext.getString(getErrorResource()))
        }
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        if (message is HttpException) {
            when {
                message.code() == 400 -> {
                    mView.sendFalsed(mContext.getString(getErrorResource()) + " 400")
                }
                message.code() == 500 -> {
                    mView.sendFalsed(mContext.getString(getErrorResource()) + " 500")
                }
                else -> {
                    mView.sendFalsed(mContext.getString(getErrorResource()))
                }
            }

        } else {
            mView.sendFalsed(mContext.getString(getErrorResource()))
        }
    }

    private fun onRetrieveTransactionStatusSuccess(model: MomoTransactionStatusResponce?) {
        if (model!!.statusCode == 200) {

            if (model.transactionStatus == "success") {
                mTimerSubscription!!.dispose()
                mView.sendSuccess(model.oId)

            }
        } else {
            mView.sendFalsed(mContext.getString(getErrorResource()))
        }
    }

    private fun onRetrieveTransactionStatusFailed(message: Throwable) {
        Log.d("MomoPresenter", message.message.toString())
    }

    ////////////// private method ////////////////

    private fun submitOrder(vaucher: String, voucher_uid: String, model: CustomerProfileResponse) {
        isInitTransaction = true

        // request

        val data: ArrayList<Product> = if (list != null) {
            list!!
        } else {
            Functions.removeProductBySupplierCondition(databaseHandler!!.getLProductList())
        }

        request = mappingCartToRequest(data, vaucher, voucher_uid, model)

        if (mTimerSubscription != null)
            mTimerSubscription!!.dispose()

        if (request != null) {
            mSubscription = APIManager.getInstance().getApi()
                .postResponce(request!!)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .doOnTerminate { }
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
        } else {
            mView.sendFalsed(mContext.getString(getErrorResource()))
        }
    }

    private fun mappingCartToRequest(
        products: ArrayList<Product>,
        vaucher: String, voucher_uid: String,
        model: CustomerProfileResponse
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                items.add(PaymentRequest.Item(item.uid, item.order_quantity))
            }
        }

        if (userInfo != null && addressData != null && items.size > 0) {
            var districtCode = ""
            var provinceCode = ""
            var addressType = 0
            var addressDes = ""

            model.address?.let { address ->
                addressType = address.address_type ?: 1
                addressDes = address.address_des ?: ""
                address.district?.let { district ->
                    districtCode = district.uid ?: ""
                }
                address.province?.let { province ->
                    provinceCode = province.uid ?: ""
                }
            }
            return PaymentRequest(
                platform = "fptplaybox",
                payType = type,
                cid = userInfo!!.data.uid,
                voucher_code = vaucher, voucher_uid = voucher_uid,
                phone = addressData!!.phone_number,
                address = addressData!!.address.address_des,
                province = addressData!!.address.customer_province.uid,
                district = addressData!!.address.customer_district.uid,
                items = items,
                addressType = addressData!!.address.address_type,
                name = addressData!!.customer_name,
                requestDeliveryTime = requestType,
                note = "",
                create_address_type = addressType,
                created_address_des = addressDes,
                created_customer_name = model.name ?: "",
                created_district = districtCode,
                created_province = provinceCode,
                created_phone_number = model.phone ?: ""
            )
        }
        return null
    }

    private fun getErrorResource(): Int {
        return when (type) {
            "vnpay" -> R.string.error_in_vinid_payment
            "momo" -> R.string.error_in_momo_payment
            "moca" -> R.string.error_in_moca_payment
            else -> R.string.error_in_payment
        }
    }
}