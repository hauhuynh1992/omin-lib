package com.bda.omnilibrary.dialog.assitant.helper

import android.content.Context
import android.util.Log
import com.bda.omnilibrary.model.Collections
import com.bda.omnilibrary.model.Product
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

object HandlerAssistantRequest {

    fun getBinhGiuNhiet(mContext: Context, json: String): ArrayList<String> {
        Log.d("AAA", "--------" + json)
        var products = ArrayList<Product>()
        val listBrands = ArrayList<String>()
        try {
            val obj = JSONObject(getData(mContext, json))
            val list_data = obj.getJSONArray("data")
            for (i in 0..list_data.length() - 1) {
                var collect_name = list_data.getJSONObject(i).getString("collection_name")
                listBrands.add(collect_name)
//                try {
//                    count_items = list_data.getJSONObject(i).getInt("count_items")
//                } catch (e: java.lang.Exception) {
//                    Log.d("AAA", e.message.toString())
//                }
//                if (count_items > 0) {
//                    val list_product = list_data.getJSONObject(i).getJSONArray("products")
//                    for (i in 0..list_product.length() - 1) {
//                        val product = list_product.getJSONObject(i)
//                        try {
//                            val brand = product.getJSONObject("product.brand")
//                            val name = brand.get("brand_name")
//                            listBrands.add(name.toString())
//                        } catch (e: Exception) {
//                            Log.d("AAA", e.message.toString())
//                        }
//                    }
//                }
            }
        } catch (e: JSONException) {
            Log.d("AAA", e.message.toString())
            e.printStackTrace()
        }
        Log.d("AAA", "--------END" + json)
        return listBrands
    }

    fun isSameBrand(products: ArrayList<Product>): Boolean {
        val brand = products.map { it.brand.name }.distinct()
        if (brand.size > 1) {
            return false
        } else {
            return true
        }
    }

    fun getBrand(products: ArrayList<Product>): ArrayList<String> {
        val brands = ArrayList<String>()
        val filter = products.map { it.brand.name }.distinct()
        filter.forEach {
            brands.add(it)
        }
        return brands
    }


    fun getCollectName(data: ArrayList<Collections>): ArrayList<String> {
        val name = ArrayList<String>()
        val filter = data.map { it.collection_name }.distinct()
        filter.forEach {
            name.add(it)
        }
        return name
    }

    fun convertPrice(str: String): Double {
        // 100.000
        // 100 ng??n
        // 1 tri???u
        if (str.contains("tri???u")) {
            val num = str.replace("[^0-9]".toRegex(), "")
            Log.d("AAA", (num.toLong() * 1000000).toString())
            return num.toDouble() * 1000000
        } else if (str.contains("ng??n") || str.contains("ngh??n")) {
            val num = str.replace("[^0-9]".toRegex(), "")
            Log.d("AAA", (num.toLong() * 100000).toString())
            return num.toDouble() + 100000
        } else {
            val num = str.replace("[^0-9]".toRegex(), "")
            Log.d("AAA", (num.toLong()).toString())
            return num.toDouble()
        }
    }

    fun getPositionByString(strPosition: String): Int {
        var position = 0
        when (strPosition) {
            "s??? 1",
            "?????u ti??n",
            "th??? nh???t" -> return 0
            "s??? 2",
            "th??? hai",
            "th??? 2" -> return 1
            "s??? 3",
            "th??? ba",
            "th??? 3" -> return 2
            "s??? 4",
            "th??? t??",
            "th??? 4" -> return 3
            "s??? 5",
            "th??? n??m",
            "th??? 5" -> return 4
            "s??? 6",
            "th??? s??u",
            "th??? 6" -> return 5
            "s??? 7",
            "th??? b???y",
            "th??? 7" -> return 6
            "s??? 8",
            "th??? t??m",
            "th??? 8" -> return 7
            "s??? 9",
            "th??? ch??n",
            "th??? 9" -> return 8
            "s??? 10",
            "th??? m?????i",
            "th??? 10" -> return 9
            "cu???i c??ng" -> return LAST_POSITION_VALUE
        }
        return position
    }

    private fun getData(context: Context, keyword: String): String {
        var json: String? = null
        try {
            val inputStream = context.getAssets().open("data/" + keyword)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

//    fun getAllCity(mContext: Context): ArrayList<CityModel> {
//        var cities = ArrayList<CityModel>()
//        try {
//            val obj = JSONArray(loadCityFromAsset(mContext))
//            for (i in 0..obj.length() - 1) {
//                val city = obj.getJSONObject(i)
//                cities.add(CityModel(city.getString("name"), city.getString("code")))
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        return cities
//    }

    //            activity?.let {
//                val list = ArrayList<String>()
//                list.add("dien_gia_dung.json")
//                list.add("dien_tu.json")
//                list.add("dung_cu_nha_bep.json")
//                list.add("hang_tieu_dung.json")
//                list.add("may_mac.json")
//                list.add("me_va_be.json")
//                list.add("my_pham.json")
//                list.add("nha_cua.json")
//                list.add("suc_khoe.json")
//                list.add("thuc_pham_tuoi_song.json")
//                val result = ArrayList<String>()
//                list.forEach { json ->
//                    result.addAll(HandlerAssistantRequest.getBinhGiuNhiet(it, json))
//                }
//
//                result.distinct().forEach { name ->
//                    Log.d("AAA", name)
//                }
//            }

    const val LAST_POSITION_VALUE = -1

}
