package com.bda.omnilibrary.dialog.assitant

import android.content.Context
import android.text.TextUtils
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences.BASE_TTS_URL
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Contract
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class AssistantPresenter(view: AssistantContract.View, private val context: Context) :
    AssistantContract.Presenter {

    private var mView: AssistantContract.View = view
    private var mSubscription: Disposable? = null
    private var preferencesHelper = PreferencesHelper(context)
    private lateinit var checkCustomerResponse: CheckCustomerResponse
    private var databaseHandler = DatabaseHandler(context)
    private var products: ArrayList<Product> = ArrayList()
    private var orderPhoneNumber: String? = null
    private var assistantContext: String? = null
    private var customerId: String? = null


    init {
        if (preferencesHelper.userInfo != null) {
            checkCustomerResponse = Gson().fromJson(
                preferencesHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    override fun getAssistant(speechText: String) {
        mView.showLoading()
        customerId = checkCustomerResponse.data.uid
        val appData =
            AppAssistantData(
                customerId,
                orderPhoneNumber,
                isFirstOpen = IS_FIRST_OPEN,
                langCode = getDefaultLanguageCode()
            )
        mSubscription =
            APIManager.getInstance().getApi()
                .getAssistant(QueryAssistant(speechText, products, appData, assistantContext))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                    IS_FIRST_OPEN = false
                    response.context?.let {
                        assistantContext = it
                    }
                    val intent = response.data
                    if (intent != null) {
                        when (intent) {
                            "it_view_cart" -> {
                                handelViewCart()
                            }
                            "it_product_detail" -> {
                                handelViewProductDetail(false)
                            }
                            "it_pay" -> {
                                handlePayment()
                            }
                            "it_add_to_cart" -> {
                                handleAddToCart()
                            }
                            else -> {
                                products = response.products
                                mView.showResult(
                                    products,
                                    response.suggestion,
                                    response.speechText ?: "",
                                    response.title
                                )

                            }
                        }
                    } else {
                        mView.showResult(
                            products,
                            response.suggestion,
                            response.speechText ?: "",
                            response.title
                        )
                    }
                },
                    { error ->
                        mView.sendFail(OTHER_ERROR_CODE, getDefaultLanguageCode())
                    }
                )
    }

    override fun speechText(text: String) {
        mView.showLoading()
        val voice = TTSRequest.Voice(
            languageCode = getDefaultLanguageCode(),
            name = if (getDefaultLanguageCode().equals("vi-VN")) Config.ASSISTANT_VOICE.NHU_THUY.voice else Config.ASSISTANT_VOICE.SUNNY.voice
        )
        val ttsRequest =
            TTSRequest(TTSRequest.AudioConfig(), TTSRequest.Input(text), voice)
        mSubscription =
            APIManager.getInstance().getApi().textToSpeech(BASE_TTS_URL, ttsRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ model ->
                    mView.speakOut(model.audioContent, getDefaultLanguageCode())
                }, {
                    refreshToken(Contract.googleTTStoken, text)
                })
    }

    private fun handelViewCart() {
        if (databaseHandler.getCountCartItem() > 0) {
            mView.gotoCart()
        } else {
            speechText(context.getString(R.string.tv_empty_cart))
        }
    }

    private fun handelViewProductDetail(isShowQuickPay: Boolean) {
        if (products.size == 0) {
            speechText(context.getString(R.string.tv_no_product_selected))
        } else if (products.size == 1) {
            mView.gotoDetailProduct(products[0], isShowQuickPay)
        } else {
            speechText(context.getString(R.string.tv_which_product_select))
        }
    }

    private fun handlePayment() {
        if (databaseHandler.getCountCartItem() > 0) {
            mView.gotoInvoice()
        } else {
            speechText(context.getString(R.string.tv_empty_cart))
        }
    }

    private fun handleAddToCart() {
        if (products != null) {
            if (products.size == 0) {
                speechText(context.getString(R.string.tv_empty_cart))
            } else if (products.size == 1) {
                mView.loadCart()
            } else {
                speechText(context.getString(R.string.tv_which_product_select_add_to_cart))
            }
        } else {
            speechText(context.getString(R.string.tv_empty_cart))
        }
    }


    override fun getDefaultLanguageCode(): String {
       return context.getString(R.string.lanCode)
    }

    private fun refreshToken(token: String, text: String) {
        mView.showLoading()
        mSubscription =
            APIManager.getInstance().getApi().getTextToSpeechToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ tokenResponse ->
                    Contract.googleTTStoken = tokenResponse.access_token
                    speechText(text)
                }, {
                    mView.sendFail(OTHER_ERROR_CODE, getDefaultLanguageCode())
                })
    }

    companion object {
        var IS_FIRST_OPEN: Boolean = true

        val OTHER_ERROR_CODE: String = "other_error_code"
        val EMPTY_CART_ERROR_CODE: String = "empty_cart_error_code"
        val EMPTY_DETAIL_PRODUCT_ERROR_CODE: String = "empty_detail_product_error_code"
        val EMPTY_ADD_TO_CART_PRODUCT_ERROR_CODE: String = "empty_add_to_cart_product_error_code"
        val MULTI_ADD_TO_CART_PRODUCT_ERROR_CODE: String = "multi_add_to_cart_product_error_code"
        val MULTI_DETAIL_PRODUCT_ERROR_CODE: String = "multi_detail_product_error_code"
        val ADD_TO_CART_CODE: String = "add_to_cart_code"
    }
}
