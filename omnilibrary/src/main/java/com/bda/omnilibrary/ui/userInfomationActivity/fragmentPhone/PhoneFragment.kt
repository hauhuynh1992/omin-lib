package com.bda.omnilibrary.ui.userInfomationActivity.fragmentPhone


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.userInfomationActivity.UserInformationActivity
import com.bda.omnilibrary.ui.userInfomationActivity.fragmentName.NameFragment
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_PHONE_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_phone.*

class PhoneFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            arguments?.let {

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone, container, false)
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            PhoneFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as BaseActivity).initChildHeaderForFragment(image_header)
    }

    private fun initial() {
        if (arrayListOf("box2019", "box2020","omnishopeu","box2021").contains(Config.platform)) {
            bn_voice.visibility = View.VISIBLE
        } else {
            bn_voice.visibility = View.GONE
        }

        edt_phone__.setText((context as UserInformationActivity).userInfoTemp.data.phone)

        rl_bn_next.setOnClickListener {
            if (!Functions.checkPhoneNumber(edt_phone__.text.toString())) {
                Functions.showError(context!!, edt_phone__)
                Functions.showMessage(context!!, getString(R.string.wrong_phone_number))
            } else {
                activity?.let {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                    dataObject.milestone = getString(R.string.text_nhap_so_dt)//"Nhập số điện thoại"
                    val data = Gson().toJson(dataObject).toString()
                    (activity as UserInformationActivity).logTrackingVersion2(
                        QuickstartPreferences.CLICK_GO_NEXT_BUTTON_v2,
                        data
                    )
                }
                (context as UserInformationActivity).userInfoTemp.data.phone =
                    edt_phone__.text.toString()
                (context as UserInformationActivity).userInfoTemp.data.alt_info[0].phone_number =
                    edt_phone__.text.toString()
                (context as UserInformationActivity).loadFragment(
                    NameFragment.newInstance(), R.id.frameLayout, true
                )
            }
        }
        rl_bn_next.setOnFocusChangeListener { _, hasFocus ->
            Handler().postDelayed({
                bn_next?.isSelected = hasFocus
            }, 0)

        }
        bn_delete.setOnClickListener {
            activity?.let {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                dataObject.inputName = "Phone"
                dataObject.inputType = "KEYBOARD_PHONE_TYPE"
                val data = Gson().toJson(dataObject).toString()
                (activity as BaseActivity).logTrackingVersion2(
                    QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                    data
                )
            }
            edt_phone__.setText("")
        }
        bn_voice.setOnClickListener {
            edt_phone__.setText("")
            activity?.let {
                (it as UserInformationActivity).let { mActivity ->
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                    dataObject.inputType = "Voice"
                    dataObject.inputName = "Phone"
                    val data = Gson().toJson(dataObject).toString()
                    mActivity.logTrackingVersion2(
                        QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                        data
                    )

                    mActivity.gotoDiscoveryVoice(REQUEST_VOICE_PHONE_CODE)
                }
            }
        }

        edt_phone__.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
            dataObject.inputName = "Phone"
            dataObject.inputType = "KEYBOARD_PHONE_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as UserInformationActivity).logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )

            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_phone__.text.toString(),
                edt_phone__, edt_phone__
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }
        edt_phone__.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edt_phone__.setSelection(edt_phone__.text.length)
            }
        }
        edt_phone__.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    if (bn_voice.visibility == View.VISIBLE) {
                        bn_voice.requestFocus()
                    } else {
                        bn_delete.requestFocus()
                    }
                }, 0)
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onResume() {
        super.onResume()
        rl_bn_next.requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_VOICE_PHONE_CODE -> {
                val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null && result.isNotBlank()) {
                    if (Functions.checkPhoneNumber(result.trim().replace(" ", ""))) {
                        (activity as UserInformationActivity).let { mActivity ->
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                            dataObject.inputName = "Phone"
                            dataObject.inputType = "Voice"
                            dataObject.result = result
                            mActivity.logTrackingVersion2(
                                QuickstartPreferences.VOICE_RESULT_v2,
                                Gson().toJson(dataObject).toString()
                            )
                        }
                        edt_phone__.setText(result.trim().replace(" ", ""))
                        Handler().postDelayed({
                            rl_bn_next.requestFocus()
                        }, 0)
                    } else {
                        Functions.showMessage(
                            requireActivity(),
                            getString(R.string.text_error_phone)
                        )
                    }
                }
            }
        }

    }
}
