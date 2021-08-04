package com.bda.omnilibrary.ui.liveStreamActivity

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.util.Functions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class LiveStreamPresenter(view: LiveStreamContract.View, context: Context) :
    LiveStreamContract.Presenter {
    private var mView: LiveStreamContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var userInfo: CheckCustomerResponse? = null
    private var profile: CustomerProfileResponse? = null
    private var addressData: CheckCustomerResponse? = null
    private var product: Product? = null

    /////////////// public function ///////////////

    override fun loadPresenter(liveStreamID: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getLiveStream(liveStreamID)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                this::onRetrieveLiveSteamSuccess,
                this::onRetrieveLiveSteamFailed
            )
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun fetchProfile(userInfo: CheckCustomerResponse?) {
        this.userInfo = userInfo

        if (profile != null) {
            onRetrieveProfileDataSuccess(profile!!)
        } else {
            mSubscription = APIManager.getInstance().getApi()
                .getCustomerProfile(this.userInfo?.data!!.uid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
        }
    }

    override fun updateOrder(
        data: CheckCustomerResponse,
        product: Product,
        voucherCode: String,
        voucherId: String
    ) {
        addressData = data
        this.product = product

        submitOrder(voucherCode, voucherId)
    }

    private fun onRetrieveFavouriteFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    override fun postAddFavourite(request: FavouriteResquest) {
        mSubscription =
            APIManager.getInstance().getApi().postAddFavourite(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveAddFavouriteSuccess, this::onRetrieveFavouriteFailed)
    }

    private fun onRetrieveAddFavouriteSuccess(@Suppress("UNUSED_PARAMETER") model: FavouriteResponse) {

    }

    override fun postDeleteFavourite(request: FavouriteResquest) {
        mSubscription =
            APIManager.getInstance().getApi().postDeleteFavourite(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDeleteFavouriteSuccess, this::onRetrieveFavouriteFailed)
    }

    private fun onRetrieveDeleteFavouriteSuccess(@Suppress("UNUSED_PARAMETER") model: FavouriteResponse) {

    }

    //////////////// cal api ////////////////////

    /////////////// private handle retrieve data ///////////////

    private fun onRetrieveLiveSteamSuccess(model: LiveStreamResponse?) {
        if (model!!.statusCode == 200) {
            mView.sendSuccess(model.liveStreams)
        } else {
            mView.sendFalsed(mContext.getString(R.string.error))

        }
    }

    private fun onRetrieveLiveSteamFailed(message: Throwable) {
        Log.d("LiveStreamPresenter", message.message.toString())
        mView.sendFalsed(mContext.getString(R.string.error))
    }

    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {
        this.profile = model

        val address = Functions.getDefaultAddress(model)
        val userDataInvoice = CheckCustomerResponse()

        userDataInvoice.data.alt_info = ArrayList()
        userDataInvoice.data.alt_info.add(ContactInfo())

        if (address != null) {
            userDataInvoice.data.alt_info[0].customer_name = address.customer_name
            userDataInvoice.data.alt_info[0].phone_number = address.phone_number
            userDataInvoice.data.alt_info[0].uid = address.uid
            userDataInvoice.data.alt_info[0].address = address.address
        } else {
            userDataInvoice.data.alt_info[0].customer_name = userInfo?.data!!.name
            userDataInvoice.data.alt_info[0].phone_number = userInfo?.data!!.phone

        }

        mView.sendAddressSuccess(userDataInvoice)
    }

    private fun onRetrieveProfileDataFailed(message: Throwable) {
        mView.sendAddressFailed(message.message.toString())
    }

    private fun onRetrieveDataSuccess(model: MomoPaymentResponce?) {
        if (model!!.statusCode == 200) {
            mView.finishThis(model.order_id)
        } else {
            mView.sendFailedOrder(mContext.getString(R.string.error_in_quickpay_payment))
        }
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        if (message is HttpException) {
            when {
                message.code() == 400 -> {
                    mView.sendFailedOrder(mContext.getString(R.string.error_in_quickpay_payment) + "400")
                }
                message.code() == 500 -> {
                    mView.sendFailedOrder(mContext.getString(R.string.error_in_quickpay_payment) + "500")
                }
                else -> {
                    mView.sendFailedOrder(mContext.getString(R.string.error_in_quickpay_payment))
                }
            }

        } else {
            mView.sendFailedOrder(mContext.getString(R.string.error_in_quickpay_payment))
        }

    }

    ////////////// private method ////////////////

    private fun submitOrder(voucher: String, voucher_uid: String) {
        // request
        var request: PaymentRequest? = null

        if (product != null) {
            request = Functions.mappingCartToRequestForQuickPay(
                userInfo,
                addressData,
                arrayListOf(product!!),
                voucher, voucher_uid,
                profile!!
            )
        }

        if (request != null) {
            mSubscription = APIManager.getInstance().getApi()
                .postResponce(request!!)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .doOnTerminate { }
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
        } else {
            mView.sendFailedOrder(mContext.getString(R.string.error_in_quickpay_payment))
        }
    }

}