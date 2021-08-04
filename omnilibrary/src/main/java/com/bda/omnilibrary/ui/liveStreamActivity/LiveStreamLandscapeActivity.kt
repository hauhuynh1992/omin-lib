package com.bda.omnilibrary.ui.liveStreamActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.FragmentManager
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.livestream.ItemLiveStreamAdapterV3
import com.bda.omnilibrary.adapter.livestream.OthersVideoAdapter
import com.bda.omnilibrary.adapter.livestream.TabLiveStreamAdapter
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.LiveStream
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.AddedListToCart.AddedListToCartFragment
import com.bda.omnilibrary.ui.liveStreamActivity.addedProductInCart.AddedProductInCartFragment
import com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher.ChooseVoucherFragment
import com.bda.omnilibrary.ui.liveStreamActivity.detailLiveStreamProduct.DetailLiveStreamProductFragment
import com.bda.omnilibrary.ui.liveStreamActivity.playbackExo.VideoConsumptionWithExoPlayerFragment
import com.bda.omnilibrary.ui.liveStreamActivity.successOrder.SuccessFragment
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_live_stream_v3.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


class LiveStreamLandscapeActivity : BaseActivity(), LiveStreamContract.View,
    MotionLayout.TransitionListener {
    private var userInfo: CheckCustomerResponse? = null
    private lateinit var presenter: LiveStreamContract.Presenter

    private var titleLive: String = ""
    private var live: List<LiveStream>? = null

    private var mLiveStream: LiveStream? = null
    private var url = ""

    private val listTab = arrayListOf(/*"info", */"product", "video"/*, "voucher", "comment"*/)
    private var isPlayAnimation = false
    private var currentTab = 0
    private var currentSlectedProduct: Product? = null

    private var othersVideoAdapter: OthersVideoAdapter? = null
    private var itemLiveStreamAdapterV3: ItemLiveStreamAdapterV3? = null
    private var ssaiTimeout: Disposable? = null

    private var bottomTab: TabLiveStreamAdapter? = null
    private var productTab: TabLiveStreamAdapter? = null
    private var videoFragment: VideoConsumptionWithExoPlayerFragment? = null

    companion object {
        private var weakLandscapeActivity: WeakReference<LiveStreamLandscapeActivity>? = null

        fun getActivity(): LiveStreamLandscapeActivity? {
            return if (weakLandscapeActivity != null) {
                weakLandscapeActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_stream_v3)
        setLayoutId(R.id.layout3)

        weakLandscapeActivity = WeakReference(this@LiveStreamLandscapeActivity)

        intent?.let {
            mLiveStream = Gson().fromJson(
                it.getStringExtra(QuickstartPreferences.LIVESTREAM),
                LiveStream::class.java
            )
        }

        //todo ........
        // streamID = "0xcfb9"


        if (mLiveStream == null) {
            finish()
        }

        userInfo = Gson().fromJson(
            getPreferenceHelper()!!.userInfo,
            object : TypeToken<CheckCustomerResponse>() {}.type
        )
        presenter = LiveStreamPresenter(this, this)
        live = getLive()
        initial()
    }

    private fun initial() {
        loadVideo()

        itemLiveStreamAdapterV3 = ItemLiveStreamAdapterV3(this, mLiveStream!!.products)

        dummy_view.setOnClickListener {
            // todo nothing
        }

        name_livestram.text = mLiveStream?.display_name

        itemLiveStreamAdapterV3!!.setOnCallbackListener(object :
            ItemLiveStreamAdapterV3.OnCallBackListener {
            override fun onItemClick(product: LiveStream.StreamProduct) {

                if (!isPlayAnimation) {
                    currentSlectedProduct = product.product
                    m_motion_layout?.let {
                        it.setTransition(R.id.transition_detail)
                        it.setTransitionListener(this@LiveStreamLandscapeActivity)
                        it.transitionToEnd()
                    }

                    if (mLiveStream!!.status == 2)
                        seekToProductTime(product.time)
                }
            }
        })

        othersVideoAdapter = OthersVideoAdapter(this, live!!).apply {
            setOnCallbackListener(object : OthersVideoAdapter.OnCallBackListener {
                override fun onItemClick(live: LiveStream) {
                    gotoLiveStreamProduct(live.uid)
                    finish()
                }
            })
        }

        // tab above list product
        productTab = TabLiveStreamAdapter(this, listTab).apply {
            setOnCallbackListener(object : TabLiveStreamAdapter.OnCallBackListener {
                override fun onItemClick(p: Int) {
                    currentTab = p
                    updateTab(false)

                    when (listTab[p]) {
                        "product" -> {
                            product_list_view.apply {
                                adapter = itemLiveStreamAdapterV3
                            }

                            Handler().postDelayed({
                                product_list_view.requestFocus()
                            }, 200)
                        }

                        "video" -> {
                            product_list_view.apply {
                                adapter = othersVideoAdapter
                            }

                            Handler().postDelayed({
                                product_list_view.requestFocus()
                            }, 200)
                        }
                    }
                }
            })
        }

        list_tab.apply {
            adapter = productTab
        }

        // in bottom of parent
        bottomTab = TabLiveStreamAdapter(this, listTab).apply {
            setOnCallbackListener(object : TabLiveStreamAdapter.OnCallBackListener {
                override fun onItemClick(p: Int) {
                    currentTab = p
                    //tabList.setCurrentIndex(p)
                    updateTab(true)

                    when (listTab[p]) {
                        "product" -> {
                            // trick
                            focusDummyView()
                            trickForFun()

                            m_motion_layout?.let {

                                it.setTransition(R.id.transition_hiding_tab)
                                it.setTransitionListener(this@LiveStreamLandscapeActivity)
                                if (!isPlayAnimation)
                                    it.transitionToEnd()
                            }
                            //setTabVisibility(false)
                        }

                        "video" -> {
                            // trick
                            focusDummyView()
                            trickForFun()

                            m_motion_layout?.let {
                                it.setTransition(R.id.transition_hiding_tab)
                                it.setTransitionListener(this@LiveStreamLandscapeActivity)
                                if (!isPlayAnimation)
                                    it.transitionToEnd()
                            }
                            //setTabVisibility(false)

                        }
                    }
                }
            })
        }

        tab_recycler.apply {
            adapter = bottomTab
        }

        //todo change

        //ImageUtils.loadImage(this, image_supplier, "")
        //detail_supplier_name.setText("")
        //supplier_number_count.setText("")


    }

    private fun getLive(): List<LiveStream> {

        for (i in Config.homeData!!.data.section) {
            if (i.sectionValue == "livestream") {
                titleLive = i.sectionName
                return i.livestream.filterIndexed { _, liveStream ->
                    liveStream.uid != this@LiveStreamLandscapeActivity.mLiveStream!!.uid
                }
            }
        }
        return arrayListOf()
    }


    private fun getCurrentFragment() = getFManager().findFragmentById(layoutToLoadId())

    private fun loadVideo() {
        if (mLiveStream!!.status == 1) {
            if (mLiveStream!!.channel.size > 0) {
                url = mLiveStream!!.channel[0].link
            }
        } else {
            if (mLiveStream!!.video_transcode.isNotEmpty()) {
                url = mLiveStream!!.video_transcode
            }
        }

        val ft = supportFragmentManager.beginTransaction()
        videoFragment = VideoConsumptionWithExoPlayerFragment.newInstance(url)

        ft.add(R.id.layout1, videoFragment!!, VideoConsumptionWithExoPlayerFragment.TAG)
        ft.commit()
    }

    override fun onBackPressed() {
        when {
            getFManager().backStackEntryCount > 1 -> {
                focusDummyView()

                val f = getCurrentFragment()

                // todo change, dumb
                if (f is AddedListToCartFragment || f is AddedProductInCartFragment)
                    getFManager().popBackStack()
                else if (f is SuccessFragment) {
                    getFManager().popBackStack()
                    getFManager().popBackStack()
                }

                getFManager().popBackStack()

            }

            layout3.visibility == View.VISIBLE -> {
                hideDetailProduct()
            }

            layout_tab.visibility == View.VISIBLE && layout2.visibility != View.VISIBLE -> {
                setTabVisibility(false)
            }

            layout2.visibility == View.VISIBLE -> {
                if (!isPlayAnimation)
                    m_motion_layout?.let {
                        it.setTransition(R.id.transition_product_reverse)
                        it.setTransitionListener(this)
                        it.transitionToEnd()
                    }
            }

            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (animation_view.isAnimating)
            animation_view.speed = 4f

        val f = getCurrentFragment()
        timer()

        if (event!!.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (layout_tab.visibility != View.VISIBLE
                        && layout2.visibility != View.VISIBLE
                        && layout3.visibility != View.VISIBLE
                    ) {
                        if (shouldShowProductTab()) {
                            setTabVisibility(true)
                        } else {
                            return super.onKeyDown(keyCode, event)
                        }

                    } else {
                        if (product_list_view.hasFocus())
                            return true
                    }
                }

                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (layout_tab.visibility != View.VISIBLE && layout2.visibility != View.VISIBLE) {
                        if (shouldShowProductTab()) {
                            setTabVisibility(true)
                        } else {
                            return super.onKeyDown(keyCode, event)
                        }

                    } else if (layout3.visibility == View.VISIBLE && f != null
                        && f is DetailLiveStreamProductFragment
                        && (f as LiveStreamBaseFragment).isAbleToHideByClickLeft()
                        && !isPlayAnimation
                    ) {
                        hideDetailProduct()
                    }
                }

                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (dummy_view.hasFocus()) {

                    } else if (product_list_view.hasFocus() && layout2.visibility == View.VISIBLE && !isPlayAnimation) {
                        Handler().postDelayed({
                            list_tab.requestFocus()
                        }, 0)
                        return true
                    } else if (list_tab.hasFocus() && layout2.visibility == View.VISIBLE && !isPlayAnimation) {
                        m_motion_layout?.let {
                            it.setTransition(R.id.transition_product_reverse)
                            it.setTransitionListener(this)
                            it.transitionToEnd()
                        }
                    } else if (layout_tab.visibility != View.VISIBLE && layout2.visibility != View.VISIBLE) {
                        if (shouldShowProductTab()) {
                            setTabVisibility(true)
                        } else {
                            return super.onKeyDown(keyCode, event)
                        }
                    } else if (layout_tab.visibility == View.VISIBLE && layout2.visibility != View.VISIBLE) {
                        setTabVisibility(false)
                    }
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    if (layout_tab.visibility != View.VISIBLE && layout2.visibility != View.VISIBLE) {
                        if (shouldShowProductTab()) {
                            setTabVisibility(true)
                        } else {
                            return super.onKeyDown(keyCode, event)
                        }
                    }
                }

                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    if (layout_tab.visibility != View.VISIBLE && layout2.visibility != View.VISIBLE) {
                        if (shouldShowProductTab()) {
                            setTabVisibility(true)
                        } else {
                            return super.onKeyDown(keyCode, event)
                        }
                    }
                }
            }
        }

        if (f != null && f is ChooseVoucherFragment && layout3.visibility == View.VISIBLE) {
            return if (f.myOnkeyDown(keyCode, event))
                true
            else
                return super.onKeyDown(keyCode, event)
        } else if (f != null && f is DetailLiveStreamProductFragment && layout3.visibility == View.VISIBLE) {
            return if (f.myOnkeyDown(keyCode, event))
                true
            else
                return super.onKeyDown(keyCode, event)
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun setTabVisibility(b: Boolean) {
        m_motion_layout?.let {
            it.setTransition(R.id.transition_tab)
            it.addTransitionListener(this)

            if (!isPlayAnimation) {
                if (b)
                    it.transitionToEnd()
                else
                    it.transitionToStart()
            }
        }
    }

    private fun shouldShowProductTab(): Boolean {
        if (mLiveStream!!.status != 2 || (videoFragment != null && videoFragment!!.isControlsOverlayVisible)) {
            videoFragment!!.hideControlsOverlay(true)
            return true
        }

        return true
    }

    override fun focusDummyView() {
        Handler().postDelayed({
            dummy_view?.requestFocus()
        }, 0)
    }

    override fun hideDetailProduct() {
        if (!isPlayAnimation) {
            m_motion_layout?.let {
                it.setTransition(R.id.transition_detail)
                it.addTransitionListener(this)
                it.transitionToStart()
            }

            Handler().postDelayed({
                isLoadFirstFragment = false
                getFManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                product_list_view.requestFocus()
            }, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in getFManager().fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun sendSuccess(liveStream: LiveStream) {
    }

    override fun sendFalsed(error: String) {
        Functions.showMessage(this, error)
    }

    override fun sendAddressSuccess(data: CheckCustomerResponse) {
    }

    override fun sendAddressFailed(message: String) {
    }

    override fun finishThis(order_Id: String) {
    }

    override fun sendFailedOrder(message: String) {
    }

    override fun seekToProductTime(time: Int) {
        videoFragment?.seekTo((time * 1000).toLong())
    }

    private fun updateTab(isClickBottomTabOfParent: Boolean) {
        if (isClickBottomTabOfParent)
            productTab?.setCurrentIndex(currentTab)
        else
            bottomTab?.setCurrentIndex(currentTab)
    }

    private fun trickForFun() {
        m_motion_layout?.setTransition(-1, -1)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {


        return super.dispatchKeyEvent(event)
    }

    private fun timer() {
        if (ssaiTimeout != null) {
            ssaiTimeout?.dispose()
        }

        if (layout_tab.visibility == View.VISIBLE && layout2.visibility != View.VISIBLE)
            ssaiTimeout = Completable.timer(
                6,
                TimeUnit.SECONDS,
                AndroidSchedulers.mainThread()
            ).subscribe {
                setTabVisibility(false)
            }
    }

    /////////////// MotionLayout.TransitionListener ////////////////
    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
        isPlayAnimation = true
    }

    // trick
    private var isLoadFirstFragment = false
    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        if (p1 == R.id.start_product || p1 == R.id.start_hiding_tab)
            layout1.radius = resources.getDimension(R.dimen._8sdp) * p3
        else if (p1 == R.id.end_product)
            layout1.radius = resources.getDimension(R.dimen._8sdp) * (1 - p3)
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        isPlayAnimation = false

        when {
            p1 == R.id.end_hiding_tab -> {

                updateTab(true)
                when (currentTab) {
                    0 -> { //product
                        Handler().postDelayed({
                            product_list_view.apply {
                                adapter = itemLiveStreamAdapterV3
                            }
                        }, 0)
                    }

                    1 -> { //video
                        Handler().postDelayed({
                            product_list_view.apply {
                                adapter = othersVideoAdapter
                            }
                        }, 0)
                    }
                }

                Handler().postDelayed({
                    product_list_view.requestFocus()
                }, 300)
            }

            (p1 == R.id.end_detail && currentSlectedProduct != null && !isLoadFirstFragment) -> {
                isLoadFirstFragment = true
                loadFragment(
                    DetailLiveStreamProductFragment.newInstance(currentSlectedProduct!!),
                    R.id.layout3,
                    true
                )
            }

            layout2.visibility == View.VISIBLE && dummy_view.hasFocus() && layout3.visibility != View.VISIBLE ->
                Handler().postDelayed({
                    product_list_view.requestFocus()
                }, 0)

            p1 == R.id.end_tab -> {
                Handler().postDelayed({
                    tab_recycler?.requestFocus()
                }, 0)
            }
        }
    }
    /////////////// MotionLayout.TransitionListener ////////////////

}