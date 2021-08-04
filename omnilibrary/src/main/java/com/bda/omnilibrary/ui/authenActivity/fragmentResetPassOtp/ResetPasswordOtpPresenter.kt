package com.bda.omnilibrary.ui.authenActivity.fragmentResetPassOtp

import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.LoginByPhoneRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ResetPasswordOtpPresenter(view: ResetPasswordOtpContact.View) :
    ResetPasswordOtpContact.Presenter {
    private var mView: ResetPasswordOtpContact.View = view
    private var mSubscription: Disposable? = null

    override fun resetPasswordInputPhone(loginRequest: LoginByPhoneRequest) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .loginByPhone(
                loginRequest
            )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                mView.showInputPhoneSuccess(
                    customerId = response.customer.uid,
                    customerName = response.customer.customerName
                )
            }, { e ->
                mView.showError(e.message.toString())
            }, {
                mView.hideProgress()
            })
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }
}

