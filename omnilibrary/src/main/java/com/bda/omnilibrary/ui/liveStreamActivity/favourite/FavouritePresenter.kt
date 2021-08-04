package com.bda.omnilibrary.ui.liveStreamActivity.favourite

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FavouritePresenter(view: FavouriteContract.View, context: Context) :
    FavouriteContract.Presenter {
    private var mView: FavouriteContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var request: PaymentRequest? = null
    private var userInfo: CheckCustomerResponse? = null

    private var list: ArrayList<Product>? = null

    private var voucher = ""
    private var voucherId = ""

    init {
        this.mHelper = PreferencesHelper(mContext)

        if (mHelper.userInfo != null) {
            userInfo = Gson().fromJson(
                mHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
    }

    /////////////// public function ///////////////

    override fun postAddFavourite(productID: String) {
        mSubscription =
            APIManager.getInstance().getApi().postAddFavourite(FavouriteResquest(userInfo!!.data.uid, productID))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveAddFavouriteSuccess, this::onRetrieveFavouriteFailed)
    }

    override fun getListFavourite(page: Int) {
        mView.showProgress()
        mSubscription = APIManager.getInstance().getApi()
            .getListFavourite(userInfo!!.data.uid, page)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .doOnTerminate {
                mView.hideProgress()
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    /////////////// private handle retrieve data ///////////////

    private fun onRetrieveAddFavouriteSuccess(@Suppress("UNUSED_PARAMETER") model: FavouriteResponse) {
        mView.sendAddFavouriteSuccess()
    }

    private fun onRetrieveFavouriteFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendFalsed(mContext.getString(R.string.fail))
    }

    private fun onRetrieveDataSuccess(model: FavouriteProductResponse) {
        mView.sendListFavouriteSuccess(model.data)
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        Log.d("CartPresenter", message.message.toString())
        mView.sendFalsed(mContext.getString(R.string.fail))
    }
}