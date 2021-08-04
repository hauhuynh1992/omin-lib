package com.bda.omnilibrary.ui.categoryDetail

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CategoryDetailPresenter(view: CategoryDetailContract.View, context: Context) :
    CategoryDetailContract.Presenter {

    private var mView: CategoryDetailContract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var preferencesHelper = PreferencesHelper(context)

    override fun loadProductMore(productMoreRequestModel: ProductMoreRequestModel) {
        mSubscription = APIManager.getInstance().getApi()
            .postListProduct(productMoreRequestModel)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
    }

    override fun getSubCollectionHome(uid: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getSubcollection(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveSubCollectionHomeSuccess, this::onRetrieveDataFailed)
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

    private fun onRetrieveDataSuccess(model: ProductMoreModel) {
        @Suppress("SENSELESS_COMPARISON")
        if (model.statusCode == 200 && model.data != null) {
            mView.sendProductsSuccess(model)
        } else {
            mView.sendFalsed(mContext.getString(R.string.product_not_avalable))
        }
    }

    private fun onRetrieveSubCollectionHomeSuccess(model: SubCollection) {
        @Suppress("SENSELESS_COMPARISON")
        if (model != null && model.statusCode == 200) {
            if (model.data.size > 0 || model.hotdeal.products.size > 0) {
                mView.sendCollectionsHomeSuccess(model)
            } else {
                mView.sendFalsed(mContext.getString(R.string.fail))
            }
        } else {
            mView.sendFalsed(mContext.getString(R.string.fail))
        }

    }

    private fun onRetrieveDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendFalsed(mContext.getString(R.string.fail))
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun getSubCollectionBrandShop(brandId: String, collectionId: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getBrandShopCollection(brandId, collectionId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveSubCollectionHomeSuccess, this::onRetrieveDataFailed)
    }

    override fun loadProductMoreBrandShop(productMoreRequestModel: ProductBrandShopMoreRequestModel) {
        mSubscription = APIManager.getInstance().getApi()
            .postListProductBrandShop(productMoreRequestModel)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
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

    override fun loadPopup(popUprequest: PopUpRequest) {
        mSubscription =
            APIManager.getInstance().getApi().getListPopup(popUprequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveListPopUpSuccess, this::onRetrieveListPopUpFailed)
    }

    override fun sendActivePopUp(activePopUpRequest: ActivePopUpRequest) {
        mSubscription =
            APIManager.getInstance().getApi().postActivePopup(activePopUpRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveActivePopUpSuccess, this::onRetrieveActivePopUpFailed)
    }

    override fun callApiProduct(uid: String) {
        if (Regex("^0[xX][a-fA-F0-9]+\$").matches(uid)) {
            mSubscription = APIManager.getInstance().getApi().getProductFromId(
                ProductByUiRequest(
                    arrayListOf(uid)
                )
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataProductSuccess, this::failedProduct)
        }
    }

    override fun getNewArrivalProducts(uid: String, page: Int, length: Int) {
        mSubscription =
            APIManager.getInstance().getApi().getNewArrivalProduct(uid, page, length)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                    this::onRetrieveNewArrivalProductSuccess,
                    this::onRetrieveActivePopUpFailed
                )
    }

    private fun onRetrieveDataProductSuccess(response: ProductByUiResponse) {
        if (response.status == 200 && !response.data.isNullOrEmpty()) {
            mView.sendProductSuccess(response.data[0])
        }
    }

    private fun onRetrieveNewArrivalProductSuccess(response: ProductMoreModel) {
        @Suppress("SENSELESS_COMPARISON")
        if (response.statusCode == 200 && response.data != null) {
            mView.sendProductsSuccess(response)
        } else {
            mView.sendFalsed(mContext.getString(R.string.product_not_avalable))
        }
    }

    private fun failedProduct(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveListPopUpSuccess(model: PopUpResponse) {
        @Suppress("SENSELESS_COMPARISON", "NullChecksToSafeCall")
        if (model != null && model.result != null && model.result.size > 0) {
            mView.sendListPopUpSussess(model)
        }
    }

    private fun onRetrieveListPopUpFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveActivePopUpSuccess(@Suppress("UNUSED_PARAMETER") model: ActivePopUpResponse) {

    }

    private fun onRetrieveActivePopUpFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }
}