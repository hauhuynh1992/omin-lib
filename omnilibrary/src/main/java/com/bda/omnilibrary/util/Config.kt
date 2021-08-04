package com.bda.omnilibrary.util

import com.bda.omnilibrary.model.ConfigModel
import com.bda.omnilibrary.model.HomeModel
import com.bda.omnilibrary.model.MegaMenuResponse


object Config {
    var configModel: ConfigModel? = null
    var popup_boxid = "0x107d04"

    //var popup_boxid = "0x77d2"
    var evironment = "production"
    var hcm = "0x11e0bd"
    var hn = "0x11e112"
    var orther = "000000"
    var platform = "box2019"
    var macAddress = ""
    var user_phone = ""
    var user_id = ""
    var uid = ""
    var oncart = ""
    var oncartdetail = ""
    var session_id = ""
    var lastKeyDownTime = 200
    var developer_mode = 0
    var isVoucher = 900000000
    var isNotVoucher = 1000000000
    var developer = "050620"
    var content = "060620"
    var homeData: HomeModel? = null
    val freeShipValue = 200000
    var megaMenu: MegaMenuResponse.MegaMenu? = null

    enum class PARTNER {
        FPT, OMNISHOPVNPT, VIETTEL, OMNISHOPEU
    }

    enum class LANGUAGE {
        vi, en
    }

    enum class POPUP_TYPE {
        PRODUCT, COLLECTION, COLLECTION_TEMP, BRANDSHOP, NONE,LANDINGPAGE
    }

    enum class SCREEN_ID {
        SPLASH,
        HOME,
        COLLECTION,
        SEARCH,
        CART,
        ACCOUNT,
        ALL_ORDERS,
        PAYMENT_PENDING_ORDERS,
        ON_GOING_ORDERS,
        DELIVERING_ORDERS,
        DELIVERED_ORDERS,
        CANCELED_ORDERS,
        ON_GOING_REFUND_ORDERS,
        REFUNDED_ORDERS,
        ORDER_DETAILS,
        WISHLIST,
        EDIT_PROFILE,
        CREATE_RECIPIENT,
        EDIT_RECIPIENT,
        REMOVE_RECIPIENT_CONFIRM_POP_UP,
        EDIT_ADDRESS_DIALOG,
        PRODUCT_DETAIL,
        ADD_TO_CART_POP_UP,
        VIDEO_FULL_SCREEN,
        LIVESTREAM_SCREEN,
        IMAGE_FULL_SCREEN,
        CATEGORY_DEAL_TAB,
        CATEGORY_CHILD_TAB,
        CATEGORY_DETAIL,
        BRAND_SHOP_CATEGORY_DETAIL,
        COLLECTION_DETAIL,
        BRAND_SHOP_COLLECTION_DETAIL,
        QUICK_PAY,
        BRANDSHOP,
        VOUCHER_LIST,
        VOUCHER_INPUT,
        CHECKOUT_RESULT,
        CHECKOUT_DETAILS,
        ASK_FOR_VOUCHER_POP_UP,
        ASK_FOR_EXIST_PAYMENT_POP_UP,
        CHANGE_RECIPIENT,
        PAYMENT_METHOD_SELECT,
        MOMO_CHECKOUT,
        VNPAY_CHECKOUT,
        MOCA_CHECKOUT,
        COD_CHECKOUT,
        PAYMENT_CANCEL_CONFIRMATION_POP_UP,
        PROMOTION_DETAIL
    }

    enum class MENU_ACCOUNT_ID {
        MY_ACCOUNT,
        ORDERS_LIST,
        WISH_LIST
    }

    enum class ASSISTANT_VOICE(val voice: String) {
        // Vietnamese voice
        NHU_THUY("vi-VN-Wavenet-A"),
        DUC_TRUNG("vi-VN-Wavenet-B"),
        NGOC_CHAU("vi-VN-Wavenet-C"),
        MINH_HIEN("vi-VN-Wavenet-D"),

        // English voice
        SUNNY("en-US-Wavenet-G"),
        PHILLIP("en-US-Wavenet-I"),
        ROSE("en-US-Wavenet-F"),
        JACK("en-US-Wavenet-J"),
    }
}