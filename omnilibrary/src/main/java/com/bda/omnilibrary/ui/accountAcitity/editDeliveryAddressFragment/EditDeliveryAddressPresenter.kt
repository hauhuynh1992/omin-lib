package com.bda.omnilibrary.ui.accountAcitity.editDeliveryAddressFragment

import android.content.Context
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.DefaultAddressRequest
import com.bda.omnilibrary.model.DefaultAddressResponse
import com.bda.omnilibrary.model.DeleteCustomerInfoRequest
import com.bda.omnilibrary.model.UpdateCustomerAltRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditDeliveryAddressPresenter(view: EditDeliveryAddressContact.View, @Suppress("UNUSED_PARAMETER") context: Context) :
    EditDeliveryAddressContact.Presenter {
    private var mView: EditDeliveryAddressContact.View = view
    private var mSubscription: Disposable? = null
    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        mView.sendFalsed(message.message.toString())
    }

    override fun updateCustomAltInfo(request: UpdateCustomerAltRequest) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .postUpdateCustomerAlt(request)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({
                if (request.alt_info!!.is_default_address) {
                    val default = DefaultAddressRequest(
                        request.uid.toString(),
                        request.alt_info!!.uid
                    )
                    postDefaultAddress(default)
                } else {
                    mView.sendSuccess()
                }
            }, this::onRetrieveDataFailed)
    }

    override fun postDefaultAddress(defaultAddressRequest: DefaultAddressRequest) {
        mSubscription = APIManager.getInstance().getApi().postDefaultAddress(defaultAddressRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDefaultAddressSuccess, this::onRetrieveDefaultAddressFailed)
    }

    override fun addCustomAltInfo(request: UpdateCustomerAltRequest) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .postAddCustomerAlt(request)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({
                mView.sendSuccess()
            }, this::onRetrieveDataFailed)
    }

    override fun removeAddress(uid: String, addresdId: String) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi().deleteCustomerInfo(
            DeleteCustomerInfoRequest(uid, addresdId)
        )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({
                mView.sendSuccess()
            }, this::onRetrieveDataFailed)
    }


    private fun onRetrieveDefaultAddressSuccess(@Suppress("UNUSED_PARAMETER") model: DefaultAddressResponse) {
        mView.sendSuccess()
    }

    private fun onRetrieveDefaultAddressFailed(message: Throwable) {
        mView.sendFalsed(message.message.toString())
    }
}