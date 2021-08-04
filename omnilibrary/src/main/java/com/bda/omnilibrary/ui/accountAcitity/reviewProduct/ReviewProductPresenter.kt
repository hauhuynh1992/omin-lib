package com.bda.omnilibrary.ui.accountAcitity.reviewProduct

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.ListOrderResponce
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ReviewProductPresenter(view: ReviewProductContact.View, context: Context) :
    ReviewProductContact.Presenter {
    private var mView: ReviewProductContact.View = view
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

    override fun loadPresenter() {
        val map = HashMap<String, String>()
        // todo change, using user typing
        map["customer_id"] = userInfo!!.data.uid
        mSubscription = APIManager.getInstance().getApi()
            .postListOrder(map)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate { }
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

    private fun onRetrieveDataSuccess(@Suppress("UNUSED_PARAMETER") model: ListOrderResponce) {
//        if (model!!.status == 200 && (model!!.filter_orders.processing.size > 0 || model!!.filter_orders.success.size > 0)) {
//            mView.sendSuccess(model)
//
//        } else {
//            mView.sendFalsed(R.string.error_cant_get_delivery)
//        }
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        Log.d("CartPresenter", message.message.toString())
        mView.sendFalsed(R.string.error_cant_get_delivery)
    }


    ////////////// private method ////////////////
}