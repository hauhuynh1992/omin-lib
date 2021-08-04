package com.bda.omnilibrary.ui.accountAcitity.wishListFragment

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.FavouriteProductResponse
import com.bda.omnilibrary.model.FavouriteResquest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WishListPresenter(view: WishListContact.View, context: Context) : WishListContact.Presenter {
    private var mView: WishListContact.View = view
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

    override fun getRemoveFavourite(productId: String) {
        val request = FavouriteResquest(
            customer_id = userInfo!!.data.uid,
            product_uid = productId
        )
        mView.showProgress()
        mSubscription =
            APIManager.getInstance().getApi().postDeleteFavourite(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .doOnTerminate {
                    mView.hideProgress()
                }
                .subscribe({
                    mView.sendRemoveFavouriteSuccess(request.product_uid)
                }, this::onRetrieveDataFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }

        databaseHandler = null
    }

    /////////////// private handle retrieve data ///////////////

    private fun onRetrieveDataSuccess(model: FavouriteProductResponse) {
        mView.sendSuccess(model.data)
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        Log.d("CartPresenter", message.message.toString())
        mView.sendFalsed(R.string.error_cant_get_delivery)
    }

    ////////////// private method ////////////////
}