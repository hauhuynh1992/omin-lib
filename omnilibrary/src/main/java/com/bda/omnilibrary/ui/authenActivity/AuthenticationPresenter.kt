package com.bda.omnilibrary.ui.authenActivity

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.ContactInfo
import com.bda.omnilibrary.util.Config
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AuthenticationPresenter(view: AuthenticationContract.View, context: Context) :
    AuthenticationContract.Presenter {
    private var mView: AuthenticationContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper? = null

    init {
        this.mHelper = PreferencesHelper(mContext)
    }

    override fun loadCustomer(uid: String) {
        mSubscription = APIManager.getInstance().getApi().postCheckCustomer(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveUserInfoSuccess, this::onRetrieveUserInfoFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun onRetrieveUserInfoSuccess(model: CheckCustomerResponse?) {
        if ((model != null && model.status == 200) || (model != null && model.status == 201)) {
            Config.uid = model.data.uid
            Config.session_id = model.data.session_id
            mHelper?.setUserInfo(model)
            if (model.data.alt_info.size > 0) {
                for (alt in model.data.alt_info) {
                    if (alt.is_default_address) {
                        val userInfoInvoiceModel = CheckCustomerResponse()
                        userInfoInvoiceModel.data.alt_info = ArrayList()
                        userInfoInvoiceModel.data.alt_info.add(ContactInfo())
                        userInfoInvoiceModel.data.alt_info[0] = alt
                        mHelper?.setUserInfoTemp(userInfoInvoiceModel)
                    }
                    break
                }
            }
            mView.sendSuccess(model)
        } else {
            mView.sendError(mContext.getString(R.string.cant_get_user_info))
        }
    }

    private fun onRetrieveUserInfoFailed(message: Throwable) {
        mView.sendError(mContext.getString(R.string.cant_get_user_info))
    }

}