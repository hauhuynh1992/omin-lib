package com.bda.omnilibrary.util

import android.animation.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Handler
import android.provider.Settings
import android.text.InputType
import android.text.Spannable
import android.text.TextPaint
import android.text.style.RelativeSizeSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.model.Region
import com.bda.omnilibrary.views.SfTextView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import java.io.*
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.experimental.and
import kotlin.math.ceil


object Functions {
    private val suffixes = TreeMap<Long, String>()
    fun checkInternet(actv: Context): Boolean {
        val cm = actv.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiNetwork != null && wifiNetwork.isConnected) {
            return true
        }
        val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (mobileNetwork != null && mobileNetwork.isConnected) {
            return true
        }
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun showError(context: Context, v: View) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        v.startAnimation(shake)
    }

    fun onGetFirmwareVersionAndMacAddress(
            context: Context?,
    ) {
        if (context != null) {
//            val deviceInfoManager = DeviceInfoManager()
//            deviceInfoManager.init(context, object : OnInitListener {
//                @SuppressLint("DefaultLocale")
//                override fun onSuccess() {
//                    Config.macAddress = deviceInfoManager.readDeviceMAC().toUpperCase()
//                    if (Config.macAddress == null) {
//                        Config.macAddress = getMacAddressForSei()
//                    } else if (Config.macAddress.trim().length == 0) {
//                        Config.macAddress = getMacAddressForSei()
//                    }
//                }
//
//                override fun onFailed() {
//                    Config.macAddress = getMacAddressForSei()
//                }
//            })
        }
    }

    fun getMacAddressForSei(): String {
        return try {
            val macAddress = getProperties("ro.boot.mac")
            if (macAddress.isNotEmpty()) macAddress else ""
        } catch (exception: java.lang.Exception) {
            ""
        }
    }

    fun getFirmwareVersionForSei(): String {
        return try {
            val firmwareVersion = getProperties("ro.nes.sw.version")
            if (firmwareVersion.isNotEmpty()) firmwareVersion else ""
        } catch (exception: java.lang.Exception) {
            ""
        }
    }

    @SuppressLint("PrivateApi")
    private fun getProperties(propertyName: String): String {
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            return getMethod.invoke(null, propertyName) as String
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return ""
    }

    @Synchronized
    fun animateScaleUp(v: View, scale: Float, duration: Long = 100) {
        v.visibility = View.VISIBLE
        val scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, scale)
        val scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, scale)
        val scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(v, scaleXHolder, scaleYHolder)
        scaleAnimator.duration = duration
        scaleAnimator.start()
    }

    @Synchronized
    fun animateScaleDown(v: View, duration: Long = 200) {
        val currentScaleX = v.scaleX
        val currentScaleY = v.scaleY
        val targetScale = 1f.takeIf { duration == 200L } ?: 0f
        val scaleXHolder =
                PropertyValuesHolder.ofFloat(View.SCALE_X, currentScaleX, targetScale)
        val scaleYHolder =
                PropertyValuesHolder.ofFloat(View.SCALE_Y, currentScaleY, targetScale)
        val scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(v, scaleXHolder, scaleYHolder)
        scaleAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (targetScale == 0f) {
                    v.visibility = View.GONE
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
        scaleAnimator.duration = duration
        scaleAnimator.start()
    }


    @Synchronized
    fun animateScaleUpLiveStream(v: View, scale: Float, duration: Long = 100) {
        v.visibility = View.VISIBLE
        val scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, scale)
        val scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, scale)
        val scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(v, scaleXHolder, scaleYHolder)
        scaleAnimator.setEvaluator(FloatEvaluator())
        scaleAnimator.interpolator = AccelerateInterpolator()
        scaleAnimator.duration = duration
        scaleAnimator.start()
    }

    @Synchronized
    fun animateScaleDownLiveStream(v: View, duration: Long = 200) {
        val currentScaleX = v.scaleX
        val currentScaleY = v.scaleY
        val targetScale = 1f.takeIf { duration == 200L } ?: 0f
        val scaleXHolder =
                PropertyValuesHolder.ofFloat(View.SCALE_X, currentScaleX, targetScale)
        val scaleYHolder =
                PropertyValuesHolder.ofFloat(View.SCALE_Y, currentScaleY, targetScale)
        val scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(v, scaleXHolder, scaleYHolder)
        scaleAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (targetScale == 0f) {
                    v.visibility = View.GONE
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
        scaleAnimator.setEvaluator(FloatEvaluator())
        scaleAnimator.interpolator = AccelerateInterpolator()
        scaleAnimator.duration = duration
        scaleAnimator.start()
    }

    fun increaseViewSize(
            view: View,
            size: Int,
            isUpDown: Boolean,
            mCallback: (result: Int) -> Unit
    ) {
        view.visibility = View.VISIBLE
        val valueAnimator = ValueAnimator.ofInt(0, size)
        valueAnimator.duration = 300L
        valueAnimator.setEvaluator(IntEvaluator())
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            if (isUpDown) {
                layoutParams.height = animatedValue
            } else {
                layoutParams.width = animatedValue
            }
            mCallback.invoke(animatedValue * 100 / size)
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }

    fun decreaseViewSize(
            view: View,
            size: Int,
            isUpDown: Boolean,
            mCallback: (result: Int) -> Unit
    ) {

        val valueAnimator = ValueAnimator.ofInt(size, 0)
        valueAnimator.duration = 300L
        valueAnimator.setEvaluator(IntEvaluator())
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            if (isUpDown) {
                layoutParams.height = animatedValue
            } else {
                layoutParams.width = animatedValue
            }
            mCallback.invoke(animatedValue * 100 / size)
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }

    fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus != null) {
            val inputMethodManager =
                    activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }

    fun showMessage(activity: Context, text: String) {
        try {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
        } catch (n: NullPointerException) {
        } catch (e: Exception) {

        }

    }

    fun showMessageCustom(activity: Context, text: String) {
//        try {
//            Toasty.custom(
//                    activity,
//                    text,
//                    R.drawable.ic_baseline_check_circle_24,
//                    R.color.color_fea72b,
//                    Toast.LENGTH_SHORT,
//                    true,
//                    true
//            ).show()
//        } catch (n: NullPointerException) {
//        } catch (e: Exception) {
//
//        }

    }

    fun formatMoney(money: Double): String {
        val formatter = DecimalFormat("#,###đ")
        return formatter.format(money)
    }

    fun formatNumber(money: Float): String {
        val formatter = DecimalFormat("##.0")
        return formatter.format(money.toDouble())
    }

    fun convertMillisecondToSecondPlayer(millisecond: Int): String {
        val minute: Int
        var second: Int = millisecond / 1000
        minute = second / 60
        second = millisecond / 1000 - minute * 60
        return if (minute >= 10) {
            if (second >= 10) {
                "$minute:$second"
            } else {
                "$minute:0$second"
            }
        } else {
            if (second >= 10) {
                "0$minute:$second"
            } else {
                "0$minute:0$second"
            }
        }

    }

    fun expireTime(millisecond: Int): Int {
        return (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + millisecond)).toInt()
    }

    fun secureLink(secretKey: String, uri: String, expire_time: Int): String {
        var secureLink = secretKey + expire_time + uri
        secureLink = Base64.encodeToString(hashMd5(secureLink), Base64.NO_WRAP)
        secureLink = secureLink.replace("+", "-")
        secureLink = secureLink.replace("/", "_")
        secureLink = secureLink.replace("=", "")
        return secureLink
    }

    fun hashMd5(s: String): ByteArray? {
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                    .getInstance("MD5")
            digest.update(s.toByteArray())
            return digest.digest()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return null
    }

    fun convertSegmentsToString(segments: List<String>): String {
        var segmentPath = ""
        if (isListValid(segments)) {
            for (segment in segments) {
                segmentPath += "/$segment"
            }
        }
        return segmentPath
    }

    fun isListValid(src: List<String>): Boolean {
        return src.isNotEmpty()
    }

    fun checksum(client_id: String, timestamp: Int, secret_key: String): String {
        return sha256(client_id + timestamp + secret_key)
    }

    fun sha256(text: String): String {
        return try {
            var digest: MessageDigest? = null
            try {
                digest = MessageDigest.getInstance("SHA-256")
            } catch (e1: NoSuchAlgorithmException) {
                e1.printStackTrace()
            }
            digest!!.reset()
            bin2hex(digest.digest(text.toByteArray()))
        } catch (ignored: java.lang.Exception) {
            return ""
        }
    }


    private fun bin2hex(data: ByteArray): String {
        val hex = java.lang.StringBuilder(data.size * 2)
        for (b in data) hex.append(String.format("%02x", b and 0xFF.toByte()))
        return hex.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateString(strdate: String): String {
        val `in` = SimpleDateFormat("yyyy-MM-dd")
        val out = SimpleDateFormat("dd.MM.yyyy")

        var date: Date? = null
        try {
            date = `in`.parse(strdate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return out.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(strdate: String): String {
        val `in` = SimpleDateFormat("dd.MM.yyyy")
        val out = SimpleDateFormat("dd-MM-yyyy")

        var date: Date? = null
        try {
            date = `in`.parse(strdate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return out.format(date)
    }

    @SuppressLint("DefaultLocale")
    fun capitalize(input: String): String {
        val words =
                input.toLowerCase().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
        val builder = StringBuilder()
        try {
            for (i in words.indices) {
                val word = words[i]

                if (i > 0 && word.isNotEmpty()) {
                    builder.append(" ")
                }
                val cap = word.substring(0, 1).toUpperCase() + word.substring(1)

                builder.append(cap)
            }
        } catch (e: Exception) {
            return input
        }

        return builder.toString()
    }

    init {
        suffixes[1_000L] = "K"
        suffixes[1_000_000L] = "TR"
        suffixes[1_000_000_000L] = "G"
        suffixes[1_000_000_000_000L] = "T"
        suffixes[1_000_000_000_000_000L] = "P"
        suffixes[1_000_000_000_000_000_000L] = "E"
    }

    /***
     * this method is used to convert number into spec format number
     * @param value number you want to convert
     * @return convert 1000 ->1K
     */
    fun format(value: Long): String {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == java.lang.Long.MIN_VALUE) return format(java.lang.Long.MIN_VALUE + 1)
        if (value < 0) return "-" + format(-value)
        if (value < 1000) return value.toString() //deal with easy case

        val e = suffixes.floorEntry(value)
        val divideBy = e.key
        val suffix = e.value

        val truncated = value / (divideBy!! / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return if (hasDecimal) (truncated / 10.0).toString() + suffix else (truncated / 10).toString() + suffix
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun convertPixelsToDp(px: Float, context: Context): String {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return formatNumber(dp)
    }

    fun getBasic(username: String, password: String): String {
        val credentials = "$username:$password"
        return "Basic " + Base64.encodeToString(
                credentials.toByteArray(),
                Base64.NO_WRAP
        )
    }

    fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0)
            resources.getDimensionPixelSize(resourceId)
        else
            ceil(((if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 24 else 25) * resources.displayMetrics.density).toDouble())
                    .toInt()
    }

    fun increaseFontSizeForPath(spannable: Spannable, path: String, increaseTime: Float) {
        val startIndexOfPath = spannable.toString().indexOf(path)
        spannable.setSpan(
                RelativeSizeSpan(increaseTime), startIndexOfPath,
                startIndexOfPath + path.length, 0
        )
    }

    fun setLocale(context: Context, locale: String) {
        val res = context.resources
        val newConfig = Configuration(res.configuration)
        val loc = Locale(locale)
        newConfig.locale = loc
        newConfig.setLayoutDirection(loc)
        res.updateConfiguration(newConfig, null)
    }

    fun setTextSizeForWidth(paint: Paint, desiredWidth: Float, text: String) {
        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        val testTextSize = 48f

        // Get the bounds of the text, using our testTextSize.
        paint.textSize = testTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        // Calculate the desired size as a proportion of our testTextSize.
        val desiredTextSize = testTextSize * desiredWidth / bounds.width()

        // Set the paint for that size.

        paint.textSize = desiredTextSize
    }

    fun baselineOfTheText(paint: Paint): Float {
        val metric = paint.fontMetrics
        val textHeight = ceil((metric.descent - metric.ascent).toDouble())
        return (textHeight - metric.descent).toFloat()
    }

    fun isNumeric(strNum: String): Boolean {
        if (strNum.isBlank()) {
            return false
        }
        try {
            java.lang.Double.parseDouble(strNum)
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
        )
    }

    fun hasGPSDevice(context: Context): Boolean {
        val mgr =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mgr.allProviders
        return providers.contains(LocationManager.GPS_PROVIDER) || providers.contains(
                LocationManager.NETWORK_PROVIDER
        ) || providers.contains(LocationManager.PASSIVE_PROVIDER)
    }

    private val sNextGeneratedId = AtomicInteger(1)

    @SuppressLint("ObsoleteSdkInt")
    fun generateViewId(): Int {

        if (Build.VERSION.SDK_INT < 17) {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF)
                    newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        } else {
            return View.generateViewId()
        }

    }

    ///////////// Animation ///////////////////


    fun bounceInLeft(view: View): AnimatorSet {
        val animatorSet = AnimatorSet()
        val width = (-view.width).toFloat()

        val object1 = ObjectAnimator.ofFloat(view, "translationX", width, 0f)
        val object2 = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f, 1f, 1f)

        animatorSet.playTogether(object1, object2)
        return animatorSet
    }

    fun faceIn(view: View): AnimatorSet {
        val animatorSet = AnimatorSet()

        val `object` = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        animatorSet.playTogether(`object`)
        return animatorSet
    }

    ///////////// Animation End///////////////////

    fun checkPhoneNumber(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }

    fun finalPrice(discount: Int, sellPrice: Double): Double {
        var finalPrice = sellPrice
        if (discount > 0) {
            finalPrice = sellPrice * discount
        }
        return finalPrice
    }

    fun getIdOrderStatusData(orderStatus: Int): Int {

        return when (orderStatus) {
            1 -> {
                R.string.status_confirming
            }

            2 -> {
                R.string.status_confirm

            }

            3 -> {
                R.string.status_delivery

            }

            4 -> {
                R.string.status_delivered
            }

            5 -> {
                R.string.status_cancel_request
            }
            6 -> {
                R.string.status_cancel
            }
            7 -> {
                R.string.status_returning_cart
            }
            8 -> {
                R.string.status_returned_cart
            }
            9 -> {
                R.string.status_refunded
            }
            else -> {
                //throw IllegalArgumentException("status code is not existed")
                R.string.status_init
            }
        }
    }

    fun getCompleteAddressString(
            context: Context,
            LATITUDE: Double,
            LONGITUDE: Double
    ): String {
        var strAdd = ""
        val locale = Locale("vi_VN")
        val geoCoder = Geocoder(context, locale)
        try {
            val addresses = geoCoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd =
                        strReturnedAddress.toString().split(", Quận")[0]
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return strAdd
    }

    fun alphaAnimation(
            mView: View,
            alpha: Float,
            mDuration: Long = 200,
            animationEnd: () -> Unit
    ) {
        mView.animate().apply {
            interpolator = LinearInterpolator()
            duration = mDuration
            alpha(alpha)
            startDelay = 10
            start()
        }.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animationEnd.invoke()
            }
        })
    }

    fun increaseViewSize(view: View) {
        val valueAnimator = ValueAnimator.ofInt(view.measuredHeight, view.measuredHeight + 500)
        valueAnimator.duration = 300L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }

    fun deIncreaseViewSize(view: View) {
        val valueAnimator = ValueAnimator.ofInt(view.measuredHeight, view.measuredHeight - 500)
        valueAnimator.duration = 300L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }

    fun postUsingHttpConnection(keyword: String): String {
        val url = "https://api-public.shoppingtv.vn/api/v1.1/search/products"
        val model = SearchRequestModel(keyword)
        return URL(url)
                .openConnection()
                .let {
                    it as HttpURLConnection
                }.apply {
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    var xApiKey = ""
                    when (Config.platform) {
                        "box2018" -> {
                            xApiKey = "Bfflmd0kBdt0dHXP"
                        }
                        "box2019" -> {
                            xApiKey = "UNOvlmlgC2dC38hc"
                        }
                        "box2020" -> {
                            xApiKey = "VyEj4xjX9ckVcKc2"
                        }
                    }
                    setRequestProperty("x-api-key", xApiKey)
                    requestMethod = "POST"
                    doOutput = true
                    val outputWriter = OutputStreamWriter(outputStream)
                    outputWriter.write(Gson().toJson(model))
                    outputWriter.flush()

                }.let {
                    if (it.responseCode == 200) {
                        it.inputStream

                    } else {
                        it.errorStream

                    }
                }.let { streamToRead ->
                    BufferedReader(InputStreamReader(streamToRead)).use {
                        val response = StringBuffer()
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }

                        it.close()
                        response.toString()
                    }
                }
    }

    fun countWords(str: String): Int {
        var state = 0
        var wc = 0  // word count
        var i = 0

        // Scan all characters one by one
        while (i < str.length) {
            // If next character is a separator, set the
            // state as OUT
            if (str[i] == ' ' || str[i] == '\n'
                    || str[i] == '\t'
            )
                state = 0


            // If next character is not a word separator
            // and state is OUT, then set the state as IN
            // and increment word count
            else if (state == 0) {
                state = 1
                ++wc
            }

            // Move to next character
            ++i
        }
        return wc
    }

    fun getAreaString(list: ArrayList<Region>): String {
        var areas = ""
        if (list.size > 0) {
            val sb = StringBuffer()
            for (i in 0 until list.size) {
                sb.append(list[i].name)
                if (i < list.size - 1) {
                    sb.append(", ")
                }
            }
            areas = sb.toString()
        }
        return areas
    }

    fun isAvailableArea(currentProvince: String, list: ArrayList<Region>): Boolean {
        if (list.size > 0) {
            for (areas in list) {
                if (currentProvince == areas.uid || areas.vn_all_province) {
                    return true
                }
            }
        }
        return false
    }

    fun isDisableCod(list: ArrayList<Product>?): Boolean {
        if (list != null && list.size > 0) {
            for (product in list) {
                if (product.is_disabled_cod) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * @param link is string in from launcher app
     * ex: https://shoppingtv.vn/home?product_id=0x12c8a8&type=ads&package=com.bda.omnilibrary&token=
     * @return id of product
     */

    fun getProductIdFromAppLink(link: String): String? {

        if (link.isBlank())
            return ""

        try {
            val url = URL(link)

            val queryPairs: Map<String, String> = LinkedHashMap()

            if (url.query != null) {
                val query = url.query
                val pairs = query.split("&")
                for (pair in pairs) {
                    val idx = pair.indexOf("=")

                    (queryPairs as LinkedHashMap).put(
                            URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                            URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                    )
                }

                return if (queryPairs.containsKey("product_id") && queryPairs["product_id"] != null) {
                    queryPairs["product_id"]
                } else {
                    ""
                }
            }

            return null
        } catch (e: MalformedURLException) {
            return ""
        } catch (e: StringIndexOutOfBoundsException) {
            return ""
        }
    }

    fun getValueFromAppLink(link: String, lookingValue: String): String? {
        if (link.isBlank())
            return ""

        try {
            val url = URL(link)

            val queryPairs: Map<String, String> = LinkedHashMap()

            if (url.query != null) {
                val query = url.query
                val pairs = query.split("&")
                for (pair in pairs) {
                    val idx = pair.indexOf("=")

                    (queryPairs as LinkedHashMap).put(
                            URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                            URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                    )
                }

                return if (queryPairs.containsKey(lookingValue) && queryPairs[lookingValue] != null) {
                    queryPairs[lookingValue]
                } else {
                    ""
                }
            }

            return null
        } catch (e: MalformedURLException) {
            return ""
        } catch (e: StringIndexOutOfBoundsException) {
            return ""
        }
    }

    @SuppressLint("DefaultLocale")
    fun getMacAddress(): String? {
        return try {
            loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17)
        } catch (e: IOException) {
            ""
        }
    }

    @Throws(IOException::class)
    fun loadFileAsString(filePath: String?): String {
        val fileData = StringBuffer(1000)
        val reader = BufferedReader(FileReader(filePath))
        val buf = CharArray(1024)
        var numRead: Int
        while (reader.read(buf).also { numRead = it } != -1) {
            val readData = String(buf, 0, numRead)
            fileData.append(readData)
        }
        reader.close()
        return fileData.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(millisecond: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return dateFormat.format(Date(millisecond))
    }

    fun isExpTimestamp(time: Long): Boolean {
        if (System.currentTimeMillis() < time) {
            return true
        }
        return false
    }

    fun isVideoInProduct(product: Product): Product.MediaType? {
        if (product.videos.isEmpty()) {
            return null
        }

        for (i in product.videos) {
            if (i.mediaType == "video")
                return i
        }

        return null
    }

    fun getVideoUrl(item: Product.MediaType): String? {
        if (item.mediaType == "video") {
            if (item.url.isNotBlank() && ImageUtils.localUrl != null) {
                return if (!item.url.contains("m3u8")) {
                    "${ImageUtils.videoUrl}/${item.url}"
                } else {
                    item.url
                }
            }
        }
        return null
    }

    /*fun loadJSONFromAsset(activity: Activity): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = activity.assets.open("test_json.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, "UTF-8")
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }*/

    fun setStrokeFocus(view: MaterialCardView, mActivity: Activity) {
        view.cardElevation =
                mActivity.resources.getDimension(R.dimen._4sdp)

        view.strokeWidth = 8
        view.strokeColor =
                ContextCompat.getColor(mActivity, R.color.start_color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor =
                    ContextCompat.getColor(mActivity, R.color.end_color)
        }
    }


    fun setThinStrokeFocus(view: MaterialCardView, mActivity: Activity) {
        view.cardElevation =
                mActivity.resources.getDimension(R.dimen._4sdp)

        view.strokeWidth = 6
        view.strokeColor =
                ContextCompat.getColor(mActivity, R.color.start_color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor =
                    ContextCompat.getColor(mActivity, R.color.end_color)
        }
    }


    fun setStrokeFocusHybridBrandShop(view: MaterialCardView, mActivity: Activity) {
        view.strokeWidth = 8
        view.strokeColor =
                ContextCompat.getColor(mActivity, R.color.start_color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor =
                    ContextCompat.getColor(mActivity, R.color.end_color)
        }
    }

    fun setLostFocusHybridBrandShop(view: MaterialCardView, mActivity: Activity) {
        view.strokeWidth = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor =
                    ContextCompat.getColor(mActivity, R.color.text_black_70)
        }
    }

    val mBrandShopSkinHandler: Handler = Handler()
    val mBrandShopIntervalHandler: Handler = Handler()
    fun setBrandFocus(
            view: MaterialCardView,
            tv_size: Int,
            ic_tick: ImageView,
            mActivity: Activity,
    ) {
        mBrandShopIntervalHandler.postDelayed(
                {
                    view.cardElevation =
                            0f
                    view.maxCardElevation = 0f
                    view.strokeWidth = 4
                    view.strokeColor =
                            ContextCompat.getColor(mActivity, R.color.color_a1b753)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        view.outlineSpotShadowColor =
                                ContextCompat.getColor(mActivity, R.color.text_black_70)
                    }


                    var anim = ValueAnimator.ofInt(120, tv_size)
                    anim.addUpdateListener { valueAnimator ->
                        val value = valueAnimator.animatedValue as Int
                        val layoutParams: ViewGroup.LayoutParams =
                                view.getLayoutParams()
                        layoutParams.width = value
                        view.setLayoutParams(layoutParams)
                        if (value > tv_size / 2) {
                            ic_tick.visibility = View.GONE
                        }
                    }
                    anim.duration = 300
                    anim.start()

                    mBrandShopSkinHandler.removeCallbacksAndMessages(null)
                    mBrandShopSkinHandler.postDelayed(
                            {
                                view.cardElevation =
                                        mActivity.resources.getDimension(R.dimen._1sdp)

                                view.strokeWidth = 0

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    view.outlineSpotShadowColor =
                                            ContextCompat.getColor(mActivity, R.color.text_black_70)
                                }
                                var anim = ValueAnimator.ofInt(tv_size, 100)
                                anim.addUpdateListener { valueAnimator ->
                                    val value = valueAnimator.animatedValue as Int
                                    val layoutParams: ViewGroup.LayoutParams =
                                            view.getLayoutParams()
                                    layoutParams.width = value
                                    view.setLayoutParams(layoutParams)
                                    if (value < tv_size / 2 || value >= 100) {
                                        ic_tick.visibility = View.VISIBLE
                                    }
                                }
                                anim.duration = 300
                                anim.start()
                            }, 10000
                    )
                }, 1000
        )

    }

    fun setBrandLostFocus(
            view: MaterialCardView,
            tv_size: Int,
            ic_tick: ImageView,
            mActivity: Activity,
    ) {
        mBrandShopIntervalHandler.removeCallbacksAndMessages(null)
        val layoutParams: ViewGroup.LayoutParams =
                view.layoutParams
        if (layoutParams.width > 100) {

            view.cardElevation =
                    1f
            view.maxCardElevation = 1f

            view.strokeWidth = 0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                view.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.text_black_70)
            }

            var anim = ValueAnimator.ofInt(tv_size, 100)
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                val layoutParams: ViewGroup.LayoutParams =
                        view.getLayoutParams()
                layoutParams.width = value
                view.setLayoutParams(layoutParams)
                if (value < tv_size / 2 || value >= 100) {
                    ic_tick.visibility = View.VISIBLE
                }
            }
            anim.duration = 300
            anim.start()
        }
    }

    fun setLostFocus(view: MaterialCardView, mActivity: Activity) {
        view.cardElevation =
                mActivity.resources.getDimension(R.dimen._2sdp)

        view.strokeWidth = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.outlineSpotShadowColor =
                    ContextCompat.getColor(mActivity, R.color.text_black_70)
        }
    }

    fun translateYAnimation(mView: View, translate: Float, animationEnd: () -> Unit) {
        mView.animate().apply {
            interpolator = LinearInterpolator()
            duration = 200
            translationY(translate)
            startDelay = 10
            start()
        }.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animationEnd.invoke()
            }
        })
    }

    @SuppressLint("DefaultLocale")
    fun firstLetterUppercasedAndFilterAcronym(str: String): String {
        if (str.isBlank())
            return ""

        val letters = arrayListOf("TNHH", "CP", "TMDV", "XNK")
        var output = ""

        for (word in str.split(" ").toMutableList()) {
            if (letters.contains(word)) {
                output += word.toUpperCase() + " "
            } else {
                output += word.toLowerCase().capitalize() + " "
            }
        }
        return output.trim()
    }

    fun createHtmlFormDescription(description: Product.Description): String {
        var result = ""

        for (block in description.blocks) {
            if (block.type == "header") {
                result += "<h${block.data.level}>${block.data.text}</${block.data.level}> <br><br>"

            } else if (block.type == "paragraph") {
                result += "<p>${block.data.text}</p>"
            }
        }

        return result
    }

    fun getParagraphDescription(description: Product.Description): String {
        for (block in description.blocks) {
            if (block.type == "paragraph") {
                return block.data.text
            }
        }
        return ""
    }


    fun gradientText(textView: SfTextView, startColor: Int, endColor: Int): Shader {
        val paint: TextPaint = textView.paint
        val width: Float = paint.measureText(textView.text.toString())
        return LinearGradient(
                0f, 0f, width, textView.textSize, intArrayOf(
                startColor,
                endColor
        ), null, Shader.TileMode.CLAMP
        )
    }

    fun checkCancelableOrder(order: ListOrderResponceV3.Data): Boolean {
        return (order.orderStatus == 0 || order.orderStatus == 1) && order.payStatus == 3 //&& order.pay_gateway != "cod"
    }

    fun toListProduct(items: ArrayList<Product>): String {
        val builder = StringBuilder()
        for (i in 0 until items.size) {
            builder.append(
                    ItemProduct(
                            itemName = items[i].name,
                            itemId = items[i].uid,
                            itemNo = i.toString(),
                            itemQuantity = items[i].order_quantity.toString(),
                            price = items[i].price.toString(),
                    ).toString()
            )
            if (i < items.size - 1) {
                builder.append(", ")
            }
        }
        return builder.toString()
    }

    fun convertOrderStatus(orderStatus: Int): String {
        when (orderStatus) {
            0 -> {
                return "initial"
            }

            1 -> {
                return "confirming"
            }

            2 -> {
                return "confirmed"
            }

            3 -> {
                return "delivering"
            }

            4 -> {
                return "delivered"
            }

            5 -> {
                return "cancel_request"
            }

            6 -> {
                return "cancel"
            }

            7 -> {
                return "returning_cart"

            }
            8 -> {
                return "returned_cart"
            }

            9 -> {
                return "refunded"
            }
        }
        return ""
    }

    fun convertOrderPayStatus(orderPayStatus: Int): String {
        when (orderPayStatus) {
            1 -> {
                return "Success"
            }

            2 -> {
                return "Failed"
            }
            3 -> {
                return "Pending"
            }
        }
        return ""
    }

    fun formatCount(count: Int): String {
        val divide: Int = count / 100
        return if (divide == 0) "$count" else "${divide * 100}+"
    }

    fun mappingCartToRequestForQuickPay(
            userInfo: CheckCustomerResponse?,
            addressData: CheckCustomerResponse?,
            products: ArrayList<Product>,
            vaucher: String,
            voucher_uid: String,
            model: CustomerProfileResponse,
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                // quick pay
                items.add(PaymentRequest.Item(item.uid, 1))
            }
        }

        if (userInfo != null && addressData != null && items.size > 0) {
            var districtCode = ""
            var provinceCode = ""
            var addressType = 1
            var addressDes = ""

            model.address?.let { address ->
                addressType = address.address_type ?: 1
                addressDes = address.address_des ?: ""
                address.district?.let { district ->
                    districtCode = district.uid ?: ""
                }
                address.province?.let { province ->
                    provinceCode = province.uid ?: ""
                }
            }
            return PaymentRequest(
                    platform = "fptplaybox",
                    payType = "quickpay",
                    cid = userInfo.data.uid,
                    voucher_code = vaucher, voucher_uid = voucher_uid,
                    phone = addressData.data.alt_info[0].phone_number,
                    address = addressData.data.alt_info[0].address.address_des,
                    province = addressData.data.alt_info[0].address.customer_province.uid,
                    district = addressData.data.alt_info[0].address.customer_district.uid,
                    items = items,
                    addressType = addressData.data.alt_info[0].address.address_type,
                    name = addressData.data.alt_info[0].customer_name,
                    requestDeliveryTime = "all_day",
                    note = "",
                    create_address_type = addressType,
                    created_address_des = addressDes,
                    created_customer_name = model.name ?: "",
                    created_district = districtCode,
                    created_province = provinceCode,
                    created_phone_number = model.phone ?: ""
            )
        }
        return null
    }

    fun decodeImageBase64(encodedDataString: String, context: Context): Drawable? {
        val data = encodedDataString.replace("data:image/jpeg;base64,", "")

        try {
            val imageAsBytes: ByteArray = Base64.decode(data, Base64.DEFAULT)

            return BitmapDrawable(
                    context.resources, BitmapFactory.decodeByteArray(
                    imageAsBytes, 0, imageAsBytes.size
            )
            )
        } catch (e: IllegalArgumentException) {

        }

        return null
    }

    fun sortListBySupplier(list: List<Product>): ArrayList<Product> {
        val pairList = ArrayList<Pair<String, ArrayList<Product>>>()

        for (i in list.reversed()) {
            val mList = findPairList(i.supplier.supplier_id, pairList)

            if (mList != null) {
                mList.add(i)
            } else {
                pairList.add(Pair(i.supplier.supplier_id, arrayListOf(i)))
            }
        }

        val newList = ArrayList<Product>()

        pairList.forEach {
            it.second[0].is_first_supplier = true
            newList.addAll(it.second)
        }

        return newList
    }

    fun getListSupplierProduct(list: List<Product>): ArrayList<Pair<String, ArrayList<Product>>> {
        val pairList = ArrayList<Pair<String, ArrayList<Product>>>()

        for (i in list.reversed()) {
            val mList = findPairList(i.supplier.supplier_id, pairList)

            if (mList != null) {
                mList.add(i)
            } else {
                pairList.add(Pair(i.supplier.supplier_id, arrayListOf(i)))
            }
        }

        return pairList
    }

    fun getCountSupplierProduct(list: ArrayList<Pair<String, ArrayList<Product>>>): Int {
        var count = 0

        for (i in list) {
            count += i.second.size
        }

        return count
    }

    fun checkSupplierCondition(list: List<Product>): Boolean {
        val pairList = ArrayList<Pair<String, ArrayList<Product>>>()
        var result = false

        for (i in list) {
            if (i.supplier.required_order_value > 0f) {
                val mList = findPairList(i.supplier.supplier_id, pairList)

                if (mList != null) {
                    mList.add(i)
                } else {
                    pairList.add(Pair(i.supplier.supplier_id, arrayListOf(i)))
                }
            }
        }

        for (i in pairList) {
            var total = 0.0
            for (j in i.second) {
                total += j.price * j.order_quantity
            }

            if (total < i.second[0].supplier.required_order_value) {
                result = true
                for (j in i.second)
                    j.isDisableBySupplierCondition = true
            } else {
                for (j in i.second)
                    j.isDisableBySupplierCondition = false
            }
        }

        return result
    }

    fun removeProductBySupplierCondition(list: ArrayList<Product>): ArrayList<Product> {
        val pairList = ArrayList<Pair<String, ArrayList<Product>>>()

        for (i in list) {
            if (i.supplier.required_order_value > 0f) {
                val mList = findPairList(i.supplier.supplier_id, pairList)

                if (mList != null) {
                    mList.add(i)
                } else {
                    pairList.add(Pair(i.supplier.supplier_id, arrayListOf(i)))
                }
            }
        }

        for (i in pairList) {
            var total = 0.0
            for (j in i.second) {
                total += j.price * j.order_quantity
            }

            if (total < i.second[0].supplier.required_order_value) {
                list.removeAll(i.second)
            }
        }

        return list
    }

    fun getProductsBySupplierCondition(list: ArrayList<Product>): ArrayList<Product> {
        val pairList = ArrayList<Pair<String, ArrayList<Product>>>()

        val newList = arrayListOf<Product>()

        for (i in list) {
            if (i.supplier.required_order_value > 0f) {
                val mList = findPairList(i.supplier.supplier_id, pairList)

                if (mList != null) {
                    mList.add(i)
                } else {
                    pairList.add(Pair(i.supplier.supplier_id, arrayListOf(i)))
                }
            }
        }

        for (i in pairList) {
            var total = 0.0
            for (j in i.second) {
                total += j.price * j.order_quantity
            }

            if (total < i.second[0].supplier.required_order_value) {
                newList.addAll(i.second)
            }
        }

        return newList
    }


    fun findPairList(
            uid: String,
            list: ArrayList<Pair<String, ArrayList<Product>>>,
    ): ArrayList<Product>? {
        if (list.isEmpty())
            return null

        for (i in list) {
            if (uid == i.first)
                return i.second
        }

        return null
    }

    fun getShippingTimeBySupplier(t: Int): String {
        val rightNow = Calendar.getInstance()
        val now = rightNow.get(Calendar.HOUR_OF_DAY)

        val data = now + t
        return if (data < 9) {
            "Hôm nay"
        } else if (data >= 20) {
            var dayDuration: Int = data / 24
            if (dayDuration < 1)
                dayDuration = 1
            rightNow.add(Calendar.DAY_OF_YEAR, dayDuration)
            val day: Date = rightNow.time
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            dateFormat.format(day)
        } else {
            "${data}:00 hôm nay"
        }
    }

    fun shippingTime(data: ArrayList<Product>): String {
        var minTime = Int.MAX_VALUE
        for (i in data!!) {
            if (i.supplier.shipping_time in 1 until minTime) {
                minTime = i.supplier.shipping_time
            }
        }

        return if (minTime != Int.MAX_VALUE)
            getShippingTimeBySupplier(minTime)
        else ""
    }

    fun shippingTime(day: String, t: Int): String {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        calendar.time = format.parse(day)

        return if (t >= 24) {
            val dayDuration: Int = t / 24
            calendar.add(Calendar.DAY_OF_YEAR, dayDuration)
            val day: Date = calendar.time
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            dateFormat.format(day)
        } else {
            day
        }
    }

    fun getDefaultAddress(profile: CustomerProfileResponse): ContactInfo? {
        if (profile.alt_info == null || profile.alt_info?.size == 0)
            return null

        for (i in profile.alt_info!!) {
            if (i.is_default_address)
                return i
        }

        return null
    }

    fun getItemFromSubOrder(data: ListOrderResponceV3.Data): ArrayList<ListOrderResponceV3.Data.Item> {
        val list = ArrayList<ListOrderResponceV3.Data.Item>()

        data.sub_orders.forEach { it ->
            list.addAll(it.items)
        }

        return list
    }

    fun getItemCountFromSubOrder(data: ListOrderResponceV3.Data): Int {
        var count = 0

        data.sub_orders.forEach { it ->
            count += it.items.size
        }

        return count
    }

    fun getItemCountFromSubOrder(data: ArrayList<ListOrderResponceV3.Data.SubOrder>): Int {
        var count = 0

        data.forEach { it ->
            count += it.items.size
        }

        return count
    }

    fun isPackageExisted(context: Context, targetPackage: String): Boolean {
        val packages: List<ApplicationInfo>
        val pm: PackageManager = context.packageManager
        packages = pm.getInstalledApplications(0)
        for (packageInfo in packages) {
            if (packageInfo.packageName == targetPackage) return true
        }
        return false
    }

    fun disableSoftInputFromAppearing(editText: EditText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
            editText.setTextIsSelectable(true)
        } else {
            editText.setRawInputType(InputType.TYPE_NULL)
            editText.isFocusable = true
        }
    }

    fun setField(
        these: Any,
        fieldName: String,
        value: Any
    ) {
        try {
            val f1: Field? =
               getDeclaredField(
                    these.javaClass,
                    fieldName
                )
            if (f1 != null) {
                // Change private modifier to public
                f1.isAccessible = true
                // Remove final modifier (don't working!!!)
                //Field modifiersField = Field.class.getDeclaredField("modifiers");
                //modifiersField.setAccessible(true);
                //modifiersField.setInt(f1, f1.getModifiers() & ~Modifier.FINAL);
                // Set field (at last)
                f1[these] = value
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getField(these: Any, fieldName: String): Any? {
        try {
            val f1: Field? =
                getDeclaredField(
                    these.javaClass,
                    fieldName
                )
            if (f1 != null) {
                f1.isAccessible = true
                return f1[these]
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getDeclaredField(
        aClass: Class<*>?,
        fieldName: String
    ): Field? {
        if (aClass == null) { // null if superclass is object
            return null
        }
        var f1: Field? = null
        f1 = try {
            aClass.getDeclaredField(fieldName)
        } catch (e: NoSuchFieldException) {
            getDeclaredField(aClass.superclass, fieldName)
        }
        return f1
    }

    fun getCurrentDateTimeShort(context: Context): String? {
        return getCurrentTimeShort(
            context,
            true,
            true
        )
    }

    fun getCurrentTimeShort(context: Context): String? {
        return getCurrentTimeShort(
            context,
            false,
            true
        )
    }

    private fun getCurrentTimeShort(
        context: Context,
        showDate: Boolean,
        showTime: Boolean
    ): String? {
        val datePattern = "EEE d MMM"

        // details: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        val timePattern =
            if (is24HourLocale(context)) "H:mm" else "h:mm a"
        val serverFormat = SimpleDateFormat(
            String.format(
                "%s%s",
                if (showDate) "$datePattern " else "",
                if (showTime) timePattern else ""
            ),
            Locale.getDefault()
        )
        val currentTime = serverFormat.format(Date())
        return String.format("%1\$s", currentTime)
    }

    private fun is24HourLocale(context: Context): Boolean {
        val locale: Locale = getCurrentLocale(context)
        val natural = DateFormat.getTimeInstance(
            DateFormat.LONG, locale
        )
        return if (natural is SimpleDateFormat) {
            val pattern = natural.toPattern()
            pattern.indexOf('H') >= 0
        } else {
            true
        }
    }

    fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }

    fun isPictureInPictureSupported(context: Context?): Boolean {
        return if (context == null) {
            false
        } else VERSION.SDK_INT >= 24 && context.packageManager
            .hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }

    fun getDeviceRam(context: Context?): Int {
        if (context == null || VERSION.SDK_INT < 16) {
            return -1
        }
        val actManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        if (actManager != null) {
            actManager.getMemoryInfo(memInfo)
        } else return 500000000 //safe value for devices with 1gb or more...
        return (memInfo.totalMem / 18).toInt()
    }

    fun toStream(content: String?): InputStream? {
        return if (content == null) {
            null
        } else ByteArrayInputStream(content.toByteArray(Charset.forName("UTF8")))
    }

    fun isPlaying(player: ExoPlayer?): Boolean {
        return player?.isPlaying ?: false

        // Exo 2.9
        //return player.getPlayWhenReady() && player.getPlaybackState() == Player.STATE_READY;

        // Exo 2.10 and up
    }

}