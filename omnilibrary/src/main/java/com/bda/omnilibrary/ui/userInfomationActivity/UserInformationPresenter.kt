package com.bda.omnilibrary.ui.userInfomationActivity

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.model.UpdateCustomerRequest
import com.bda.omnilibrary.util.Functions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.nlopez.smartlocation.SmartLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class UserInformationPresenter(view: UserInformationContract.View, context: Context) :
    UserInformationContract.Presenter {
    private var mView: UserInformationContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var databaseHandler: DatabaseHandler?
    private lateinit var checkCustomerResponse: CheckCustomerResponse

    init {
        this.mHelper = PreferencesHelper(mContext)
        this.databaseHandler = DatabaseHandler(context)

        if (mHelper.userInfo != null) {
            checkCustomerResponse = Gson().fromJson(
                mHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
    }

    override fun loadLocation() {
        if (ContextCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            SmartLocation.with(mContext).location()
                .oneFix()
                .start {
                    if (it != null) {
                        val address =
                            Functions.getCompleteAddressString(mContext, it.latitude, it.longitude)
                        mView.sendLocationSuccess(address)
                    }
                }
        } else {
            if (mHelper.isFirstTimeAskingInUsetInfo()) {
                mView.requestPermission()
            }
        }
    }

    override fun loadPresenter() {
        @Suppress("SENSELESS_COMPARISON")
        if (checkCustomerResponse.data != null) {
            fetchProfile()

            if (GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS
            ) {
                loadLocation()
            }
        } else {
            mView.sendError(mContext.getString(R.string.text_user_data_error))
        }
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
        databaseHandler = null
    }

    override fun fetchProfile() {
        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(checkCustomerResponse.data.uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    override fun updateAddress(userInfoTemp: CheckCustomerResponse) {
        val request: UpdateCustomerRequest
        if (checkCustomerResponse.data.name.isEmpty()) {
            request = UpdateCustomerRequest(
                uid = checkCustomerResponse.data.uid,
                phone = checkCustomerResponse.data.phone,
                name = userInfoTemp.data.name,
                address = userInfoTemp.data.address,
                alt_info = userInfoTemp.data.alt_info
            )

        } else {
            request = UpdateCustomerRequest(
                uid = checkCustomerResponse.data.uid,
                phone = checkCustomerResponse.data.phone,
                name = checkCustomerResponse.data.name,
                address = checkCustomerResponse.data.address,
                alt_info = userInfoTemp.data.alt_info
            )
        }

        mSubscription = APIManager.getInstance().getApi()
            .postUpdateCustomer(request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                this::onRetrieveUpdateCustomerSuccess,
                this::onRetrieveUpdateCustomerFailed
            )
    }

    private fun onRetrieveUpdateCustomerSuccess(model: CheckCustomerResponse) {
        if (model.status == 200) {
            mView.sendUpdateCustomer(model)
        } else {
            mView.sendUpdateCustomerFail(mContext.getString(R.string.customer_update_faill))
        }
    }

    private fun onRetrieveUpdateCustomerFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendUpdateCustomerFail(mContext.getString(R.string.customer_update_faill))
    }

    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {
        model.phone?.let {
            model.name?.let { it1 ->
                mView.sendProfileSuccess(it1, it)
            }
        }

        mView.loadFragment()
    }

    private fun onRetrieveProfileDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.loadFragment()
    }
}