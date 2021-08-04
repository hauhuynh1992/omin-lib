package com.bda.omnilibrary.ui.authenActivity.fragmentSignupOtpPhone

import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.RegisterOtpRequest
import com.bda.omnilibrary.model.RegisterPhoneNameRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignUpOtpPresenter(view: SignUpOtpContact.View) :
    SignUpOtpContact.Presenter {
    private var mView: SignUpOtpContact.View = view
    private var mSubscription: Disposable? = null

    override fun signUpOtp(loginRequest: RegisterOtpRequest) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .registerOtp(
                loginRequest
            )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                mView.showSignUpNamePhoneSuccess("", "")
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

