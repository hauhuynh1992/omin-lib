package com.bda.omnilibrary.ui.authenActivity.fragmentRequireLogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity
import com.bda.omnilibrary.ui.authenActivity.fragmentLoginPhone.LoginPhoneFragment
import com.bda.omnilibrary.ui.authenActivity.fragmentLoginGoogle.LoginGoogleFragment
import com.bda.omnilibrary.ui.authenActivity.fragmentSignupNamePhone.SignUpNamePhoneFragment
import kotlinx.android.synthetic.main.fragment_require_login.*

class RequireLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_require_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    private fun initial() {
        bn_login_google.setOnClickListener {
            (context as AuthenticationActivity).loadFragment(
                LoginGoogleFragment.newInstance(),
                R.id.frameLayout, true
            )
        }

        bn_sign_in_by_phone.setOnClickListener {
            (context as AuthenticationActivity).loadFragment(
                LoginPhoneFragment.newInstance(),
                R.id.frameLayout, true
            )
        }

        bn_sign_up.setOnClickListener {
            (context as AuthenticationActivity).loadFragment(
                SignUpNamePhoneFragment.newInstance(),
                R.id.frameLayout, true
            )
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            RequireLoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}
