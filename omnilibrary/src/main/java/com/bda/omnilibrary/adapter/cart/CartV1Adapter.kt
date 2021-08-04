package com.bda.omnilibrary.adapter.cart


import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.dialog.RemoveItemInCartDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_dialog_cart.view.*
import java.util.*


class CartV1Adapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
) : BaseAdapter(activity) {

    private var mList: ArrayList<Product> = list
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_dialog_cart, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!mList[position].animation) {
            var quantity = mList[position].order_quantity
            ImageUtils.loadImage(
                mActivity,
                (holder as ItemViewHolder).imgThumbnail,
                mList[position].imageCover,
                ImageUtils.TYPE_CART
            )

            if (mList[position].is_first_supplier) {
                holder.showSupplierDivide(mList[position].supplier.supplier_name)

            } else {
                holder.hideSupplierDivide()

            }

            if (mList[position].brand_shop != null && mList[position].brand_shop!!.size > 0
                && mList[position].brand_shop!![0].display_name_in_product != ""
            ) {
                holder.text_shop.visibility = View.VISIBLE
                holder.shop_name.visibility = View.VISIBLE
                holder.shop_name.text = mList[position].brand_shop!![0].display_name_in_product
            } else {
                holder.text_shop.visibility = View.GONE
                holder.shop_name.visibility = View.GONE
            }

            if (mList[position].isDisableBySupplierCondition) {
                holder.text_condition.visibility = View.VISIBLE
                holder.text_condition.text =
                    mActivity.getString(R.string.text_cart_supplier_condition,
                        Functions.formatMoney(mList[position].supplier.required_order_value))
                holder.rl_cart_background.setBackgroundResource(R.mipmap.ic_item_incart_disable)

            } else {
                holder.rl_cart_background.setBackgroundResource(R.mipmap.ic_item_incart)
                holder.text_condition.visibility = View.GONE

            }

            holder.tv_quantity.text = quantity.toString()
            holder.tvPrice.text = Functions.formatMoney(mList[position].price)

            holder.name.text = mList[position].display_name_detail
            holder.bn_remove.setOnClickListener {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CART.name
                dataObject.itemNo = position.toString()
                dataObject.itemQuantity = mList[position].order_quantity.toString()
                dataObject.itemName = mList[position].name
                dataObject.itemId = mList[position].uid
                dataObject.itemListPriceVat = mList[position].price.toString()
                dataObject.totalItem = mList.size.toString()
                var totalMoney = 0.0
                for (i in 0 until mList.size) {
                    totalMoney += (mList[i].price * mList[i].order_quantity)
                }
                dataObject.cartValue = totalMoney.toString()
                val data = Gson().toJson(dataObject).toString()
                mActivity.logTrackingVersion2(
                    QuickstartPreferences.CLICK_REMOVE_PRODUCT_BUTTON_v2,
                    data
                )
                RemoveItemInCartDialog(mActivity, mList[position].display_name_detail, {
                    clickListener.onRemoveClick(position)
                }) {
                    mActivity.logTrackingVersion2(
                        QuickstartPreferences.CLICK_REJECT_DELETE_BUTTON_v2,
                        data
                    )
                }
            }

            holder.bn_remove.setOnFocusChangeListener { _, hasFocus ->

                if (hasFocus) {
                    holder.text_bn_remove.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )

                } else {

                    holder.text_bn_remove.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.color_text_key
                        )
                    )
                }
            }

            holder.handleMinusButton(quantity)

            holder.btn_minus.setOnClickListener {
                if (quantity > 1) {
                    quantity -= 1
                    if (quantity == 1) {
                        holder.btn_plus.requestFocus()
                    }
                    holder.tv_quantity.text = quantity.toString()
                    clickListener.onMinusClick(position, quantity)
                    holder.handleMinusButton(quantity)
                }
            }

            holder.btn_minus.setOnFocusChangeListener { _, hasFocus ->

                if (hasFocus) {

                    holder.text_btn_minus.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )

                } else {

                    holder.text_btn_minus.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.color_text_key
                        )
                    )
                }
            }

            holder.btn_plus.setOnClickListener {
                quantity += 1
                holder.tv_quantity.text = quantity.toString()
                clickListener.onPlusClick(position, quantity)

                holder.handleMinusButton(quantity)
            }

            holder.btn_plus.setOnFocusChangeListener { _, hasFocus ->

                if (hasFocus) {

                    holder.text_btn_plus.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.white
                        )
                    )

                } else {


                    holder.text_btn_plus.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.color_text_key
                        )
                    )
                }
            }

            holder.bn_detail.setOnClickListener {
                clickListener.onDetailClick(mList[position])
            }

            holder.bn_detail.setOnFocusChangeListener { _, hasFocus ->
                holder.text_bn_detail.isSelected = hasFocus

            }

            holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
                if (hasFocus) {
                    android.os.Handler().postDelayed({
                        holder.btn_plus.requestFocus()
                    }, 10)
                }
            }

        } else {
            holder.itemView.visibility = View.GONE
        }

    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rl_cart_background: RelativeLayout = view.rl_cart_background

        val imgThumbnail: ImageView = view.img_thumbnail
        val name: SfTextView = view.name
        val tvPrice: SfTextView = view.tv_price

        val shop_name: SfTextView = view.shop_name
        val text_shop: SfTextView = view.text_shop

        val text_condition: SfTextView = view.text_condition

        val tv_quantity: SfTextView = view.tv_quantity


        val bn_remove: LinearLayout = view.bn_remove

        val text_bn_remove: ImageView = view.text_bn_remove


        val btn_minus: LinearLayout = view.btn_minus

        val text_btn_minus: ImageView = view.text_btn_minus


        val btn_plus: LinearLayout = view.btn_plus

        val text_btn_plus: ImageView = view.text_btn_plus
        val bn_detail: LinearLayout = view.bn_detail

        val text_bn_detail: SfTextView = view.text_bn_detail

        val viewDivide: View = view.view_divide
        val supplier: SfTextView = view.supplier

        fun handleMinusButton(quantity: Int) {
            if (quantity == 1) {
                btn_minus.isEnabled = false
                btn_minus.alpha = 0.2f
            } else {
                btn_minus.isEnabled = true
                btn_minus.alpha = 1f
            }
        }

        fun showSupplierDivide(supplierName: String) {
            viewDivide.visibility = View.VISIBLE
            supplier.visibility = View.VISIBLE
            supplier.text = mActivity.getString(R.string.supplier_name, supplierName)
        }

        fun hideSupplierDivide() {
            viewDivide.visibility = View.GONE
            supplier.visibility = View.GONE
        }

    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onRemoveClick(position: Int)
        fun onPlusClick(position: Int, quantity: Int)
        fun onMinusClick(position: Int, quantity: Int)
        fun onDetailClick(product: Product)
    }
}