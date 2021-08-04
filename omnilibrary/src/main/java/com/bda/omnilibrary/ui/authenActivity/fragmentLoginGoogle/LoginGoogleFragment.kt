package com.bda.omnilibrary.ui.authenActivity.fragmentLoginGoogle

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity
import kotlinx.android.synthetic.main.fragment_login_google.*

class LoginGoogleFragment : Fragment(), LoginGoogleContact.View {

    private lateinit var presenter: LoginGooglePresenter
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = LoginGooglePresenter(this)
        return inflater.inflate(R.layout.fragment_login_google, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bn_make_new.setOnClickListener {
            presenter.getDeviceCode()
            bn_make_new.isEnabled=false
            bn_make_new.alpha = 0.4f
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginGoogleFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onResume() {
        super.onResume()
        presenter.getDeviceCode()
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showDeviceCode(userCode: String, url: String, deviceCode: String) {
        tv_code.text = userCode
        tv_link_auth.text = url
        presenter.getAccessTokenInterval(deviceCode)
        activity?.let {
            timer = (it as AuthenticationActivity).startLoginTimer(time, {
                bn_make_new.isEnabled = true
                bn_make_new.alpha=1f
                bn_make_new.requestFocus()
            }, {})
        }
    }

    override fun showAssessToken(token: String, token_type: String) {
        presenter.getDriveInfo(token, token_type)
    }

    override fun showGoogleDriveInfoToken(id: String, email: String, token: String, name: String) {
        presenter.loginWithGoogle(
            id = id,
            email = email,
            token = token,
            name = name
        )
    }

    override fun showError(error: String) {

    }

    override fun showLoginByGoogleSuccess(customerId: String, customerName: String) {
        if (timer != null) {
            timer?.cancel()
        }
        (activity as AuthenticationActivity).getCustomer(customerId)

    }

    override fun onStop() {
        super.onStop()
        if (timer != null) {
            timer?.cancel()
        }

        presenter.disposeAPI()
    }
}
