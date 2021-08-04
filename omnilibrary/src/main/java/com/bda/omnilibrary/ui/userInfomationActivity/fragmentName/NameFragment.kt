package com.bda.omnilibrary.ui.userInfomationActivity.fragmentName


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.userInfomationActivity.UserInformationActivity
import com.bda.omnilibrary.ui.userInfomationActivity.fragmentProvince.ProvinceFragment
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_NAME_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_name.*
import kotlinx.android.synthetic.main.item_payment_type.*
import kotlinx.android.synthetic.main.milestone_6_step.view.*


class NameFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            NameFragment().apply {
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
        ic_status.step_2.setTextColor(ContextCompat.getColor(context!!, R.color.color_41AE96))
        ic_status.step.setImageResource(R.mipmap.milestone_6_step2)

        edt_name.setText((context as UserInformationActivity).userInfoTemp.data.name)

        if (arrayListOf("box2019", "box2020","omnishopeu","box2021").contains(Config.platform)) {
            bn_voice.visibility = View.VISIBLE
        } else {
            bn_voice.visibility = View.GONE
        }

        rl_bn_next.setOnClickListener {
            if (edt_name.text.isBlank()) {
                Functions.showError(context!!, edt_name)
            } else {
                (context as UserInformationActivity).userInfoTemp.data.name =
                    edt_name.text.toString().trim()
                (context as UserInformationActivity).userInfoTemp.data.alt_info[0].customer_name =
                    edt_name.text.toString().trim()
                (context as UserInformationActivity).loadFragment(
                    ProvinceFragment.newInstance(),
                    R.id.frameLayout, true
                )
            }
        }
        rl_bn_next.setOnFocusChangeListener { _, hasFocus ->
            Handler().postDelayed({
                bn_next?.isSelected = hasFocus
            }, 0)
        }
        bn_delete.setOnClickListener {
            edt_name.setText("")
        }
        bn_voice.setOnClickListener {
            edt_name.setText("")

            activity?.let {
                (it as UserInformationActivity).let { mActivity ->
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                    dataObject.inputName = "Address"
                    dataObject.inputType = "Voice"
                    val data = Gson().toJson(dataObject).toString()
                    mActivity.logTrackingVersion2(
                        QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                        data
                    )
                    mActivity.gotoDiscoveryVoice(REQUEST_VOICE_NAME_CODE)
                }
            }
        }

        edt_name.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
            dataObject.inputName = "Name"
            dataObject.inputType = "KEYBOARD_NAME_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as BaseActivity).logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_NAME_TYPE,
                edt_name.text.toString(),
                edt_name, edt_name
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }
        edt_name.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edt_name.setSelection(edt_name.text.length)
            }
        }
        edt_name.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
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
            REQUEST_VOICE_NAME_CODE -> {
                val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null && result.isNotBlank()) {
                    (activity as UserInformationActivity).let { mActivity ->
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                        dataObject.inputName = "Name"
                        dataObject.inputType = "Voice"
                        dataObject.result = result
                        mActivity.logTrackingVersion2(
                            QuickstartPreferences.VOICE_RESULT_v2,
                            Gson().toJson(dataObject).toString()
                        )
                    }
                    edt_name.setText(result.trim())
                    Handler().postDelayed({
                        rl_bn_next.requestFocus()
                    }, 0)
                }
            }
        }

    }
}
