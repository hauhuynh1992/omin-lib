package com.bda.omnilibrary.ui.liveStreamActivity.detailLiveStreamProduct

import android.content.Context
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DetailLiveStreamProductPresenter
    (view: DetailLiveStreamProductContract.View, context: Context) :
    DetailLiveStreamProductContract.Presenter {

    private var mView: DetailLiveStreamProductContract.View = view
    private var mSubscription: Disposable? = null

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun loadFavourite(customer_id: String, product_id: String) {
        mSubscription =
            APIManager.getInstance().getApi().getSingleFavourite(customer_id, product_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveFavouriteSuccess, this::onRetrieveFavouriteFailed)
    }

    override fun postDeleteFavourite(request: FavouriteResquest) {
        mSubscription =
            APIManager.getInstance().getApi().postDeleteFavourite(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDeleteFavouriteSuccess, this::onRetrieveFavouriteFailed)
    }

    private fun onRetrieveFavouriteSuccess(model: FavouriteResponse) {
        if (model.checkStatus) {
            mView.sendLoadFavouriteSuccess(model)
        }
    }

    private fun onRetrieveFavouriteFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveDeleteFavouriteSuccess(@Suppress("UNUSED_PARAMETER") model: FavouriteResponse) {

    }
}
