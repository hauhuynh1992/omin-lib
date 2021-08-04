package com.bda.omnilibrary.ui.altAddressActivity

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.model.DefaultAddressRequest
import com.bda.omnilibrary.model.DefaultAddressResponse
import com.bda.omnilibrary.model.DeleteCustomerInfoRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AltAddressPresenter(
    view: AltAddressContract.View,
    @Suppress("UNUSED_PARAMETER") context: Context
) :
    AltAddressContract.Presenter {

    private var mView: AltAddressContract.View = view
    private var mSubscription: Disposable? = null

    override fun fetchDeliveryAddress(uid: String) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate {
                mView.hideProgress()
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
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
                mView.sendRemoveAltAddressSuccess(addresdId)
            }, this::onRetrieveDataFailed)
    }

    override fun postDefaultAddress(defaultAddressRequest: DefaultAddressRequest) {
        mSubscription = APIManager.getInstance().getApi().postDefaultAddress(defaultAddressRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDefaultAddressSuccess, this::onRetrieveDefaultAddressFailed)
    }


    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun onRetrieveDefaultAddressSuccess(model: DefaultAddressResponse) {
        @Suppress("SENSELESS_COMPARISON")
        if (model != null && model.statusCode == 200) {
            mView.sendDafaultAddressSuccess(model)
        } else {
            mView.sendDafaultAddressFalsed("")
        }
    }

    private fun onRetrieveDefaultAddressFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendDafaultAddressFalsed("")
    }


    private fun onRetrieveDataFailed(message: Throwable) {
        Log.d("CartPresenter", message.message.toString())
        mView.sendFalsed(R.string.alt_address_can_not_delete_address)
    }

    private fun onRetrieveDataSuccess(model: CustomerProfileResponse) {
        mView.sendSuccess(model)
    }
}