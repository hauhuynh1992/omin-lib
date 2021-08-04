package com.bda.omnilibrary.ui.mainActivity

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.util.Config
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MainActivityPresenter(view: MainActivityContact.View, context: Context) :
    MainActivityContact.Presenter {
    private var mView: MainActivityContact.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var preferencesHelper = PreferencesHelper(context)


    override fun loadAddHistory(uid: String) {
        mSubscription = Observable.just(addHistory(uid))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe()
    }

    override fun loadLandingPage(uid: String) {
        mSubscription =
            APIManager.getInstance().getApi()
                .getLandingPage(uid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                    this::onRetrieveDataLandingPageSuccess,
                    this::onRetrieveDataLandingPagefail
                )
    }

    override fun loadSkyMusic(
        key: String
    ) {
        mSubscription =
            APIManager.getInstance().getApi()
                .getSkyMusic("http://api.skysoundtrack.vn/song/detail", key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveSkyMusicSuccess, this::onRetrieveSkyMusicFailed)
    }

    override fun loadListMusic() {
        mSubscription =
            APIManager.getInstance().getApi().getMusic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveListMusicSuccess, this::onRetrieveListMusicFailed)
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

    override fun loadHomeData(historyModel: HistoryModel) {
        mSubscription = APIManager.getInstance().getApi().postHome(historyModel)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProductDataSuccess, this::onRetrieveProductDataFailed)
    }

    override fun loadOrderMegaMenu(uid: String) {
        val map = HashMap<String, String>()
        map["customer_id"] = uid

        mSubscription = APIManager.getInstance().getApi().orderMegaMenu(map)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveMegaMenuSuccess, this::onRetrieveProductDataFailed)
    }

    override fun editOnlineCart(cardRequest: CartRequest) {
        mSubscription = APIManager.getInstance().getApi().postSaveCart(cardRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataEditSuccess, this::onRetrieveDataEditFailed)
    }

    override fun loadOnlineCart(uid: String) {
        mSubscription = APIManager.getInstance().getApi().getCartItem(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
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

    private fun onRetrieveProductDataSuccess(model: HomeModel?) {
        if (model != null && model.statusCode == 200 && model.data.section.size > 0) {
            mView.sendSplashUISussess(model)
        } else {
            mView.sendSplashUIFail(mContext.getString(R.string.cant_get_product))
        }
    }

    private fun onRetrieveDataProductSuccess(response: ProductByUiResponse) {
        if (response.status == 200 && !response.data.isNullOrEmpty()) {
            mView.sendProductSuccess(response.data[0])
        }
    }

    private fun failedProduct(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveDataLandingPageSuccess(response: LandingPageModel) {
        if (response.statusCode == 200) {
            mView.sendLandingPageSuccess(response)
        }
    }

    private fun onRetrieveDataLandingPagefail(@Suppress("UNUSED_PARAMETER") message: Throwable) {

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

    private fun onRetrieveSkyMusicSuccess(model: SkyMusicResponse) {
        if (model.status.equals("ok", true)) {
            mView.sendSkySussess(model)
        } else {
            mView.sendSkyFail("fail")
        }
    }

    private fun onRetrieveDataSuccess(model: CartModel) {
        @Suppress("SENSELESS_COMPARISON")
        if (model.data != null) {
            mView.sendCartOnlineSuccess(model)
        } else {
            mView.sendCartOnlineFail(mContext.getString(R.string.error))
        }
    }

    private fun onRetrieveListMusicSuccess(model: MusicResponse) {
        @Suppress("SENSELESS_COMPARISON")
        if (model.result != null && model.result.size > 0) {
            mView.sendListMusicSussess(model)
        } else {
            mView.sendListMusicFail("Fail")
        }
    }

    private fun onRetrieveListMusicFailed(message: Throwable) {
        mView.sendListMusicFail(message.toString())
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

    private fun onRetrieveDataEditFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {

    }

    private fun onRetrieveDataEditSuccess(@Suppress("UNUSED_PARAMETER") model: CartModel) {

    }

    private fun onRetrieveSkyMusicFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendSkyFail("fail")
    }

    private fun onRetrieveDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendCartOnlineFail(mContext.getString(R.string.error))
    }

    private fun onRetrieveProductDataFailed(message: Throwable) {
        Log.e("Trung", "onRetrieveProductDataFailed ${message.message}")
        mView.sendSplashUIFail(mContext.getString(R.string.cant_get_product))
    }

    private fun onRetrieveMegaMenuSuccess(data: MegaMenuResponse) {
        if (data.statusCode == 200) {
            mView.sendOrderMegaMenu(
                data.megamenu.order_total,
                data.megamenu.order_collection.collection_name,
                data.megamenu.date_total_order
            )

            Config.megaMenu = data.megamenu
        }
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }
}