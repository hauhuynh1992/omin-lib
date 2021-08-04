package com.bda.omnilibrary.ui.promotionDetail

import android.annotation.SuppressLint
import android.os.Bundle
import com.bda.omnilibrary.R
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Promotion
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_promotion_detail.*

class PromotionDetailActivity : BaseActivity(), PromotionDetailContract.View {
    private lateinit var adapter: PromotionDetailAdapter
    private var promotion: Promotion? = null
    private lateinit var descripton_html: String
    private var uid: String = ""
    private lateinit var presenter: PromotionDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion_detail)
        val bundle = intent?.extras
        bundle?.let {
            if (it.keySet().contains("UID")){
                uid = it.getString("UID", "")
            } else if (it.keySet().contains(QuickstartPreferences.PROMOTION_MODEL)){
                promotion = Gson().fromJson(
                    bundle.getString(QuickstartPreferences.PROMOTION_MODEL),
                    Promotion::class.java
                )
            }
        }
        presenter = PromotionDetailPresenter(this, this)

        when {
            uid != "" -> {
                presenter.getPromotion(uid)
            }
            promotion != null -> {
                initial()
            }
            else -> {
                finish()
            }
        }

        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.PROMOTION_DETAIL.name
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_PROMOTION_DETAIL_v2,
            data
        )
    }

    @SuppressLint("RestrictedApi")
    private fun initial() {
        promotion?.let {
            adapter = PromotionDetailAdapter(this, it)
            adapter.setOnCallbackListener(object : PromotionDetailAdapter.OnCallBackListener {
                override fun onClickUnderstand() {
                    it.button_direct?.let {
                        when (it.direct) {
                            PROMOTION_TYPE_PRODUCT -> {
                                gotoProductDetail(
                                    it.value,
                                    0,
                                    Config.SCREEN_ID.PROMOTION_DETAIL.name
                                )
                            }
                            PROMOTION_TYPE_LIVESTREAM -> {
                                gotoLiveStreamProduct(it.value)
                            }
                            PROMOTION_TYPE_COLLECTION-> {
                                gotoCategory(it.value, getString(R.string.app_name))
                            }
                            PROMOTION_TYPE_COLLECTION_TEMP -> {
                                gotoCollection(it.value, getString(R.string.app_name))
                            }
                            PROMOTION_TYPE_BRAND_SHOP -> {
                                gotoBrandShopDetail(it.value)
                            }
                            else -> {
                                finish()
                            }
                        }
                    }

                }
            })
            rv_home.extraLayoutSpace = 800
            rv_home.setItemViewCacheSize(20)
            rv_home.setHasFixedSize(true)
            rv_home.adapter = adapter
        }
    }

    companion object {
        const val PROMOTION_TYPE_PRODUCT = "product"
        const val PROMOTION_TYPE_LIVESTREAM = "livestream"
        const val PROMOTION_TYPE_COLLECTION_TEMP = "collection_temp"
        const val PROMOTION_TYPE_COLLECTION = "collection"
        const val PROMOTION_TYPE_BRAND_SHOP = "brand_shop"
    }

    override fun sendSuccess(promotion: Promotion) {
        this.promotion = promotion
        initial()
    }

    override fun sendFalsed(message: String) {
        Functions.showMessage(this, message)
    }
}


