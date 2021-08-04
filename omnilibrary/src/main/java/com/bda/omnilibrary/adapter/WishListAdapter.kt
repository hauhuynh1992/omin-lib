package com.bda.omnilibrary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import java.util.*

class WishListAdapter(
    activity: Activity,
    list: ArrayList<Product>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<Product> = list
    private var mActivity: Activity = activity
    private lateinit var clickListener: OnItemClickListener
    private var isAddToCartButtonHasFocus: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == R.layout.item_wish_list_empty) {
            v = mInflater
                .inflate(R.layout.item_wish_list_empty, parent, false)
            EmptyViewHolder(v)
        } else {
            v = mInflater
                .inflate(R.layout.item_wish_list, parent, false)
            ItemViewHolder(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList.size == 0) {
            R.layout.item_wish_list_empty
        } else {
            R.layout.item_wish_list
        }
    }

    override fun getItemCount(): Int {
        return if (mList.size > 0) {
            mList.size
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EmptyViewHolder -> {
                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.itemView.requestFocus()
                        holder.itemView.invalidate()
                        android.os.Handler().postDelayed({
                            holder.bn_shopping.requestFocus()
                        }, 0)
                    }
                }

                holder.rl_bn_continue_shopping.setOnFocusChangeListener { _, isFocus ->
                    holder.bn_shopping.isSelected = isFocus
                }

                holder.rl_bn_continue_shopping.setOnClickListener {
                    clickListener.onShoppingContinueClick()
                }
            }
            else -> {
                holder as ItemViewHolder
                holder.bind(mList[position])
                ImageUtils.loadImage(
                    mActivity,
                    holder.img_product,
                    mList[position].imageCover,
                    ImageUtils.TYPE_CART
                )
                holder.rl_bn_add_cart.setOnClickListener {
                    clickListener.onAddToCartClick(mList[position], position)
                }

                holder.rl_bn_detail.setOnClickListener {
                    clickListener.onViewDetailClick(mList[position], position)
                }

                holder.rl_bn_remove.setOnClickListener {
                    clickListener.onRemoveItemClick(mList[position])
                }


                holder.rl_bn_add_cart.setOnFocusChangeListener { _, isFocus ->
                    holder.bn_add_cart.isSelected = isFocus
                    isAddToCartButtonHasFocus = isFocus
                }

                holder.rl_bn_remove.setOnFocusChangeListener { _, isFocus ->
                    holder.bn_remove.isSelected = isFocus
                }

                holder.rl_bn_detail.setOnFocusChangeListener { _, isFocus ->
                    holder.bn_detail.isSelected = isFocus
                }


                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.itemView.requestFocus()
                        holder.itemView.invalidate()
                        android.os.Handler().postDelayed({
                            holder.bn_add_cart.requestFocus()
                        }, 0)
                    }
                }
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val img_product: ImageView = view.findViewById(R.id.img_product)
        val tv_product_name: SfTextView = view.findViewById(R.id.tv_product_name)
        val tv_sell_price: SfTextView = view.findViewById(R.id.tv_sell_price)
        val tv_list_price: SfStrikeTextView = view.findViewById(R.id.tv_list_price)
        val rating: RatingBar = view.findViewById(R.id.rating)
        val bn_add_cart: SfTextView = view.findViewById(R.id.bn_add_cart)
        val bn_detail: SfTextView = view.findViewById(R.id.bn_detail)
        val bn_remove: SfTextView = view.findViewById(R.id.bn_remove)
        val rl_bn_remove: RelativeLayout = view.findViewById(R.id.rl_bn_remove)
        val rl_bn_add_cart: RelativeLayout = view.findViewById(R.id.rl_bn_add_cart)
        val rl_bn_detail: RelativeLayout = view.findViewById(R.id.rl_bn_detail)

        @Suppress("UNNECESSARY_SAFE_CALL")
        fun bind(
            mProduct: Product
        ) {

            mProduct.display_name_detail?.let {
                tv_product_name.text = it
            }

            mProduct.price?.let {
                tv_sell_price.text = Functions.formatMoney(it)
            }

            mProduct.listedPrice?.let {
                tv_list_price.text = Functions.formatMoney(it)
            }
        }

    }

    @Suppress("PropertyName")
    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_shopping: SfTextView = view.findViewById(R.id.bn_continue_shopping)
        val rl_bn_continue_shopping: RelativeLayout =
            view.findViewById(R.id.rl_bn_continue_shopping)
    }

    fun removeFavoriteId(id: String) {
        mList.indexOfFirst { it.uid == id }.let { index ->
            if (index != ITEM_NOT_FOUND) {
                mList.removeAt(index)
                notifyItemRemoved(index)
                notifyItemRangeChanged(index, mList.size)
            }
        }

    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    fun isAddToCartFocus(): Boolean {
        return isAddToCartButtonHasFocus
    }

    fun getNumOfItems(): Int {
        return mList.size
    }

    interface OnItemClickListener {
        fun onViewDetailClick(product: Product, position: Int)
        fun onRemoveItemClick(productId: Product)
        fun onAddToCartClick(product: Product, position: Int)
        fun onShoppingContinueClick()
    }

    companion object {
        val ITEM_NOT_FOUND: Int = -1
    }

}