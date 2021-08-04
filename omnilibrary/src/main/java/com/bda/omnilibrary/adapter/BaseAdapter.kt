package com.bda.omnilibrary.adapter

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.ui.baseActivity.BaseActivity

abstract class BaseAdapter(val mActivity: BaseActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mInflater: LayoutInflater =
        mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun getLayoutInflater() = mInflater
}