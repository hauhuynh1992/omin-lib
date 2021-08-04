package com.bda.omnilibrary.ui.authenActivity.fragmentSignupPassword

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
import com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment.ResetPasswordPhoneFragment
import kotlinx.android.synthetic.main.fragment_signup_pass.*
import kotlinx.android.synthetic.main.item_activity_header.*

class SignUpPasswordFragment : Fragment(), SignUpPasswordContact.View {

    private lateinit var presenter: SignUpPasswordPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = SignUpPasswordPresenter(this)
        return inflater.inflate(R.layout.fragment_signup_pass, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edt_password.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AuthenticationActivity).bn_search.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_confirm_password.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    edt_password.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })


        edt_password.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_password.text.toString(),
                edt_password, edt_confirm_password
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_confirm_password.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_password.text.toString(),
                edt_password, null
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        bn_complete.setOnClickListener {
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
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SignUpPasswordFragment().apply {
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

    override fun showSignUpPasswordSuccess(customerId: String, customerName: String) {
    }
}