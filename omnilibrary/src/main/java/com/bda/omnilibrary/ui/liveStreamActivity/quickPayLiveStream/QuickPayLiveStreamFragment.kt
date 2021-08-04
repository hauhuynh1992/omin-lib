package com.bda.omnilibrary.ui.liveStreamActivity.quickPayLiveStream

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.ui.liveStreamActivity.paymentMethod.PaymentMethodFragment
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_NAME_CODE
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_PHONE_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_live_stream_quick_pay.*

class QuickPayLiveStreamFragment : LiveStreamBaseFragment(), QuickPayLiveStreamContract.View {

    private lateinit var product: Product
    private var quatity: Int = 1
    private var mName = ""
    private var mPhone = ""
    private lateinit var presenter: QuickPayLiveStreamContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Gson().fromJson(
                it.getString(STR_PRODUCT),
                Product::class.java
            )
        }

        presenter = QuickPayLiveStreamPresenter(this, context!!)
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live_stream_quick_pay, container, false)
    }

    override fun onDestroy() {
        presenter?.disposeAPI()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (lastFocusId() == null) {
            setLastFocusId(R.id.edt_name)
        }

        lastFocus = view.findViewById(lastFocusId()!!)

        val customerInfo = (activity as BaseActivity).getCheckCustomerResponse()

        if (customerInfo != null)
            presenter.fetchProfile(customerInfo)

        price.text = Functions.formatMoney(product.price)
        name.text = product.display_name_detail

        ImageUtils.loadImage(activity!!, image, product.imageCover, ImageUtils.TYPE_PRIVIEW_SMALL)

        setSetQuantity(quatity)
        if (arrayListOf("box2019", "box2020","omnishopeu","box2021").contains(Config.platform)) {
            bn_voice_name.visibility=View.VISIBLE
            bn_voice_phone.visibility=View.VISIBLE
        }else{
            bn_voice_name.visibility=View.GONE
            bn_voice_phone.visibility=View.GONE
        }
        bn_minus?.apply {
            setOnClickListener {
                if (quatity > 1)
                    setSetQuantity(--quatity)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Functions.animateScaleUpLiveStream(rl_quantity, 1.05f)
                    image_bn_minus.setColorFilter(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.color_A1B753
                        )
                    )
                    rl_quantity.isSelected = true
                } else {
                    image_bn_minus.setColorFilter(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.color_484848
                        )
                    )

                    if (!bn_minus.hasFocus() && !bn_plus.hasFocus()) {
                        Functions.animateScaleDown(rl_quantity)
                        rl_quantity.isSelected = false
                    }
                }
            }
        }

        bn_plus?.apply {
            setOnClickListener {
                setSetQuantity(++quatity)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Functions.animateScaleUpLiveStream(rl_quantity, 1.05f)

                    image_bn_plus.setColorFilter(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.color_A1B753
                        )
                    )
                    rl_quantity.isSelected = true
                } else {
                    image_bn_plus.setColorFilter(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.color_484848
                        )
                    )

                    if (!bn_minus.hasFocus() && !bn_plus.hasFocus()) {
                        rl_quantity.isSelected = false
                        Functions.animateScaleDown(rl_quantity)
                    }
                }
            }
        }

        bn_confirm?.apply {
            setOnClickListener {
                product.order_quantity = quatity

                if (edt_phone__ != null && edt_phone__.text.isNotEmpty()) {
                    mName = edt_name?.text.toString()
                    mPhone = edt_phone__?.text.toString()

                    if (mPhone.length > 9) {
                        (activity as BaseActivity).focusDummyView()
                        setLastFocusId(it.id)

                        val f = PaymentMethodFragment.newInstance(product, mName, mPhone)
                        (activity as BaseActivity).loadFragment(
                            f,
                            (activity as BaseActivity).layoutToLoadId(),
                            true
                        )

                    } else {
                        Functions.showMessage(activity!!, getString(R.string.wrong_phone_number))
                    }
                } else {
                    Functions.showMessage(activity!!, getString(R.string.enter_your_phone_number))
                }
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (bn_confirm != null)
                    if (hasFocus) {
                        Functions.animateScaleUp(bn_confirm, 1.05f)
                        text_bn_confirm.setNewTextColor(R.color.color_white)
                    } else {
                        Functions.animateScaleDown(bn_confirm)
                        text_bn_confirm.setNewTextColor(R.color.color_A1B753)
                    }
            }
        }

        edt_name?.apply {
            setOnClickListener {
                if (edt_name != null) {
                    val keyboardDialog = KeyboardDialog(
                        KeyboardDialog.KEYBOARD_NAME_TYPE,
                        edt_name?.text.toString(),
                        edt_name, edt_name
                    )
                    keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
                }
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (edt_name != null)
                    if (hasFocus) {
                        Functions.animateScaleUp(edt_name, 1.01f)
                        edt_name?.setSelection(edt_name.text.length)
                    } else {
                        Functions.animateScaleDown(edt_name)
                    }
            }

            setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                    return@OnKeyListener true
                }

                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                    return@OnKeyListener true
                }

                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.action == KeyEvent.ACTION_DOWN) {
                    Handler().postDelayed({
                        edt_phone__?.requestFocus()
                    }, 0)
                    return@OnKeyListener true
                }
                false
            })

        }

        bn_voice_name.setOnClickListener {
            (activity as BaseActivity).apply {
                gotoDiscoveryVoice(REQUEST_VOICE_NAME_CODE)
            }
        }

        bn_voice_name.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                image_bn_voice_name.setColorFilter(
                    ContextCompat.getColor(
                        context!!,
                        R.color.color_A1B753
                    )
                )

                Functions.animateScaleUp(bn_voice_name, 1.01f)
            } else {
                image_bn_voice_name.setColorFilter(
                    ContextCompat.getColor(
                        context!!,
                        R.color.color_484848
                    )
                )
                Functions.animateScaleDown(bn_voice_name)
            }
        }

        edt_phone__?.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_phone__?.text.toString(),
                edt_phone__, edt_phone__
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_phone__?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(edt_phone__, 1.01f)
                edt_phone__?.setSelection(edt_phone__.text.length)
            } else {
                Functions.animateScaleDown(edt_phone__)
            }
        }

        edt_phone__?.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    edt_name?.requestFocus()
                }, 0)
                return@OnKeyListener true
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    bn_minus.requestFocus()
                }, 0)
                return@OnKeyListener true
            }

            false
        })

        bn_voice_phone.setOnClickListener {
            (activity as BaseActivity).apply {

                gotoDiscoveryVoice(REQUEST_VOICE_PHONE_CODE)
            }
        }

        bn_voice_phone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                image_bn_voice_phone.setColorFilter(
                    ContextCompat.getColor(
                        context!!,
                        R.color.color_A1B753
                    )
                )

                Functions.animateScaleUp(bn_voice_phone, 1.01f)
            } else {
                image_bn_voice_phone.setColorFilter(
                    ContextCompat.getColor(
                        context!!,
                        R.color.color_484848
                    )
                )
                Functions.animateScaleDown(bn_voice_phone)
            }
        }
    }

    private fun setSetQuantity(q: Int) {
        quantity.text = "$q"
    }

    fun myOnkeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        return false
    }

    override fun requestFocus() {
        Handler().postDelayed({
            lastFocus?.requestFocus()
        }, 100)
    }

    override fun isAbleToHideByClickLeft() = bn_minus.hasFocus() || bn_confirm.hasFocus()

    override fun onResume() {
        super.onResume()
        requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
        if (result != null && result.isNotBlank()) {
            when (requestCode) {
                /*GOTO_DETAIL_INVOICE_REQUEST -> {
                    val orderId = data!!.getStringExtra("ORDER_ID")
                    //deliveryFilterAdapter.deleteOrderById(orderId)
                }*/
                REQUEST_VOICE_PHONE_CODE -> {


                    if (Functions.checkPhoneNumber(result.trim().replace(" ", ""))) {
                        edt_phone__?.setText(result.trim().replace(" ", ""))

                    } else {
                        Functions.showMessage(
                            activity!!,
                            getString(R.string.text_error_phone)
                        )
                    }

                }
                REQUEST_VOICE_NAME_CODE -> {
                    edt_name?.setText(result.trim())

                }
            }
        }
    }


    companion object {
        val GOTO_DETAIL_INVOICE_REQUEST = 123
        private val STR_PRODUCT = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            QuickPayLiveStreamFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(product))
                }
            }
    }

    override fun sendAddressSuccess(data: CheckCustomerResponse) {
        edt_name?.setText(data.data.alt_info[0].customer_name)
        edt_phone__?.setText(data.data.alt_info[0].phone_number)

        Handler().postDelayed({
            bn_confirm?.requestFocus()
        }, 100)
    }

    override fun sendAddressFailed(message: String) {
        Functions.showMessage(activity!!, message)
    }

}