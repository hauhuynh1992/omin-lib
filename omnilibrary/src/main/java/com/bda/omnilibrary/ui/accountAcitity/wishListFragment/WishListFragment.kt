package com.bda.omnilibrary.ui.accountAcitity.wishListFragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.VerticalGridView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.WishListAdapter
import com.bda.omnilibrary.dialog.WishListConfirmDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_wish_list.*

class WishListFragment : Fragment(), WishListContact.View {
    private lateinit var presenter: WishListPresenter
    lateinit var favouriteAdatper: WishListAdapter
    lateinit var rvList: VerticalGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        favouriteAdatper = WishListAdapter(requireActivity(), arrayListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wish_list, container, false)
        rvList = view.findViewById<VerticalGridView>(R.id.rv_wish_list)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WishListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun sendSuccess(products: ArrayList<Product>) {
        val items = ArrayList<Product>()
        items.addAll(products)
        txt_quantity.text = products.size.toString()
        favouriteAdatper = WishListAdapter(activity!!, items)
        favouriteAdatper?.setOnItemClickListener(object :
            WishListAdapter.OnItemClickListener {
            override fun onViewDetailClick(product: Product, position: Int) {
                this@WishListFragment.activity.let {
                    val dataObject = LogDataRequest()
                    dataObject.itemName = product.name
                    if (product.collection.size >= 1) {
                        dataObject.itemCategoryId = product.collection.get(0).uid
                        dataObject.itemCategoryName = product.collection.get(0).collection_name
                    }

                    dataObject.itemId = product.uid
                    dataObject.itemBrand = product.brand.name
                    dataObject.itemListPriceVat = product.price.toString()
                    val data = Gson().toJson(dataObject).toString()
                    (activity as AccountActivity)?.logTrackingVersion2(
                        QuickstartPreferences.CLICK_PRODUCT_DETAILS_BUTTON_v2,
                        data
                    )

                    (it as AccountActivity).gotoProductDetail(
                        product,
                        position,
                        QuickstartPreferences.CLICK_PRODUCT_FROM_WISH_LIST,
                        isPopup = false
                    )
                }
            }

            override fun onRemoveItemClick(product: Product) {
                val dataObject = LogDataRequest()
                dataObject.itemName = product.name
                if (product.collection.size >= 1) {
                    dataObject.itemCategoryId = product.collection.get(0).uid
                    dataObject.itemCategoryName = product.collection.get(0).collection_name
                }
                dataObject.itemId = product.uid
                dataObject.itemBrand = product.brand.name
                dataObject.itemListPriceVat = product.price.toString()
                val data = Gson().toJson(dataObject).toString()
                (activity as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_REMOVE_WISHLIST_BUTTON_v2,
                    data
                )
                presenter.getRemoveFavourite(product.uid)
            }

            override fun onAddToCartClick(product: Product, position: Int) {
                val dataObject = LogDataRequest()
                val dbProduct =
                    (activity as AccountActivity)?.getDatabaseHandler()?.getLProductList()
                var mTotalMoney = 0.0
                dbProduct?.let { list ->
                    for (i in 0 until list.size) {
                        mTotalMoney += (list[i].price * list[i].order_quantity)
                    }
                }
                dataObject.screen = Config.SCREEN_ID.WISHLIST.name
                dataObject.itemId = product?.uid
                dataObject.itemName = product?.name
                dataObject.cartValue = mTotalMoney.toString()
                dataObject.cartTotalItem = dbProduct?.size.toString()
                if (product.collection.size >= 1) {
                    dataObject.itemCategoryId = product.collection.get(0).uid
                    dataObject.itemCategoryName = product.collection.get(0).collection_name
                }
                dataObject.itemListPriceVat = product?.price.toString()
                val data = Gson().toJson(dataObject).toString()
                (activity as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_ADD_TO_CART_BUTTON_v2,
                    data
                )
                loadGoToCart(product, position)

            }

            override fun onShoppingContinueClick() {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.WISHLIST.name
                val data = Gson().toJson(dataObject).toString()
                (activity as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_CONTINUE_SHOPPING_BUTTON_v2,
                    data
                )
                requireActivity().finish()
            }
        })
        rvList.adapter = favouriteAdatper
    }

    private fun loadGoToCart(product: Product, productPosition: Int) {
        this@WishListFragment.activity?.let { mActivty ->
            mActivty as AccountActivity
            val productName =
                resources.getString(R.string.text_wish_list_confirm, product.name)
            WishListConfirmDialog(mActivty, productName, {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.WISHLIST.name
                dataObject.itemId = product?.uid
                dataObject.itemName = product?.name
                dataObject.itemListPriceVat = product?.price.toString()
                val data = Gson().toJson(dataObject).toString()
                mActivty.logTrackingVersion2(
                    QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                    data
                )


                (mActivty).addItemToCart(product)
                (mActivty).updateCart()
                Functions.showMessage(mActivty, getString(R.string.text_da_them_vao_gio_hang)/*"Đã thêm vào giỏ hàng"*/)

            }, {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.WISHLIST.name
                dataObject.itemId = product?.uid
                dataObject.itemName = product?.name
                dataObject.itemListPriceVat = product?.price.toString()
                val data = Gson().toJson(dataObject).toString()
                mActivty.logTrackingVersion2(
                    QuickstartPreferences.CLICK_NO_BUTTON_POP_UP_v2,
                    data
                )
            })
        }
    }

    override fun sendRemoveFavouriteSuccess(id: String) {
        favouriteAdatper?.removeFavoriteId(id)
        txt_quantity.text = favouriteAdatper.getNumOfItems().toString()
    }

    override fun sendFalsed(message: Int) {
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        presenter = WishListPresenter(this, activity!!)
        Handler().postDelayed({
            presenter.getListFavourite(0)
        }, 300)

    }
}
