package com.bda.omnilibrary.ui.mainActivity

import com.bda.omnilibrary.model.*

class MainActivityContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendCartOnlineSuccess(cart: CartModel)
        fun sendCartOnlineFail(error: String)
        fun sendProductSuccess(product: Product)
        fun sendSkyFail(erroMessage: String)
        fun sendSkySussess(model: SkyMusicResponse)
        fun sendListMusicFail(erroMessage: String)
        fun sendListMusicSussess(model: MusicResponse)
        fun sendListPopUpFail(erroMessage: String)
        fun sendListPopUpSussess(model: PopUpResponse)
        fun sendActivePopUpFail(erroMessage: String)
        fun sendActivePopUpSussess(model: ActivePopUpResponse)
        fun sendSplashUISussess(model: HomeModel)
        fun sendSplashUIFail(erroMessage: String)
        fun sendLandingPageSuccess(cart: LandingPageModel)
        fun sendOrderMegaMenu(quantity: Int, supplier: String, date: Int)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadAddHistory(uid: String)
        fun loadLandingPage(uid: String)
        fun editOnlineCart(cardRequest: CartRequest)
        fun loadOnlineCart(uid: String)
        fun callApiProduct(uid: String)
        fun loadSkyMusic(key: String)
        fun loadListMusic()
        fun loadPopup(popUprequest: PopUpRequest)
        fun sendActivePopUp(activePopUpRequest: ActivePopUpRequest)
        fun loadHomeData(historyModel: HistoryModel)
        fun loadOrderMegaMenu(uid: String)
        fun disposeAPI()
    }
}