package com.bda.omnilibrary.ui.authenActivity.fragmentLoginGoogle

import android.util.Log
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LoginByGoogleRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LoginGooglePresenter(view: LoginGoogleContact.View) :
    LoginGoogleContact.Presenter {
    private var mView: LoginGoogleContact.View = view
    private var mSubscription: Disposable? = null
    private var mTimerSubscription: Disposable? = null

    private var requestStatusCount: Int = -1

    override fun getDeviceCode() {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .getGoogleDeviceCode(
                QuickstartPreferences.OAUTH_DEVICE_CODE_URL,
                QuickstartPreferences.OAUTH_CLIENT_ID,
                QuickstartPreferences.OAUTH_SCOPE
            )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                mView.showDeviceCode(
                    response.userCode,
                    response.verificationUrl,
                    response.deviceCode
                )
            }, { e ->
                mView.showError(e.message.toString())
            }, {
                mView.hideProgress()
            })
    }

    override fun getAccessToken(deviceCode: String) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .getGoogleDeviceToken(
                url = QuickstartPreferences.OAUTH_DEVICE_TOKEN_URL,
                clientId = QuickstartPreferences.OAUTH_CLIENT_ID,
                clientSecret = QuickstartPreferences.OAUTH_CLIENT_SECRET,
                deviceCode = deviceCode,
                grantType = QuickstartPreferences.OAUTH_GRANT_TYPE,
            )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                if (mTimerSubscription != null)
                    mTimerSubscription!!.dispose()
                mView.showAssessToken(response.accessToken, response.token_type)
            }, { e ->
                mView.showError(e.message.toString())
            }, {
                mView.hideProgress()
            })
    }

    override fun getDriveInfo(token: String, token_type: String) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .getGoogleDriveInfo(
                url = QuickstartPreferences.OAUTH_DRIVE_URL,
                authHeader = token_type + " " + token
            )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                mView.showGoogleDriveInfoToken(
                    id = response.user.permissionId,
                    name = response.user.displayName,
                    token = token,
                    email = response.user.emailAddress
                )
            }, { e ->
                mView.showError(e.message.toString())
            }, {
                mView.hideProgress()
            })
    }

    override fun loginWithGoogle(id: String, email: String, token: String, name: String) {
        mView.showProgress()
        val request = LoginByGoogleRequest(
            googleId = id,
            email = email,
            googleToken = token,
            customerName = name
        )
        mSubscription = APIManager.getInstance().getApi()
            .loginByGoogle(
                request
            )
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { mView.hideProgress() }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                mView.showLoginByGoogleSuccess(
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

        if (mTimerSubscription != null && !mTimerSubscription!!.isDisposed) {
            mTimerSubscription!!.dispose()
        }
    }

    override fun getAccessTokenInterval(deviceCode: String) {
        Log.d("BBB", "deviceCode: " + deviceCode)
        mTimerSubscription = Observable.interval(5, TimeUnit.SECONDS, Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                requestStatusCount = 1
                getAccessToken(deviceCode)
            }, { e ->
                requestStatusCount = 0
                mView.showError(e.message.toString())
            }, {
                mView.hideProgress()
            })
    }
}

