package com.bda.omnilibrary.ui.accountAcitity.profileFragment

import android.content.Context
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.model.DefaultAddressResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ProfilePresenter(view: ProfileContact.View, @Suppress("UNUSED_PARAMETER") context: Context) : ProfileContact.Presenter {
    private var mView: ProfileContact.View = view
    private var mSubscription: Disposable? = null


    override fun fetchProfile(uid: String) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .postCheckCustomer(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate {
                mView.hideProgress()
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun onRetrieveDataSuccess(model: CheckCustomerResponse) {
        mView.sendSuccess(model)
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        mView.sendFalsed(message.message.toString())
    }
}