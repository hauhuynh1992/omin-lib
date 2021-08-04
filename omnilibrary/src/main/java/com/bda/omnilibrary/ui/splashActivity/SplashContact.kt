package com.bda.omnilibrary.ui.splashActivity

import com.bda.omnilibrary.model.HistoryModel
import com.bda.omnilibrary.model.HomeModel

class SplashContact {
    /**
     * Represents the View in MVP.
     */
    interface View {
        fun sendSplashUISussess(model: HomeModel)
        fun sendSplashUIFail(erroMessage: String)
        fun sendBoxDataFail(erroMessage: String)
    }

    /**
     * Represents the Presenter in MVP.
     */
    interface Presenter {
        fun loadSplash(token: String, historyModel: HistoryModel?)
        fun loadUser(phone: String, history: HistoryModel)
        fun disposeAPI()
    }
}