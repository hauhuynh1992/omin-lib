package com.bda.omnilibrary.ui.accountAcitity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.ConfirmSignOutDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.accountAcitity.editDeliveryAddressFragment.EditDeliveryAddressFragment
import com.bda.omnilibrary.ui.accountAcitity.editProfileFragment.EditProfileFragment
import com.bda.omnilibrary.ui.accountAcitity.orderFragment.OrderFragment
import com.bda.omnilibrary.ui.accountAcitity.profileFragment.ProfileFragment
import com.bda.omnilibrary.ui.accountAcitity.wishListFragment.WishListFragment
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.image_top
import kotlinx.android.synthetic.main.fragment_edit_delivery_address.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.item_activity_header.*
import java.lang.ref.WeakReference


class AccountActivity : BaseActivity() {
    private var isRequestInvoiceFirst = false
    private var currentMenu = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        isRequestInvoiceFirst = intent.getBooleanExtra("ISINVOICE", false)
        weakActivity = WeakReference(this@AccountActivity)

        initial()
    }

    private fun initial() {
        if (isRequestInvoiceFirst) {
            btn_invoice.isSelected = true
            loadFragment(OrderFragment.newInstance(), R.id.frameLayout, false)
        } else {
            Handler().postDelayed({
                layout_btn_invoice.requestFocus()
            }, 0)
            loadFragment(OrderFragment.newInstance(), R.id.frameLayout, false)
        }
        if (LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()&&LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPVNPT.toString()) {
            layout_btn_sign_out.visibility = View.GONE
        }
        layout_btn_account.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val textColorId = ContextCompat.getColorStateList(
                    this,
                    R.color.color_white
                )
                layout_btn_account.background =
                    ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_center_focus)
                layout_btn_account.bringToFront()
                btn_account.setTextColor(textColorId)
                img_profile.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_white
                    )
                )

            } else {
                if (currentMenu == 1) {
                    Handler().postDelayed({
                        layout_btn_account.setBackgroundResource(R.mipmap.ic_menu_account_center_active)
                        btn_account.setTextColor(ContextCompat.getColor(this, R.color.end_color))
                        img_profile.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.end_color
                            )
                        )
                        layout_btn_account.bringToFront()
                    }, 0)


                } else {
                    val textColorId = ContextCompat.getColorStateList(
                        this,
                        R.color.title_black_black
                    )
                    layout_btn_account.background =
                        ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_center)
                    btn_account.setTextColor(textColorId)
                    img_profile.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.title_black_black
                        )
                    )
                }
            }
        }
        layout_btn_invoice.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                val textColorId = ContextCompat.getColorStateList(
                    this,
                    R.color.color_white
                )
                layout_btn_invoice.background =
                    ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_top_focus)
                layout_btn_invoice.bringToFront()
                btn_invoice.setTextColor(textColorId)

                img_cart.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_white
                    )
                )

            } else {
                if (currentMenu == 0) {
                    Handler().postDelayed({
                        layout_btn_invoice.setBackgroundResource(R.mipmap.ic_menu_account_top_active)
                        btn_invoice.setTextColor(ContextCompat.getColor(this, R.color.end_color))
                        img_cart.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.end_color
                            )
                        )
                        layout_btn_invoice.bringToFront()
                    }, 0)

                } else {
                    val textColorId = ContextCompat.getColorStateList(
                        this,
                        R.color.title_black_black
                    )
                    layout_btn_invoice.background =
                        ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_top)

                    btn_invoice.setTextColor(textColorId)
                    img_cart.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.title_black_black
                        )
                    )
                }
            }
        }

        layout_btn_wish_list.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                //btn_wish_list.isSelected = true
                val textColorId = ContextCompat.getColorStateList(
                    this,
                    R.color.color_white
                )
                layout_btn_wish_list.background =
                    ContextCompat.getDrawable(
                        this,
                        R.mipmap.ic_menu_account_bot_focus.takeIf { LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()&&LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPVNPT.toString() }
                            ?: R.mipmap.ic_menu_account_center_focus
                    )
                layout_btn_wish_list.bringToFront()
                btn_wish_list.setTextColor(textColorId)

                img_love.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_white
                    )
                )
            } else {
                //btn_wish_list.isSelected = false
                if (currentMenu == 2) {
                    Handler().postDelayed({
                        layout_btn_wish_list.setBackgroundResource(R.mipmap.ic_menu_account_bot_active.takeIf { LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()&&LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPVNPT.toString() }
                            ?: R.mipmap.ic_menu_account_center_active)
                        btn_wish_list.setTextColor(ContextCompat.getColor(this, R.color.end_color))
                        img_love.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.end_color
                            )
                        )
                        layout_btn_wish_list.bringToFront()
                    }, 0)
                } else {
                    val textColorId = ContextCompat.getColorStateList(
                        this,
                        R.color.title_black_black
                    )
                    layout_btn_wish_list.background =
                        ContextCompat.getDrawable(
                            this,
                            R.mipmap.ic_menu_account_bot.takeIf { LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString() &&LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPVNPT.toString()}
                                ?: R.mipmap.ic_menu_account_center
                        )

                    btn_wish_list.setTextColor(textColorId)
                    img_love.setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.title_black
                        )
                    )
                }
            }
        }
//        layout_btn_notification.setOnFocusChangeListener { view, hasFocus ->
//            if (hasFocus) {
//                //btn_notification.isSelected = true
//                val textColorId = ContextCompat.getColorStateList(
//                    this,
//                    R.color.color_white
//                )
//
//
//                layout_btn_notification.background =
//                    ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_center_focus)
//                layout_btn_notification.bringToFront()
//                btn_notification.setTextColor(textColorId)
//
//                img_bell.setColorFilter(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.title_white
//                    )
//                )
//            } else {
//                // btn_notification.isSelected = false
//                if (currentMenu == 3) {
//                    Handler().postDelayed({
//                        layout_btn_notification.setBackgroundResource(R.mipmap.ic_menu_account_center_active)
//                        btn_notification.setTextColor(resources.getColor(R.color.end_color))
//                        img_bell.setColorFilter(
//                            ContextCompat.getColor(
//                                this,
//                                R.color.end_color
//                            )
//                        )
//                        layout_btn_notification.bringToFront()
//                    }, 0)
//
//                } else {
//                    val textColorId = ContextCompat.getColorStateList(
//                        this,
//                        R.color.title_black_black
//                    )
//
//                    layout_btn_notification.background =
//                        ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_center)
//
//                    btn_notification.setTextColor(textColorId)
//                    img_bell.setColorFilter(
//                        ContextCompat.getColor(
//                            this,
//                            R.color.title_black_black
//                        )
//                    )
//                }
//            }
//        }

        layout_btn_sign_out.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                btn_sign_out.isSelected = true
                val textColorId = ContextCompat.getColorStateList(
                    this,
                    R.color.color_white
                )

                layout_btn_sign_out.background =
                    ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_bot_focus)
                layout_btn_sign_out.bringToFront()
                btn_sign_out.setTextColor(textColorId)

                img_door.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_white
                    )
                )
            } else {
                btn_sign_out.isSelected = true
                val textColorId = ContextCompat.getColorStateList(
                    this,
                    R.color.title_black_black
                )

                layout_btn_sign_out.background =
                    ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_bot)

                btn_sign_out.setTextColor(textColorId)
                img_door.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_black_black
                    )
                )
            }
        }

        layout_btn_account.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
            dataObject.menuName = Config.MENU_ACCOUNT_ID.MY_ACCOUNT.name
            logTrackingVersion2(
                QuickstartPreferences.CLICK_LEFT_MENU_v2,
                Gson().toJson(dataObject).toString()
            )

            currentMenu = 1
            makeAllMenuNormal()
            layout_btn_account.bringToFront()
            loadFragment(ProfileFragment.newInstance(), R.id.frameLayout, false)
        }
        layout_btn_invoice.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
            dataObject.menuName = Config.MENU_ACCOUNT_ID.ORDERS_LIST.name
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_LEFT_MENU_v2,
                data
            )
            currentMenu = 0
            makeAllMenuNormal()
            layout_btn_invoice.bringToFront()
            loadFragment(OrderFragment.newInstance(), R.id.frameLayout, false)
        }
        layout_btn_wish_list.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
            dataObject.menuName = Config.MENU_ACCOUNT_ID.WISH_LIST.name
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_LEFT_MENU_v2,
                data
            )
            currentMenu = 2
            makeAllMenuNormal()
            layout_btn_wish_list.bringToFront()
            loadFragment(WishListFragment.newInstance(), R.id.frameLayout, false)
        }

//        layout_btn_notification.setOnClickListener {
        //currentMenu = 3
        // makeAllMenuNormal()
        //layout_btn_notification.bringToFront()
//            loadFragment(NotificationFragment.newInstance(), R.id.frameLayout, false)
//        }
        layout_btn_sign_out.setOnClickListener {
            ConfirmSignOutDialog(this, R.string.text_sign_out_app, {
                getPreferenceHelper()?.setUserInfo(null)
                finishAffinity()
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra(QuickstartPreferences.RESTART, true)
                startActivity(intent)
            }, {

            })
        }
    }

    private fun makeAllMenuNormal() {
        val textColorId = ContextCompat.getColorStateList(
            this,
            R.color.title_black_black
        )
        ///account
        if (currentMenu != 1) {
            layout_btn_account.background =
                ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_center)
            btn_account.setTextColor(textColorId)
            img_profile.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.title_black_black
                )
            )
        }
        ///invoice
        if (currentMenu != 0) {
            layout_btn_invoice.background =
                ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_top)

            btn_invoice.setTextColor(textColorId)
            img_cart.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.title_black_black
                )
            )
        }

        ///buy_later
//        if (currentMenu != 4) {
//            layout_btn_buy_later.background =
//                ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_center)
//
//            btn_buy_later.setTextColor(textColorId)
//            img_clock.setColorFilter(
//                ContextCompat.getColor(
//                    this,
//                    R.color.title_black_black
//                )
//            )
//        }

        //whist list
        if (currentMenu != 2) {
            layout_btn_wish_list.background =
                ContextCompat.getDrawable(this,
                    R.mipmap.ic_menu_account_bot.takeIf { LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()&&LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPVNPT.toString() }
                        ?: R.mipmap.ic_menu_account_center
                )

            btn_wish_list.setTextColor(textColorId)
            img_love.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.title_black
                )
            )
        }
        ///notify
//        if (currentMenu != 3) {
//            layout_btn_notification.background =
//                ContextCompat.getDrawable(this, R.mipmap.ic_menu_account_center)
//
//            btn_notification.setTextColor(textColorId)
//            img_bell.setColorFilter(
//                ContextCompat.getColor(
//                    this,
//                    R.color.title_black_black
//                )
//            )
//        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        when (keyCode) {
            KeyEvent.KEYCODE_MENU -> {

            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {

            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (event!!.action == KeyEvent.ACTION_DOWN) {
                    val currentFragment: Fragment? =
                        getFManager().findFragmentById(R.id.frameLayout)

//                    if (layout_btn_buy_later.hasFocus()) {
//                        Handler().postDelayed({
//                            layout_btn_sign_out.requestFocus()
//                        }, 100)
//                    }

                    if (currentFragment is OrderFragment) {
                        if (currentFragment.rvMenu.hasFocus()) {
                            Handler().postDelayed({
                                currentFragment.rvList.requestFocus()
                            }, 0)
                        }
                    }

                    if (currentFragment is EditDeliveryAddressFragment) {
                        if (currentFragment.edt_name_delivery.hasFocus() || currentFragment.bn_delete_name_delivery.hasFocus() || currentFragment.bn_voice_name_delivery.hasFocus()) {
                            Handler().postDelayed({
                                currentFragment.edt_phone_delivery.requestFocus()
                            }, 0)

                        }
                    }

//                    if (currentFragment is NotificationFragment) {
//                        if (currentFragment.rvMenu.hasFocus()) {
//                            currentFragment.menuAdapter.notifyDataSetChanged()
//                            if (currentFragment.deliveryFilterAdapter.itemCount > 0) {
//                                Handler().postDelayed({
//                                    currentFragment.rvList.requestFocus()
//                                }, 100)
//
//                            } else {
//                                Handler().postDelayed({
//                                    currentFragment.bn_continue_shopping.requestFocus()
//                                    layout_btn_notification.background =
//                                        ContextCompat.getDrawable(this, R.drawable.menu_item_focus)
//                                }, 100)
//
//                            }
//                        }
//                    }
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (event!!.action == KeyEvent.ACTION_DOWN) {
                    val currentFragment: Fragment? =
                        getFManager().findFragmentById(R.id.frameLayout)

                    if (currentFragment is ProfileFragment) {
                        if (currentFragment.rvList.hasFocus()) {
                            if (currentFragment.rvList[0].hasFocus()) {
                                bn_cart.requestFocus()
                            }
                        }
                    }

                    if (currentFragment is WishListFragment) {
                        if (currentFragment.rvList.hasFocus()) {
                            if (currentFragment.rvList[0].hasFocus()) {
                                bn_cart.requestFocus()
                            }
                        }
                    }

                    if (currentFragment is OrderFragment) {
                        if (currentFragment.rvList.hasFocus()) {
                            Log.d("AAA-",currentFragment.deliveryFilterAdapter.getCurrentPositionFocus().toString())
                            if (currentFragment.deliveryFilterAdapter.getCurrentPositionFocus() == 0) {
                                Handler().postDelayed({
                                    currentFragment.rvMenu.requestFocus()
                                }, 0)
                            }
                        }
                    }
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                val currentFragment: Fragment? =
                    getFManager().findFragmentById(R.id.frameLayout)
                if (event!!.action == KeyEvent.ACTION_DOWN) {
                    if (currentMenu == 0) {///OrderFragment
                        Handler().postDelayed({
                            layout_btn_invoice.setBackgroundResource(R.mipmap.ic_menu_account_top_active)
                            btn_invoice.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.end_color
                                )
                            )
                            img_cart.setColorFilter(
                                ContextCompat.getColor(
                                    this,
                                    R.color.end_color
                                )
                            )
                            layout_btn_invoice.bringToFront()
                        }, 0)
                    }

                    if (currentMenu == 1) {//ProfileFragment
                        Handler().postDelayed({
                            layout_btn_account.setBackgroundResource(R.mipmap.ic_menu_account_center_active)
                            btn_account.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.end_color
                                )
                            )
                            img_profile.setColorFilter(
                                ContextCompat.getColor(
                                    this,
                                    R.color.end_color
                                )
                            )
                            layout_btn_account.bringToFront()
                        }, 0)

                    }


                    if (currentMenu == 2) {//WishListFragment
                        Handler().postDelayed({
                            layout_btn_wish_list.setBackgroundResource(R.mipmap.ic_menu_account_bot_active.takeIf { LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString() &&LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPVNPT.toString()}
                                ?: R.mipmap.ic_menu_account_center_active)
                            btn_wish_list.setTextColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.end_color
                                )
                            )
                            img_love.setColorFilter(
                                ContextCompat.getColor(
                                    this,
                                    R.color.end_color
                                )
                            )
                            layout_btn_wish_list.bringToFront()
                            if (currentFragment is WishListFragment) {
                                if (currentFragment.favouriteAdatper.getNumOfItems() == 0) {
                                    currentFragment.rvList.requestFocus()
                                }
                            }
                        }, 0)

                    }

//                    if (currentMenu == 4) {//BuyLaterFragment
//                        Handler().postDelayed({
//                            layout_btn_buy_later.setBackgroundResource(R.mipmap.ic_menu_account_center_active)
//                            btn_buy_later.setTextColor(resources.getColor(R.color.end_color))
//                            img_clock.setColorFilter(
//                                ContextCompat.getColor(
//                                    this,
//                                    R.color.end_color
//                                )
//                            )
//                            layout_btn_buy_later.bringToFront()
//                        }, 0)

//                    }


//                    if (currentMenu == 3) {//NotificationFragment
//                        Handler().postDelayed({
//                            layout_btn_notification.setBackgroundResource(R.mipmap.ic_menu_account_center_active)
//                            btn_notification.setTextColor(resources.getColor(R.color.end_color))
//                            img_bell.setColorFilter(
//                                ContextCompat.getColor(
//                                    this,
//                                    R.color.end_color
//                                )
//                            )
//                            layout_btn_notification.bringToFront()
//                        }, 0)
//                    }
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (event!!.action == KeyEvent.ACTION_DOWN) {
                    val currentFragment: Fragment? =
                        getFManager().findFragmentById(R.id.frameLayout)

                    if (currentFragment is OrderFragment) {
                        if (currentFragment.rvMenu.hasFocus()) {
                            if (currentFragment.rvMenu[0].hasFocus()) {
                                layout_btn_invoice.requestFocus()
                            }

                        }
                        if (currentFragment.rvList.hasFocus()) {
                            layout_btn_invoice.requestFocus()
                        }
                    } else if (currentFragment is ProfileFragment) {
                        if (currentFragment.rvList.hasFocus()) {
                            layout_btn_account.requestFocus()
                        }
                    } else if (currentFragment is EditDeliveryAddressFragment) {
                        if (currentFragment.bn_voice_name_delivery.visibility == View.GONE) {
                            if (currentFragment.bn_delete_name_delivery.hasFocus()) {
                                Handler().postDelayed({
                                    edt_name_delivery.requestFocus()
                                }, 0)

                            }
                        } else if (currentFragment.bn_voice_name_delivery.hasFocus()) {
                            Handler().postDelayed({
                                edt_name_delivery.requestFocus()
                            }, 0)
                        }
                        if (currentFragment.bn_voice_phone_delivery.visibility == View.GONE) {
                            if (currentFragment.bn_delete_phone_delivery.hasFocus()) {
                                Handler().postDelayed({
                                    edt_phone_delivery.requestFocus()
                                }, 0)
                            }
                        } else if (currentFragment.bn_voice_phone_delivery.hasFocus()) {
                            Handler().postDelayed({
                                edt_phone_delivery.requestFocus()
                            }, 0)
                        }
                        if (currentFragment.edt_name_delivery.hasFocus()
                            || currentFragment.edt_phone_delivery.hasFocus()
                            || currentFragment.edt_delivery_address.hasFocus()
                            || currentFragment.rl_house.hasFocus()
                            || currentFragment.rl_default.hasFocus()
                            || currentFragment.rl_update.hasFocus()
                        ) {
                            layout_btn_account.requestFocus()
                        }

                    } else if (currentFragment is EditProfileFragment) {
                        if (currentFragment.edt_name_profile.hasFocus()
                            || currentFragment.edt_phone_profile.hasFocus()
                            || currentFragment.edt_email.hasFocus()
                            || currentFragment.tv_gender.hasFocus()
                            || currentFragment.layout_male.hasFocus()
                            || currentFragment.tv_birth_day.hasFocus()
                            || currentFragment.edt_date.hasFocus()
                            || currentFragment.bn_update_profile.hasFocus()
                            || currentFragment.rl_house_profile.hasFocus()
                            || currentFragment.edt_address_profile.hasFocus()
                        ) {
                            layout_btn_account.requestFocus()
                        }
                    }
                }
            }
            else -> return super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        initChildHeader(image_top, Tab.ACCOUNT)

        if (currentMenu == 0) {
            Handler().postDelayed({
                val currentFragment: Fragment? =
                    getFManager().findFragmentById(R.id.frameLayout)
                (currentFragment as OrderFragment).scrollToLastPosition()
            }, 0)
        }

        if (currentMenu == 2) {
            Handler().postDelayed({
                layout_btn_wish_list.requestFocus()
            }, 0)
        }
        if (MainActivity.getMaiActivity() != null && !MainActivity.getMaiActivity()!!
                .checkIsPlayingBackground()
        ) {
            MainActivity.getMaiActivity()?.playMusicBackground()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (checkPermissions()) {
                val currentFragment: Fragment? =
                    getFManager().findFragmentById(R.id.frameLayout)
                if (currentFragment is EditDeliveryAddressFragment) {
                    currentFragment.showInputAddressDialog()
                }

                if (currentFragment is EditProfileFragment) {
                    currentFragment.showInputAddressDialog()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in getFManager().fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@AccountActivity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    companion object {
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

        private var weakActivity: WeakReference<AccountActivity>? = null

        fun getMaiActivity(): AccountActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment: Fragment? =
            getFManager().findFragmentById(R.id.frameLayout)
        if (currentFragment is EditProfileFragment
            || currentFragment is EditDeliveryAddressFragment
        ) {
            loadFragment(ProfileFragment.newInstance(), R.id.frameLayout, false)
            reloadProfile()
        } else {
            super.onBackPressed()
        }

    }

    fun updateCart() {
        initChildHeader(image_top, Tab.ACCOUNT)
    }

    fun reloadProfile() {
        Handler().postDelayed({
            layout_btn_account.requestFocus()
        }, 0)
    }
}