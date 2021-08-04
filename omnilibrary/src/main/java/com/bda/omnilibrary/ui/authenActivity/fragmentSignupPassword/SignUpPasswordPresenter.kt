package com.bda.omnilibrary.ui.authenActivity.fragmentSignupPassword

import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.RegisterPasswordRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignUpPasswordPresenter(view: SignUpPasswordContact.View) :
    SignUpPasswordContact.Presenter {
    private var mView: SignUpPasswordContact.View = view
    private var mSubscription: Disposable? = null

    override  fun signUpPassword(loginRequest: RegisterPasswordRequest) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .registerPassword(
                loginRequest
            )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                mView.showSignUpPasswordSuccess("", "")
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

