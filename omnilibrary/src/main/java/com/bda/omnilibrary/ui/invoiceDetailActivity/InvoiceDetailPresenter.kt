package com.bda.omnilibrary.ui.invoiceDetailActivity

import android.content.Context
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class InvoiceDetailPresenter(
    view: InvoiceDetailContact.View,
    @Suppress("UNUSED_PARAMETER") context: Context
) :
    InvoiceDetailContact.Presenter {
    private var mView: InvoiceDetailContact.View = view
    private var mSubscription: Disposable? = null

    private var list = ArrayList<String>()

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun getListProductFromOrder(list: ArrayList<ListOrderResponceV3.Data.Item>) {
        this.list.clear()

        for (i in list) {
            this.list.add(i.itemUid)
        }

        if (list.size > 0 && Regex("^0[xX][a-fA-F0-9]+\$").matches(this.list[0])) {
            mSubscription = APIManager.getInstance().getApi()
                .getProductFromId(ProductByUiRequest(this.list))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataProductSuccess, this::failedProduct)
        }
    }

    private fun onRetrieveDataProductSuccess(response: ProductByUiResponse) {
        if (response.status == 200 && !response.data.isNullOrEmpty()) {
            mView.sendListProductSuccessful(response.data)
        } else {
            mView.sendListProductFalse()
        }
    }

    private fun failedProduct(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendListProductFalse()

    }

    override fun cancelOrderByOrder(orderId: String) {

        mSubscription?.dispose()
        mSubscription = null
        mSubscription = APIManager.getInstance().getApi()
            .pushOrderStatus(PushOrderStatus(orderId, 5, QuickstartPreferences.CLIENT_NEW_CANCEL))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                this::onRetrieveChangeStatusSuccessByOrder,
                this::onRetrieveChangeStatusFailedByOrder
            )
    }

    private fun onRetrieveChangeStatusSuccessByOrder(@Suppress("UNUSED_PARAMETER") model: PushOrderStatus?) {
        mView.cancelOrderSuccess()
    }

    private fun onRetrieveChangeStatusFailedByOrder(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.cancelOrderFailed()

    }
}