package com.bda.omnilibrary.ui.productDetailActivity

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class ProductDetailPresenter(view: ProductDetailContact.View, context: Context) :
    ProductDetailContact.Presenter {

    private var mView: ProductDetailContact.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var userInfo: CheckCustomerResponse? = null
    private var profile: CustomerProfileResponse? = null
    private var addressData: CheckCustomerResponse? = null
    private var product: Product? = null
    private var request: PaymentRequest? = null

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }


    override fun callApiProduct(uid: String) {
        if (Regex("^0[xX][a-fA-F0-9]+\$").matches(uid)) {
            mSubscription = APIManager.getInstance().getApi().getProductFromId(
                ProductByUiRequest(
                    arrayListOf(uid)
                )
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataProductSuccess, this::failedProduct)
        }
    }

    override fun callVoucher(id: String, uid: String?) {
        mSubscription = APIManager.getInstance().getApi().getBestVoucherForProduct(id, uid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataVoucherSuccess, this::failedVoucher)
    }

    private fun onRetrieveDataVoucherSuccess(response: BestVoucherForProductResponse) {
        if (response.status == 200) {
            mView.sendVoucher(response.data)
        }
    }

    private fun failedVoucher(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveDataProductSuccess(response: ProductByUiResponse) {
        if (response.status == 200 && !response.data.isNullOrEmpty()) {
            mView.sendProductSuccess(response.data[0])
        } else {
            mView.sendProductFail()
        }
    }

    private fun failedProduct(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendProductFail()
    }

    override fun callApiRelative(idProduct: String) {
        mSubscription =
            APIManager.getInstance().getApi().postRelativeProduct(RelativeProductRequest(idProduct))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveRelativeSuccess) {
                    mView.sendSplashRelativeFail(mContext.getString(R.string.error))
                }
    }

    private fun onRetrieveRelativeSuccess(response: CartModel) {
        if (response.statusCode == 200) {
            mView.sendSplashRelativeSuccess(response.data)
        } else {
            mView.sendSplashRelativeFail(mContext.getString(R.string.error))
        }
    }

    override fun loadPopup(popUprequest: PopUpRequest) {
        mSubscription =
            APIManager.getInstance().getApi().getListPopup(popUprequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveListPopUpSuccess, this::onRetrieveListPopUpFailed)
    }

    override fun loadFavourite(customer_id: String, product_id: String) {
        mSubscription =
            APIManager.getInstance().getApi().getSingleFavourite(customer_id, product_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveFavouriteSuccess, this::onRetrieveFavouriteFailed)
    }

    private fun onRetrieveFavouriteSuccess(model: FavouriteResponse) {
        if (model.checkStatus) {
            mView.sendLoadFavouriteSuccess(model)
        }
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

    override fun sendActivePopUp(activePopUpRequest: ActivePopUpRequest) {
        mSubscription =
            APIManager.getInstance().getApi().postActivePopup(activePopUpRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveActivePopUpSuccess, this::onRetrieveActivePopUpFailed)
    }

    override fun fetchProfile(userInfo: CheckCustomerResponse?) {
        this.userInfo = userInfo

        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(this.userInfo?.data!!.uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    override fun updateOrder(
        data: CheckCustomerResponse,
        product: Product,
        voucherCode: String,
        voucherId: String,
    ) {
        addressData = data
        this.product = product

        submitOrder(voucherCode, voucherId)
    }

    private fun submitOrder(voucher: String, voucher_uid: String) {
        // request
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


    private fun onRetrieveListPopUpSuccess(model: PopUpResponse) {
        if (model.result.size > 0) {
            mView.sendListPopUpSussess(model)
        }
    }

    private fun onRetrieveListPopUpFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveActivePopUpSuccess(@Suppress("UNUSED_PARAMETER") model: ActivePopUpResponse) {

    }

    private fun onRetrieveActivePopUpFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }
}