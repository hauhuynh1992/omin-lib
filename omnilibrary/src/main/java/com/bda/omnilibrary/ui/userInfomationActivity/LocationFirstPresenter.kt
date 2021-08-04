package com.bda.omnilibrary.ui.userInfomationActivity

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.ProvinceDistrictModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LocationFirstPresenter(view: LocationFirstContact.View, context: Context) :
    LocationFirstContact.Presenter {
    private var mView: LocationFirstContact.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context


    override fun loadDistrict(province_id: String) {
        mSubscription = APIManager.getInstance().getApi().getDistrict(province_id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onSuccess, this::onFailed)
    }

    override fun loadProvince() {
        mSubscription = APIManager.getInstance().getApi().getProvince()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onSuccessProvince, this::onFailedProvince)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun onSuccess(model: ProvinceDistrictModel) {
        if (model.statusCode == 200) {
            @Suppress("SENSELESS_COMPARISON")
            if (model.data != null && model.data.size > 0) {
                mView.sendUISussess(model)
            } else {
                mView.sendUIFail(mContext.getString(R.string.error))
            }
        } else {
            mView.sendUIFail(mContext.getString(R.string.error))
        }
    }

    private fun onFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendUIFail(mContext.getString(R.string.error))
    }

    private fun onSuccessProvince(model: ProvinceDistrictModel) {
        if (model.statusCode == 200) {
            @Suppress("SENSELESS_COMPARISON")
            if (model.data != null && model.data.size > 0) {
                mView.sendUIProvinceSussess(model)
            } else {
                mView.sendUIProvinceFail(mContext.getString(R.string.error))
            }

        } else {
            mView.sendUIProvinceFail(mContext.getString(R.string.error))
        }
    }

    private fun onFailedProvince(message: Throwable) {
        mView.sendUIProvinceFail(mContext.getString(R.string.error))
    }

}