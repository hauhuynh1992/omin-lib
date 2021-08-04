package com.bda.omnilibrary.ui.baseActivity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.LogRequest
import com.bda.omnilibrary.util.Config
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

abstract class BaseTracking : FragmentActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mSubscription: Disposable? = null
    private var databaseTrackingHelper: DatabaseHandler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        this.databaseTrackingHelper = DatabaseHandler(this)
    }

    /*
    *  Internal Tracking ver2
    * */

    fun logTrackingVersion2(
        action: String,
        data: String
    ) {
        val logRequest = LogRequest()
        logRequest.timestamp = Calendar.getInstance().timeInMillis
        logRequest.platform = Config.platform
        logRequest.mac_address = Config.macAddress
        logRequest.action = action
        logRequest.session_id = Config.session_id
        logRequest.app_version = LibConfig.VERSION_NAME
        logRequest.user_id = Config.user_id
        logRequest.uid = Config.uid
        logRequest.user_phone = Config.user_phone

        val info: LogDataRequest = Gson().fromJson(
            data,
            object : TypeToken<LogDataRequest>() {}.type
        )

        // Screen
        info.screen?.let {
            logRequest.screen = it
        }

        // section name
        info.sectionName?.let {
            logRequest.sectionName = it
        }
        info.sectionIndex?.let {
            logRequest.sectionIndex = it
        }
        info.sectionType?.let {
            logRequest.sectionType = it
        }

        // Pop-up
        info.source?.let {
            logRequest.utmSource = it
        }
        info.medium?.let {
            logRequest.utmMedium = it
        }
        info.content?.let {
            logRequest.utmContent = it
        }
        info.campaign?.let {
            logRequest.utmCampaign = it
        }
        logRequest.data = data

        // Internal tracking
        mSubscription =
            APIManager.getInstance().getApi()
                .logShoppingTracking(QuickstartPreferences.BASE_TRACKING_URL, logRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                },
                    {
                        val log = Gson().toJson(logRequest).toString()
                        databaseTrackingHelper?.insertLog(log)
                    }
                )

        // Log firebase
        val bundle = Bundle()
        bundle.putString("timestamp", Calendar.getInstance().timeInMillis.toString())
        bundle.putString("platform", Config.platform)
        bundle.putString("mac_address", Config.macAddress)
        bundle.putString("session_id", Config.session_id)
        bundle.putString("app_version", LibConfig.VERSION_NAME)
        bundle.putString("user_id", Config.user_id)
        bundle.putString("uid", Config.uid)
        bundle.putString("user_phone", Config.user_phone)
        bundle.putString("data", data)
        mFirebaseAnalytics?.logEvent(action, bundle)
    }

    fun sendListLog() {
        val listLog = databaseTrackingHelper?.getTrackingLog()

        val myType = object : TypeToken<ArrayList<LogRequest>>() {}.type
        val logs = Gson().fromJson<ArrayList<LogRequest>>(listLog.toString(), myType)
        mSubscription =
            APIManager.getInstance().getApi()
                .logShoppingListTracking(QuickstartPreferences.BASE_LIST_TRACKING_URL, logs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                    { _ ->
                        // Success
                        databaseTrackingHelper?.deleteLogTracking()
                    },
                    { _ ->
                        // Error DO NOTHING
                    })
    }


    fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }
}