package com.bda.omnilibrary.ui.promotionDetail

import android.content.Context
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.model.PromotionResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PromotionDetailPresenter(val Context: Context, val view: PromotionDetailContract.View) :
    PromotionDetailContract.Presenter {

    private var mSubscription: Disposable? = null


    override fun getPromotion(id: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getPromotion(id)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    private fun onRetrieveProfileDataSuccess(model: PromotionResponse) {
        if (model.statusCode == 200 && model.data != null && model.data.size > 0) {
            view.sendSuccess(model.data[0])
        } else {
            view.sendFalsed(model.message)
        }
    }

    private fun onRetrieveProfileDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        view.sendFalsed(message.message.toString())
    }

    override fun disposeAPI() {
        if (mSubscription != null)
            mSubscription!!.dispose()
    }
}