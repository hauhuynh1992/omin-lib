package com.bda.omnilibrary.adapter

import android.animation.Animator
import android.animation.AnimatorInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.java.lazy.ContextKt.child


class DotsAdapter(val activity: BaseActivity, var list: ArrayList<String>) :
    RecyclerView.Adapter<DotsAdapter.DotViewHolder>() {

    private var currentSelected = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotViewHolder =
        LayoutInflater.from(parent.context).inflate(R.layout.item_dots, parent, false).let {
            DotViewHolder(it)
        }

    override fun onBindViewHolder(holder: DotViewHolder, position: Int) {
        val data = list[position]
        when (data) {
            "next" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_indicator_next)
                val animation: Animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
                holder.ivIcon.animation = animation
            }
            "pre" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_indicator_forward)
                val animation: Animation = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
                holder.ivIcon.animation = animation
            }
            "none" -> {
                holder.ivIcon.setImageResource(R.drawable.ic_unselected_indicator)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class DotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivIcon: ImageView

        init {
            ivIcon = itemView.findViewById(R.id.iv_dot)
        }
    }

    fun update(position: Int) {
        for (i in 0..list.size - 1) {
            if (i == position) {
                if (currentSelected > position) {
                    list[i] = "pre"
                } else {
                    list[i] = "next"
                }
                currentSelected = position
            } else {
                list[i] = "none"
            }
        }
        notifyDataSetChanged()
    }
}