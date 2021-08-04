package com.bda.omnilibrary.adapter.checkout

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.ListOrderResponceV3
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_product_checkout_by_supplier.view.*

class ProductCheckoutBySupplierAdapter(
    activity: BaseActivity,
    private val mList: ArrayList<Pair<String, ArrayList<Product>>>?,
    private val orders: ArrayList<ListOrderResponceV3.Data.SubOrder>?,
) : BaseAdapter(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_product_checkout_by_supplier, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {

            if (position == 3) {
                holder.setMoreProduct()

                if (mList != null) {
                    holder.more_product.text =
                        mActivity.getString(
                            R.string.text_tong_sp,
                            Functions.getCountSupplierProduct(mList).toString()
                        )
                } else if (orders != null) {
                    holder.more_product.text =
                        mActivity.getString(
                            R.string.text_tong_sp,
                            Functions.getItemCountFromSubOrder(orders).toString()
                        )
                }

            } else {

                if (mList != null) {
                    if (mList.size == 1) {
                        holder.setProductOnlyOneSupplier(mList[0], position)
                    } else {
                        holder.setProduct(mList[position])
                    }
                } else if (orders != null) {
                    if (orders.size == 1) {
                        holder.setItemOnlyOneSupplier(orders[0], position)
                    } else {
                        holder.setItem(orders[position])
                    }
                }
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rl_product: LinearLayout = view.rl_product
        val product_count: SfTextView = view.product_count
        val supplier: SfTextView = view.supplier
        val content: LinearLayout = view.content
        val img_thumbnail: ImageView = view.img_thumbnail
        val name: SfTextView = view.name

        val more_product: SfTextView = view.more_product
        val rl_more_product: RelativeLayout = view.rl_more_product

        val supplier_title: RelativeLayout = view.supplier_title

        fun setMoreProduct() {
            rl_product.visibility = View.GONE
            rl_more_product.visibility = View.VISIBLE
        }

        fun setProduct(pair: Pair<String, ArrayList<Product>>) {
            product_count.text =
                mActivity.getString(R.string.text_so_sp, pair.second.size.toString())
            supplier.text = mActivity.getString(R.string.text_ten_nha_cung_cap, pair.second[0].supplier.supplier_name)

            ImageUtils.loadImage(
                mActivity,
                img_thumbnail,
                pair.second[0].imageCover,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )

            name.text = pair.second[0].display_name_detail
        }

        fun setProductOnlyOneSupplier(pair: Pair<String, ArrayList<Product>>, p: Int) {
            if (p == 0) {
                product_count.text =
                    mActivity.getString(R.string.text_so_sp, pair.second.size.toString())
                supplier.text = mActivity.getString(R.string.text_ten_nha_cung_cap, pair.second[0].supplier.supplier_name)
            } else {
                supplier_title.visibility = View.GONE
            }

            ImageUtils.loadImage(
                mActivity,
                img_thumbnail,
                pair.second[p].imageCover,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )

            name.text = pair.second[p].display_name_detail
        }

        fun setItem(order: ListOrderResponceV3.Data.SubOrder) {
            product_count.text =
                mActivity.getString(R.string.text_so_sp, order.items.size.toString())
            supplier.text =
                mActivity.getString(
                    R.string.text_ten_nha_cung_cap,
                    order.partner?.partner_name ?: ""
                )

            ImageUtils.loadImage(
                mActivity,
                img_thumbnail,
                order.items[0].thumb,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )

            name.text = order.items[0].displayNameDetail
        }

        fun setItemOnlyOneSupplier(order: ListOrderResponceV3.Data.SubOrder, p: Int) {

            if (p == 0) {
                product_count.text =
                    mActivity.getString(R.string.text_so_sp, order.items.size.toString())
                supplier.text =
                    mActivity.getString(
                        R.string.text_ten_nha_cung_cap,
                        order.partner?.partner_name ?: ""
                    )
            } else {
                supplier_title.visibility = View.GONE
            }

            ImageUtils.loadImage(
                mActivity,
                img_thumbnail,
                order.items[p].thumb,
                ImageUtils.TYPE_PRIVIEW_SMALL
            )

            name.text = order.items[p].displayNameDetail
        }
    }


    override fun getItemCount(): Int {
        when {
            mList != null -> {
                return if (mList.size == 1) {
                    if (mList[0].second.size > 3) {
                        4
                    } else {
                        mList[0].second.size
                    }
                } else {
                    if (mList.size > 3) {
                        4
                    } else {
                        mList.size
                    }
                }
            }

            orders != null -> {
                return if (orders.size == 1) {
                    if (orders[0].items.size > 3) {
                        4
                    } else {
                        orders[0].items.size
                    }
                } else {
                    if (orders.size > 3) {
                        4
                    } else {
                        orders.size
                    }
                }
            }

            else -> return 0
        }
    }
}