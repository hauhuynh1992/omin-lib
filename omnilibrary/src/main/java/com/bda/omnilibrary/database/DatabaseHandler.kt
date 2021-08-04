package com.bda.omnilibrary.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.bda.omnilibrary.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val KEY_PRODUCT_ID = "product_id"
    private val KEY_BODY = "body"
    private val KEY_HISTORY = "history"
    private val KEY_LOG = "log"

    private var product_list: ArrayList<Product> = ArrayList()
    private var search_history_list: ArrayList<String> = ArrayList()
    private var log_list: ArrayList<String> = ArrayList()

    companion object {
        // Database Version
        private val DATABASE_VERSION = 5

        // Database Name
        private val DATABASE_NAME = "shoppingDBManager"

        // Contacts table name
        private val TABLE_CART = "table_cart"
        private val TABLE_SEARCH_HISTORY = "table_search_history"
        private val TABLE_TRACKING_LOG = "table_tracking_log"
    }

    // Creating Tables
    @Suppress("LocalVariableName")
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_CART = "CREATE TABLE $TABLE_CART($KEY_PRODUCT_ID TEXT,$KEY_BODY TEXT)"
        db.execSQL(CREATE_TABLE_CART)
        val CREATE_TABLE_HISTORY = "CREATE TABLE $TABLE_SEARCH_HISTORY($KEY_HISTORY TEXT)"
        db.execSQL(CREATE_TABLE_HISTORY)
        val CREATE_TRACKING_LOG = "CREATE TABLE $TABLE_TRACKING_LOG($KEY_LOG TEXT)"
        db.execSQL(CREATE_TRACKING_LOG)

    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SEARCH_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRACKING_LOG")
        // Create tables again
        onCreate(db)
    }

    ////////////TABLE_CART////////////////
    fun insertProduct(product_id: String, body: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_PRODUCT_ID, product_id)
        values.put(KEY_BODY, body)
        // Inserting Row
        db.insert(TABLE_CART, null, values)
        db.close() // Closing database connection
    }

    @Suppress("unused")
    fun insertHistory(text: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_HISTORY, text)
        // Inserting Row
        db.insert(TABLE_SEARCH_HISTORY, null, values)
        db.close() // Closing database connection
    }

    fun insertLog(text: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_LOG, text)
        // Inserting Row
        db.insert(TABLE_TRACKING_LOG, null, values)
        db.close() // Closing database connection
    }

    fun getLProductList(): ArrayList<Product> {
        try {
            product_list.clear()
            // Select All Query
            val selectQuery =
                "SELECT * FROM table_cart"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val finalOutputString: Product =
                        Gson().fromJson(
                            cursor.getString(cursor.getColumnIndex(KEY_BODY)),
                            object : TypeToken<Product>() {}.type
                        )
                    product_list.add(finalOutputString)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return product_list
        } catch (e: Exception) { // TODO: handle exception
            Log.e("product_list", "" + e)
        }
        return product_list
    }

    @Suppress("unused")
    fun getSearchHistoryList(): ArrayList<String> {
        try {
            search_history_list.clear()
            // Select All Query
            val selectQuery =
                "SELECT * FROM table_search_history"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val history = cursor.getString(cursor.getColumnIndex(KEY_HISTORY))
                    search_history_list.add(history)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return search_history_list
        } catch (e: Exception) { // TODO: handle exception
            Log.e("search_history_list", "" + e)
        }
        return search_history_list
    }

    fun getTrackingLog(): ArrayList<String> {
        try {
            log_list.clear()
            // Select All Query
            val selectQuery =
                "SELECT * FROM table_tracking_log"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val history = cursor.getString(cursor.getColumnIndex(KEY_LOG))
                    log_list.add(history)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return log_list
        } catch (e: Exception) { // TODO: handle exception
            Log.e("log_list", "" + e)
        }
        return log_list
    }

    fun getExistItem(uid: String): Product? {
        var mProduct: Product? = null
        val selectQuery =
            "SELECT * FROM table_cart WHERE product_id=?"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(uid))
        if (cursor.moveToFirst()) {
            mProduct =
                Gson().fromJson<Product>(
                    cursor.getString(cursor.getColumnIndex(KEY_BODY)),
                    object : TypeToken<Product>() {}.type
                )
        }
        cursor.close()
        db.close()
        return mProduct
    }

    @Suppress("unused")
    fun getSearchExistItem(textHistory: String): String? {
        var text: String? = null
        val selectQuery =
            "SELECT * FROM table_search_history WHERE history=?"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(textHistory))
        if (cursor.moveToFirst()) {
            text = cursor.getString(cursor.getColumnIndex(KEY_HISTORY))

        }
        cursor.close()
        db.close()
        return text
    }


    fun getCountCartItem(): Int {
        var count = 0
        val countQuery = "SELECT  * FROM " + TABLE_CART
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        count = cursor.count
        cursor.close()
        db.close()
        // return count
        return count
    }

    @Suppress("unused")
    fun getCountHistoryItem(): Int {
        var count = 0
        val countQuery = "SELECT  * FROM " + TABLE_SEARCH_HISTORY
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        count = cursor.count
        cursor.close()
        db.close()
        // return count
        return count
    }

    fun getCountLogItem(): Int {
        var count = 0
        val countQuery = "SELECT  * FROM " + TABLE_TRACKING_LOG
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        count = cursor.count
        cursor.close()
        db.close()
        // return count
        return count
    }

    fun deleteItemCart(uid: String) {
        val db = this.writableDatabase
        db.delete(TABLE_CART, "product_id = ?", arrayOf(uid))
        db.close()
    }

    @Suppress("unused")
    fun deleteItemHistory(textHistory: String) {
        val db = this.writableDatabase
        db.delete(TABLE_SEARCH_HISTORY, "history = ?", arrayOf(textHistory))
        db.close()
    }

    fun updateItemCart(uid: String, body: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_BODY, body)
        db.update(TABLE_CART, values, "product_id = ?", arrayOf(uid))
        db.close()
    }

    fun deleteCart() {
        val db = this.writableDatabase
        db.delete(TABLE_CART, null, null)
        db.close()
    }

    @Suppress("unused")
    fun deleteHistory() {
        val db = this.writableDatabase
        db.delete(TABLE_SEARCH_HISTORY, null, null)
        db.close()
    }

    fun deleteLogTracking() {
        val db = this.writableDatabase
        db.delete(TABLE_TRACKING_LOG, null, null)
        db.close()
    }
}