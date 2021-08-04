package com.bda.omnilibrary.ui.checkoutExistingUser.creditCard

import android.os.Bundle
import com.bda.omnilibrary.R
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.checkoutExistingUser.creditCard.InstallmentStep2Financial.InstallmentStep2FinancialFragment


class CreditCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)

        initial()
    }

    private fun initial() {
        //container
        loadFragment(InstallmentStep2FinancialFragment.newInstance(), R.id.container, false)
    }
}
