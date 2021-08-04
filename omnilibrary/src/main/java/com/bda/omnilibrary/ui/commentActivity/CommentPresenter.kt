package com.bda.omnilibrary.ui.commentActivity

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class CommentPresenter(view: CommentContract.View, context: Context) : CommentContract.Presenter {
    private var mView: CommentContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var request: PaymentRequest? = null
    private var databaseHandler: DatabaseHandler?
    private var userInfo: CheckCustomerResponse? = null
    private var response: MomoPaymentResponce? = null

    private var addressData: CheckCustomerResponse? = null

    private var mTimerSubscription: Disposable? = null

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
    }

    /////////////// public function ///////////////

    override fun loadPresenter(
        addressData: CheckCustomerResponse,
        vaucher: String,
        voucher_uid: String
    ) {
        this.addressData = addressData
        submitOrder(vaucher, voucher_uid)
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
        map["order_id"] = response!!.transaction_id
        map["uid"] = response!!.uid
        mSubscription = APIManager.getInstance().getApi()
            .postTransactionStatus(map)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                this::onRetrieveTransactionStatusSuccess,
                this::onRetrieveTransactionStatusFailed
            )
    }

    override fun cancelOrder() {
//        mSubscription!!.dispose()
//        mTimerSubscription!!.dispose()
//        mSubscription = null
//        mSubscription = APIManager.getInstance().getApi()
//            .pushOrderStatus(PushOrderStatus(response!!.order_Id, 6))
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.newThread())
//            .subscribe(
//                this::onRetrieveChangeStatusSuccess,
//                this::onRetrieveChangeStatusFailed
//            )
    }

    /////////////// private handle retrieve data ///////////////

    @Suppress("unused")
    private fun onRetrieveChangeStatusSuccess(@Suppress("UNUSED_PARAMETER") model: PushOrderStatus?) {
        mView.finishActivity()
    }

    @Suppress("unused")
    private fun onRetrieveChangeStatusFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.finishActivity()
    }

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
            mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment))
        }
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        if (message is HttpException) {
            when {
                message.code() == 400 -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment) + "400")
                }
                message.code() == 500 -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment) + "500")
                }
                else -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment))
                }
            }

        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment))
        }

    }

    private fun onRetrieveTransactionStatusSuccess(model: MomoTransactionStatusResponce?) {
        if (model!!.statusCode == 200) {
            if (model.transactionStatus == "success") {
                mTimerSubscription!!.dispose()
                mView.sendSuccess()
            }
        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment))
        }
    }

    private fun onRetrieveTransactionStatusFailed(message: Throwable) {
        Log.d("MomoPresenter", message.message.toString())
    }


    ////////////// private method ////////////////

    private fun submitOrder(vaucher: String, voucher_uid: String) {
        // request
        val data = databaseHandler!!.getLProductList()
        request = mappingCartToRequest(data, vaucher, voucher_uid)

        if (request != null) {
            mSubscription = APIManager.getInstance().getApi()
                .postResponce(request!!)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .doOnTerminate { }
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment))
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mappingCartToRequest(
        products: ArrayList<Product>,
        voucher: String,
        voucher_uid: String
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                items.add(PaymentRequest.Item(item.uid, item.order_quantity))
            }
        }

//        if (userInfo != null && addressData != null && items.size > 0) {
//            return PaymentRequest(
//                platform = "fptplaybox",
//                payType = "momo",
//                cid = userInfo!!.data.uid,
//                voucher_code = vaucher,voucher_uid = voucher_uid,
//                phone = addressData!!.data.alt_info[0].phone_number,
//                address = addressData!!.data.alt_info[0].address.address_des,
//                province = addressData!!.data.alt_info[0].address.customer_province.uid,
//                district = addressData!!.data.alt_info[0].address.customer_district.uid,
//                items = items,
//                addressType = addressData!!.data.alt_info[0].address.address_type,
//                name = addressData!!.data.alt_info[0].customer_name,
//                env = Config.evironment
//            )
//        }
        return null
    }
}