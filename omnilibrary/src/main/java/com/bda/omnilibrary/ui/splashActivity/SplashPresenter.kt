package com.bda.omnilibrary.ui.splashActivity

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Contract
import com.bda.omnilibrary.util.ImageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashPresenter(view: SplashContact.View, context: Context) : SplashContact.Presenter {

    private var mView: SplashContact.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var mPhone = ""
    private var historyModel: HistoryModel? = null
    private var token: String = ""
    private var fptUrl = ""

    init {
        this.mHelper = PreferencesHelper(mContext)
    }

    override fun loadSplash(token: String, historyModel: HistoryModel?) {
        this.token = token
        this.historyModel = historyModel
        getConfig()
    }

    override fun loadUser(phone: String, history: HistoryModel) {
        this.mPhone = phone
        this.historyModel = history
        getConfig()
    }


    private fun getConfig() {
        mSubscription = APIManager.getInstance().getApi().getConfig()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveConfigDataSuccess, this::onRetrieveConfigDataFailed)
    }

    /**
     * Fetch all product
     */
    private fun getData() {
        historyModel!!.customer_id = Config.uid
        mSubscription = APIManager.getInstance().getApi().postHome(historyModel!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProductDataSuccess, this::onRetrieveProductDataFailed)

    }

    private fun getUserInfo(token: String) {
        val map = HashMap<String, String>()
        // todo change, using user typing
        map["token"] = token

        mSubscription = APIManager.getInstance().getApi().postCheckCustomer(map)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveUserInfoSuccess, this::onRetrieveUserInfoFailed)
    }


    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }


    private fun onRetrieveConfigDataSuccess(model: ConfigModel?) {
        if (model != null && model.status == 200) {
            ImageUtils.localUrl = model.config.url
            ImageUtils.videoUrl = model.config.video_url
            this.fptUrl = model.config.urlFpt
            Config.configModel = model
            Config.evironment = model.config.env
            getUserInfo(token)
        } else {
            mView.sendSplashUIFail(mContext.getString(R.string.cant_get_config))
        }
    }

    private fun onRetrieveProductDataSuccess(model: HomeModel?) {
        if (model != null && model.statusCode == 200) {
            mView.sendSplashUISussess(model)
        } else {
            mView.sendSplashUIFail(mContext.getString(R.string.cant_get_product))
        }
    }

    private fun onRetrieveUserInfoSuccess(model: CheckCustomerResponse?) {
        if ((model != null && model.status == 200) || (model != null && model.status == 201)) {
            Config.uid = model.data.uid
            Config.session_id = model.data.session_id
            Config.user_id=model.data.fptplay_id
            Config.user_phone=model.data.phone
            mHelper.setUserInfo(model)
            if (model.data.alt_info.size > 0) {
                for (alt in model.data.alt_info) {
                    if (alt.is_default_address) {
                        val userInfoInvoiceModel = CheckCustomerResponse()
                        userInfoInvoiceModel.data.alt_info = ArrayList()
                        userInfoInvoiceModel.data.alt_info.add(ContactInfo())
                        userInfoInvoiceModel.data.alt_info[0] = alt
                        mHelper.setUserInfoTemp(userInfoInvoiceModel)
                    }
                    break
                }
            }
            getData()
        } else {
            mView.sendSplashUIFail(mContext.getString(R.string.cant_get_user_info))
        }
    }


    private fun onRetrieveUserInfoFailed(message: Throwable) {
        Log.e("Trung", "onRetrieveUserInfoFailed $message")

        mView.sendSplashUIFail(mContext.getString(R.string.cant_get_user_info) + "Box ID")
    }


    private fun onRetrieveProductDataFailed(message: Throwable) {
        Log.e("Trung", "onRetrieveProductDataFailed ${message.message}")
        mView.sendSplashUIFail(mContext.getString(R.string.cant_get_product))
    }

    private fun onRetrieveConfigDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendSplashUIFail(mContext.getString(R.string.cant_get_config))
    }

}