package com.bda.omnilibrary.ui.searchActivity

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.HightlightModel
import com.bda.omnilibrary.model.SearchProductResponse
import com.bda.omnilibrary.model.SearchRequestModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchPresenter(view: SearchContact.View, @Suppress("unused") private val context: Context) :
    SearchContact.Presenter {
    private var mView: SearchContact.View = view
    private var mSubscription: Disposable? = null

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun callSearchProduct(key: String) {
        callApi(key)
    }

    override fun callSuggestKeywords(key: String) {
        // todo call api
    }

    private fun callApi(keyword: String) {
        mSubscription = APIManager.getInstance().getApi().searchProduct(SearchRequestModel(keyword))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataCountSuccess, this::failedCount)
    }


    private fun callHightlight() {
        mSubscription = APIManager.getInstance().getApi().getSuggestedProducts()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveHightlightSuccess, this::failedHightlight)
    }

    private fun onRetrieveDataCountSuccess(response: SearchProductResponse) {
        if (response.status == 200) {
            if (response.data.size > 0 || response.brand_Shop.size > 0) {
                mView.sendSuccess(response.data, response.brand_Shop)
            } else {
                //list = ArrayList()
                callHightlight()
            }
        }
    }

    private fun failedCount(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        callHightlight()
        //mView.sendFail(R.string.error)
    }

    private fun onRetrieveHightlightSuccess(response: HightlightModel) {
        if (response.statusCode == 200) {
            mView.sendHightlightSuccess(response.products)
        } else {
            mView.sendHightlightSuccess(ArrayList())
        }
    }

    private fun failedHightlight(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        //mView.sendFail(R.string.error)
        mView.sendHighlightFail(R.string.error)
    }
}