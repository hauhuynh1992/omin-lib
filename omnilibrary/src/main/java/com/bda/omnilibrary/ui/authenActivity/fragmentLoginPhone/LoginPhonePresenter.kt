package com.bda.omnilibrary.ui.authenActivity.fragmentLoginPhone

import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.LoginByGoogleRequest
import com.bda.omnilibrary.model.LoginByPhoneRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginPhonePresenter(view: LoginPhoneContact.View) :
    LoginPhoneContact.Presenter {
    private var mView: LoginPhoneContact.View = view
    private var mSubscription: Disposable? = null

    override fun loginWithPhone(loginRequest: LoginByPhoneRequest) {
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
                mView.showLoginByPhoneSuccess(
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

