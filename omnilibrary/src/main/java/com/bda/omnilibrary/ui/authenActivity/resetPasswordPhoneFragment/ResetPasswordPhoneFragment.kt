package com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity
import com.bda.omnilibrary.ui.authenActivity.fragmentResetPassOtp.ResetPasswordOtpFragment
import kotlinx.android.synthetic.main.fragment_login_phone.*
import kotlinx.android.synthetic.main.fragment_reset_pass_input_phone.*
import kotlinx.android.synthetic.main.fragment_reset_pass_input_phone.edt_phone__
import kotlinx.android.synthetic.main.item_activity_header.*

class ResetPasswordPhoneFragment : Fragment(), ResetPasswordPhoneContact.View {

    private lateinit var presenter: ResetPasswordPhonePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = ResetPasswordPhonePresenter(this)
        return inflater.inflate(R.layout.fragment_reset_pass_input_phone, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edt_phone__.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AuthenticationActivity).bn_search.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_phone__.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_phone__.text.toString(),
                edt_phone__, edt_password
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        bn_next.setOnClickListener {
//            val request = LoginByPhoneRequest(
//                phoneNumber = edt_phone.text.toString(),
//                password = edt_phone.text.toString()
//            )
//            request.validate().also { result ->
//                if (result.first) {
//                    presenter.resetPasswordInputPhone(request)
//                } else {
//                    Functions.showMessage(activity!!, result.second.toString())
//                }
//            }
            (context as AuthenticationActivity).loadFragment(
                ResetPasswordOtpFragment.newInstance(),
                R.id.frameLayout, true
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ResetPasswordPhoneFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError(error: String) {
    }

    override fun showInputPhoneSuccess(customerId: String, customerName: String) {
        // TODO
    }
}
