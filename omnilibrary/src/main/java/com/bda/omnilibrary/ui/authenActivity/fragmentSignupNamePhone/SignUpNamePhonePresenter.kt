package com.bda.omnilibrary.ui.authenActivity.fragmentSignupNamePhone

import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.RegisterPhoneNameRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignUpNamePhonePresenter(view: SignUpNamePhoneContact.View) :
    SignUpNamePhoneContact.Presenter {
    private var mView: SignUpNamePhoneContact.View = view
    private var mSubscription: Disposable? = null

    override fun signUpNamePhone(loginRequest: RegisterPhoneNameRequest) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .registerPhoneName(
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

