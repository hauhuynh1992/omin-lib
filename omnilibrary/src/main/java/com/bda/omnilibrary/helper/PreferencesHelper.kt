package com.bda.omnilibrary.helper

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.util.Config
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context) {
    private var myPrefs: SharedPreferences
    private var myEditors: SharedPreferences.Editor
    private val PREFS_NAME = "shopping"
    private val PREFS_USER_DEMO = "user_demo"
    private val PREFS_USER_INFO = "user_info"
    private val PREFS_USER_INFO_TEMP = "user_info_temp"
    private val PREFS_HISTORY = "history"
    private val PREFS_PROVINCE = "province"
    private val PREFS_LIST_PROVINCE = "listprovince"
    private val PREFS_DISTRICT = "district"
    private val PREFS_SHOW_CASE = "show_case"
    private val PREFS_FIRST_OPEN = "first_open"
    private val PREFS_FIRST_ASK_INFO = "firstTimeAskingInUsetInfo"
    private val PREFS_VOUCHER = "voucher"
    private val PREFS_DEV_MODE = "dev_mode"


    init {
        val sdkVersion = Build.VERSION.SDK_INT
        if (sdkVersion <= 10) {
            myPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        } else {
            myPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS)
        }
        myEditors = myPrefs.edit()
    }

    val userDemo: String?
        get() = myPrefs.getString(PREFS_USER_DEMO, null)

    fun setUserDemo(user: String): Boolean {
        myEditors.putString(PREFS_USER_DEMO, user)
        return myEditors.commit()
    }

    val showCase: Boolean
        get() = myPrefs.getBoolean(PREFS_SHOW_CASE, false)

    fun setShowCase(isShowCase: Boolean): Boolean {
        myEditors.putBoolean(PREFS_SHOW_CASE, isShowCase)
        return myEditors.commit()
    }

    val developerMode: Int
        get() = myPrefs.getInt(PREFS_DEV_MODE, 0)

    fun setDeveloperMode(isDev: Int): Boolean {
        //0 normal, 1 de_, 2 content
        myEditors.putInt(PREFS_DEV_MODE, isDev)
        return myEditors.commit()
    }

    val listProvince: String
        get() = myPrefs.getString(PREFS_LIST_PROVINCE, "").toString()

    fun setListProvince(listProvince: String): Boolean {
        myEditors.putString(PREFS_LIST_PROVINCE, listProvince)
        return myEditors.commit()
    }

    val firstOpen: Boolean
        get() = myPrefs.getBoolean(PREFS_FIRST_OPEN, true)

    fun setFirstOpen(isFirst: Boolean): Boolean {
        myEditors.putBoolean(PREFS_FIRST_OPEN, isFirst)
        return myEditors.commit()
    }

    val history: String
        get() = myPrefs.getString(PREFS_HISTORY, "").toString()

    fun setHistory(history: String): Boolean {
        myEditors.putString(PREFS_HISTORY, history)
        return myEditors.commit()
    }

    val currentProvince: String
        get() = myPrefs.getString(PREFS_PROVINCE, Config.hcm).toString()

    fun setCurrentProvince(province: String): Boolean {
        myEditors.putString(PREFS_PROVINCE, province)
        return myEditors.commit()
    }

    val currentDistrict: String
        get() = myPrefs.getString(PREFS_DISTRICT, "").toString()

    fun setCurrentDistrict(district: String): Boolean {
        myEditors.putString(PREFS_DISTRICT, district)
        return myEditors.commit()
    }

    val userInfo: String?
        get() = myPrefs.getString(PREFS_USER_INFO, null)

    fun setUserInfo(user: CheckCustomerResponse?): Boolean {
        if (user != null) {
            myEditors.putString(PREFS_USER_INFO, Gson().toJson(user))
        } else {
            myEditors.putString(PREFS_USER_INFO, null)
        }
        return myEditors.commit()
    }

    val userInfoTemp: String?
        get() = myPrefs.getString(PREFS_USER_INFO_TEMP, null)

    fun setUserInfoTemp(userInfoInvoiceModel: CheckCustomerResponse): Boolean {
        myEditors.putString(PREFS_USER_INFO_TEMP, Gson().toJson(userInfoInvoiceModel))
        return myEditors.commit()
    }

    fun firstTimeAsking(permission: String, isFirstTime: Boolean): Boolean {
        myEditors.putBoolean(permission, isFirstTime)
        return myEditors.commit()
    }

    fun isFirstTimeAsking(permission: String): Boolean {
        return myPrefs.getBoolean(permission, true)
    }

    fun firstTimeAskingInUsetInfo(isFirstTime: Boolean): Boolean {
        myEditors.putBoolean(PREFS_FIRST_ASK_INFO, isFirstTime)
        return myEditors.commit()
    }

    fun isFirstTimeAskingInUsetInfo(): Boolean {
        return myPrefs.getBoolean(PREFS_FIRST_ASK_INFO, true)
    }

    val vouchers: ArrayList<Voucher>?
        get() = Gson().fromJson(
            myPrefs.getString(PREFS_VOUCHER, ""),
            object : TypeToken<ArrayList<Voucher>>() {}.type
        )

    fun setVouchers(vouches: ArrayList<Voucher>): Boolean {
        myEditors.putString(PREFS_VOUCHER, Gson().toJson(vouches))
        return myEditors.commit()
    }


    // clear all object
    fun clearAllPreferences() {
        myEditors.clear()
        myEditors.commit()
    }

    fun brandShopHistory(uid: String): String? = myPrefs.getString("bs_$uid", "")

    fun setBrandShopHistory(history: String, uid: String): Boolean {
        myEditors.putString("bs_$uid", history)
        return myEditors.commit()
    }

}