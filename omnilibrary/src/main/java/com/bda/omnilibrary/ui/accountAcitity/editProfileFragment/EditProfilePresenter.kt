package com.bda.omnilibrary.ui.accountAcitity.editProfileFragment

import android.content.Context
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.UpdateCustomerProfileRequest
import com.bda.omnilibrary.model.UpdateCustomerProfileResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EditProfilePresenter(view: EditProfileContact.View, @Suppress("UNUSED_PARAMETER") context: Context) :
    EditProfileContact.Presenter {
    private var mView: EditProfileContact.View = view
    private var mSubscription: Disposable? = null

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onRetrieveDataSuccess(model: UpdateCustomerProfileResponse) {
        mView.sendSuccess()
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        mView.sendFalsed(message.message.toString())
    }

    override fun updateCustomProfile(request: UpdateCustomerProfileRequest) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .postUpdateCustomerProfile(request)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)

    }
}