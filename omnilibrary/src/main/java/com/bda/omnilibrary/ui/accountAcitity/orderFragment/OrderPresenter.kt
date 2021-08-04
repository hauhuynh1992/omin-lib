package com.bda.omnilibrary.ui.accountAcitity.orderFragment

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.ListOrderResponce
import com.bda.omnilibrary.model.ListOrderResponceV3
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OrderPresenter(view: OrderContact.View, context: Context) : OrderContact.Presenter {
    private var mView: OrderContact.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var databaseHandler: DatabaseHandler?
    private var userInfo: CheckCustomerResponse? = null

    init {
        this.mHelper = PreferencesHelper(mContext)
        this.databaseHandler = DatabaseHandler(context)

        if (mHelper.userInfo != null) {
            userInfo = Gson().fromJson(
                mHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
    }
    /////////////// public function ///////////////

    override fun getOrderByStatus(status: String, page: Int) {
        mView.showLoading()
        val map = HashMap<String, String>()
        map["customer_id"] = userInfo!!.data.uid
        map["order_by"] = status
        map["length"] = "20"
        map["page"] = page.toString()
        mSubscription = APIManager.getInstance().getApi()
            .postListOrderV3(map)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate {
                mView.hideLoading()
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }

        databaseHandler = null
    }

    /////////////// private handle retrieve data ///////////////

    private fun onRetrieveDataSuccess(model: ListOrderResponceV3) {
        mView.sendSuccess(model.data)

    }

    private fun onRetrieveDataFailed(message: Throwable) {
        Log.d("CartPresenter", message.message.toString())
        mView.sendFalsed(R.string.error_cant_get_delivery)
    }


    ////////////// private method ////////////////
}