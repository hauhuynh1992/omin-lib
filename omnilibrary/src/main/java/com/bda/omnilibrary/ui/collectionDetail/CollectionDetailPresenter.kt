package com.bda.omnilibrary.ui.collectionDetail

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.ChildDetails
import com.bda.omnilibrary.model.HistoryModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CollectionDetailPresenter(view: CollectionDetailContract.View, context: Context) :
    CollectionDetailContract.Presenter {

    private var mView: CollectionDetailContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var preferencesHelper = PreferencesHelper(context)

    override fun getCollectionProduct(uid: String, type: String, length: Int, page: Int) {
        mSubscription = APIManager.getInstance().getApi()
            .getSubcollection(uid, type, length, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProductSuccess, this::onRetrieveProductFailed)
    }

    override fun loadAddHistory(uid: String) {
        mSubscription = Observable.just(addHistory(uid))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe()
    }

    override fun loadAddHistoryBrandShop(uid: String, brandId: String) {
        mSubscription = Observable.just(addHistoryBrandShop(uid, brandId))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe()
    }

    private fun onRetrieveProductSuccess(model: ChildDetails) {
        @Suppress("SENSELESS_COMPARISON")
        if (model != null && model.statusCode == 200) {
            if (model.data.products.size > 0) {
                mView.sendSuccessProducts(model)
            }
        } else {
            mView.sendFalsed(mContext.getString(R.string.fail))
        }

    }

    private fun onRetrieveProductFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendFalsed(mContext.getString(R.string.fail))
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun getBrandShopChildCollection(uid: String, type: String, length: Int, page: Int) {
        mSubscription = APIManager.getInstance().getApi()
            .getBrandShopChildCollection(uid, type, length, page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProductSuccess, this::onRetrieveProductFailed)
    }

    private fun addHistory(uid: String) {
        val json = preferencesHelper.history
        var history = HistoryModel(ArrayList(), "")
        @Suppress("SENSELESS_COMPARISON")
        if (json != null && json.isNotEmpty()) {
            history = Gson().fromJson(
                json,
                object : TypeToken<HistoryModel>() {}.type
            )
            for (position in 0 until history.uids.size) {
                if (history.uids[position] == uid) {
                    history.uids.removeAt(position)
                    break
                }
            }
            history.uids.add(uid)
            if (history.uids.size > 20) {
                history.uids.removeAt(0)
            }
            preferencesHelper.setHistory(Gson().toJson(history))
        } else {
            history.uids.add(uid)
            preferencesHelper.setHistory(Gson().toJson(history))
        }
    }

    private fun addHistoryBrandShop(uid: String, brandId: String) {
        addHistory(uid)
        val json = preferencesHelper.brandShopHistory(brandId)
        var history = HistoryModel(ArrayList(), "")
        if (json != null && json.isNotEmpty()) {
            history = Gson().fromJson(
                json,
                object : TypeToken<HistoryModel>() {}.type
            )
            for (position in 0 until history.uids.size) {
                if (history.uids[position] == uid) {
                    history.uids.removeAt(position)
                    break
                }
            }
            history.uids.add(uid)
            if (history.uids.size > 20) {
                history.uids.removeAt(0)
            }
            preferencesHelper.setBrandShopHistory(Gson().toJson(history), brandId)
        } else {
            history.uids.add(uid)
            preferencesHelper.setBrandShopHistory(Gson().toJson(history), brandId)
        }
    }
}