package com.bda.omnilibrary.ui.brandShopDetail

import android.content.Context
import android.util.Log
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

class BrandShopDetailPresenter(view: BrandShopDetailContract.View, context: Context) :
    BrandShopDetailContract.Presenter {
    private var mView: BrandShopDetailContract.View = view
    private var mSubscription: Disposable? = null
    private lateinit var uid: String
    private lateinit var brandShop: BrandShop
    private var preferencesHelper = PreferencesHelper(context)

    override fun loadPresenter(uid: String) {
        this.uid = uid
        callDetail()
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
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

    override fun loadAddHistory(uid: String) {
        mSubscription = Observable.just(addHistoryBrandShop(uid))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe()
    }

    private fun callDetail() {
        mSubscription = APIManager.getInstance().getApi().getBrandShopDetail(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDetailSuccess, this::onRetrieveDetailFailed)
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

    private fun onRetrieveDataProductSuccess(response: ProductByUiResponse) {
        if (response.status == 200 && !response.data.isNullOrEmpty()) {
            mView.sendProductSuccess(response.data[0])
        }
    }

    private fun failedProduct(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveHomeSuccess(model: BrandShopModel) {
        @Suppress("NullChecksToSafeCall", "SENSELESS_COMPARISON")
        if (model != null && model.data != null) {
            mView.getSuccess(brandShop, model)

        } else {
            mView.getHomeFailed(R.string.error_home_brand_shop)
        }
    }

    private fun onRetrieveHomeFailed(message: Throwable) {
        Log.d("CartPresenter", message.message.toString())
        mView.getHomeFailed(R.string.error_home_brand_shop)
    }

    private fun onRetrieveDetailSuccess(model: BrandShopResponse) {
        @Suppress("SENSELESS_COMPARISON")
        if (model != null && model.statusCode == 200) {
            this.brandShop = model.brandShop

            mSubscription =
                APIManager.getInstance().getApi().getHomeBrandShop(uid, getHistory(brandShop.uid))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(this::onRetrieveHomeSuccess, this::onRetrieveHomeFailed)

        } else {
            mView.getDetailFailed(R.string.error_detail)
        }
    }

    private fun onRetrieveDetailFailed(message: Throwable) {
        Log.d("CartPresenter", message.message.toString())
        mView.getDetailFailed(R.string.error_detail)
    }

    private fun addHistoryBrandShop(uid: String) {
        addHistory(uid)
        val json = preferencesHelper.brandShopHistory(brandShop.uid)
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
            preferencesHelper.setBrandShopHistory(Gson().toJson(history), brandShop.uid)
        } else {
            history.uids.add(uid)
            preferencesHelper.setBrandShopHistory(Gson().toJson(history), brandShop.uid)
        }
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

    private fun getHistory(brandShopId: String): HistoryModel {
        var checkCustomerResponse: CheckCustomerResponse? = null
        if (preferencesHelper.userInfo != null) {
            checkCustomerResponse = Gson().fromJson(
                preferencesHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }

        var history = HistoryModel(ArrayList(), "")
        if (preferencesHelper.brandShopHistory(brandShopId) != "") {
            history = Gson().fromJson(
                preferencesHelper.brandShopHistory(brandShopId),
                object : TypeToken<HistoryModel>() {}.type
            )
        }

        if (checkCustomerResponse != null) {
            history.customer_id = checkCustomerResponse.data.uid
        }

        return history
    }

    private fun onRetrieveListPopUpSuccess(model: PopUpResponse) {
        @Suppress("SENSELESS_COMPARISON")
        if (model.result != null && model.result.size > 0) {
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