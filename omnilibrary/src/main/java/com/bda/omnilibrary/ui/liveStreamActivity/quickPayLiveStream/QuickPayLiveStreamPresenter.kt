package com.bda.omnilibrary.ui.liveStreamActivity.quickPayLiveStream

import android.content.Context
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.ContactInfo
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.util.Functions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class QuickPayLiveStreamPresenter(view: QuickPayLiveStreamContract.View, context: Context) :
    QuickPayLiveStreamContract.Presenter {
    private var mView: QuickPayLiveStreamContract.View = view
    private var mSubscription: Disposable? = null
    private var userInfo: CheckCustomerResponse? = null
    private var profile: CustomerProfileResponse? = null

    /////////////// public function ///////////////

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    //////////////// cal api ////////////////////
    override fun fetchProfile(userInfo: CheckCustomerResponse?) {
        this.userInfo = userInfo

        if (profile != null) {
            onRetrieveProfileDataSuccess(profile!!)
        } else {
            mSubscription = APIManager.getInstance().getApi()
                .getCustomerProfile(this.userInfo?.data!!.uid)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
        }
    }


    /////////////// private handle retrieve data ///////////////
    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {
        this.profile = model

        val address = Functions.getDefaultAddress(model)
        val userDataInvoice = CheckCustomerResponse()

        userDataInvoice.data.alt_info = ArrayList()
        userDataInvoice.data.alt_info.add(ContactInfo())

        if (address != null) {
            userDataInvoice.data.alt_info[0].customer_name = address.customer_name
            userDataInvoice.data.alt_info[0].phone_number = address.phone_number
            userDataInvoice.data.alt_info[0].uid = address.uid
            userDataInvoice.data.alt_info[0].address = address.address
        } else {
            userDataInvoice.data.alt_info[0].customer_name = userInfo?.data!!.name
            userDataInvoice.data.alt_info[0].phone_number = userInfo?.data!!.phone

        }

        mView.sendAddressSuccess(userDataInvoice)
    }

    private fun onRetrieveProfileDataFailed(message: Throwable) {
        mView.sendAddressFailed(message.message.toString())
    }

    ////////////// private method ////////////////

}