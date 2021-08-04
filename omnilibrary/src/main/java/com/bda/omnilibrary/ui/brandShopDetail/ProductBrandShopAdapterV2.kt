package com.bda.omnilibrary.ui.brandShopDetail

import com.bda.omnilibrary.adapter.homev2.ProductAdapterV2
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity

class ProductBrandShopAdapterV2(
    activity: BaseActivity,
    list: ArrayList<Product>,
    positionOnList: Int = 1,
    private val isLoadMore: Boolean,
    private val collectionId: String = "",
    private val collectionName: String = "",
) : ProductAdapterV2(activity, list, positionOnList, isLoadMore, collectionId, collectionName) {

    override fun isHasBrandSkin(product: Product): Boolean {
        var isHasTag = true
        product.tags.forEach {
            if (it.tag_category == "assess") {
                return false
            }
        }
        if (product.brand_shop == null) {
            return false
        } else {
            product.brand_shop!!.forEach {
                return it.skin_display_at_brandshop
            }
        }
        return isHasTag
    }
}