package com.bda.omnilibrary.dialog.address

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.ProvinceDistrictModel
import com.bda.omnilibrary.util.Functions
import io.nlopez.smartlocation.SmartLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LocationFirstPresenter(view: LocationFirstContact.View, context: Context) :
    LocationFirstContact.Presenter {
    private var mView: LocationFirstContact.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context


    override fun loadDistrict(province_id: String) {
        Log.d("AAA", province_id)
        mSubscription = APIManager.getInstance().getApi().getDistrict(province_id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onSuccessDistrict, this::onFailed)
    }

    override fun loadProvince() {
        mSubscription = APIManager.getInstance().getApi().getProvince()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onSuccessProvince, this::onFailed)
    }

    override fun loadLocation() {
        SmartLocation.with(mContext).location()
            .oneFix()
            .start {
                if (it != null) {
                    val address =
                        Functions.getCompleteAddressString(mContext, it.latitude, it.longitude)
                    mView.sendUILocationSuccess(address)
                }
            }
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }


    private fun onFailed(message: Throwable) {
        mView.sendUIFail(mContext.getString(R.string.error))
    }

    private fun onSuccessProvince(model: ProvinceDistrictModel) {
        if (model != null && model.statusCode == 200) {
            if (model.data != null && model.data.size > 0) {
                mView.sendUIProvinceSussess(model)
            } else {
                mView.sendUIFail(mContext.getString(R.string.error))
            }

        } else {
            mView.sendUIFail(mContext.getString(R.string.error))
        }
    }

    private fun onSuccessDistrict(model: ProvinceDistrictModel) {
        if (model != null && model.statusCode == 200) {
            if (model.data != null && model.data.size > 0) {
                mView.sendUIDistrictSussess(model)
            } else {
                mView.sendUIFail(mContext.getString(R.string.error))
            }

        } else {
            mView.sendUIFail(mContext.getString(R.string.error))
        }
    }
}