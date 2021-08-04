package com.bda.omnilibrary.ui.accountAcitity.infoFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.multidex.BuildConfig.VERSION_NAME
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.LibConfig.VERSION_NAME
import com.bda.omnilibrary.R
import kotlinx.android.synthetic.main.fragment_info.*

@Suppress("unused")
class InfoFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            InfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    @SuppressLint("SetTextI18n")
    private fun initial() {
        tv_version.text = getString(R.string.version) + " " + LibConfig.VERSION_NAME
    }
}
