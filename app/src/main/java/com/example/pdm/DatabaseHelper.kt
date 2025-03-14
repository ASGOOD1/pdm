package com.example.pdm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createAccountsTableQuery = ("CREATE TABLE IF NOT EXISTS accounts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user TEXT," +
                "email TEXT," +
                "password TEXT," +
                "status INTEGER DEFAULT 0," +
                "token TEXT)")
        db?.execSQL(createAccountsTableQuery)


        val createSuppliersTableQuery = ("CREATE TABLE IF NOT EXISTS suppliers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT)")
        db?.execSQL(createSuppliersTableQuery)

        val createProductsQuery = ("CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "supplier INTEGER DEFAULT 0," +
                "price INTEGER DEFAULT 0," +
                "name TEXT)")
        db?.execSQL(createProductsQuery)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        onCreate(db)

    }

    fun loadProducts(supplier: Long) : MutableList<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val selection = "supplier = ?"
        val selectionArgs = arrayOf(supplier.toString())
        val cursor = db.query("products", null, selection, selectionArgs, null, null, null)
        while(cursor.moveToNext()) {
            products.add(Product(cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("supplier")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getInt(cursor.getColumnIndexOrThrow("price"))))
        }

        cursor.close()
        return products
    }


    fun loadSuppliers(): MutableList<String> {
        val suppliers = mutableListOf<String>()
        suppliers.add("None")
        val db = readableDatabase
        val cursor = db.query("suppliers", null, null, null, null, null, null, null)
        while(cursor.moveToNext()) {
            suppliers.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
        }
        cursor.close()
        return suppliers
    }

    fun updateAccount(token: String, user: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("token", token)
        }
        db.update("accounts", values, "user=?", arrayOf(user))
    }

    fun insertAccount(user: String, email: String, pwd: String): Long {
        val values = ContentValues().apply {
            put("user", user)
            put("email", email)
            put("password", pwd)
        }
        val db = writableDatabase
        return db.insert("accounts", null, values)
    }

    fun loginAccountExists(user: String, pwd: String): Boolean {
        val db = readableDatabase
        val selection = "user = ? AND password = ?"
        val selectionArgs = arrayOf(user, pwd)
        val cursor = db.query("accounts", null, selection, selectionArgs, null, null, null, null)
        val userExists = cursor.count > 0

        cursor.close()
        return userExists
    }

    fun userOrEmailExists(user: String, email: String): Boolean {
        val db = readableDatabase
        val selection = "user = ? OR email = ?"
        val selectionArgs = arrayOf(user, email)
        val cursor = db.query("accounts", null, selection, selectionArgs, null, null, null, null)
        val userExists = cursor.count > 0

        cursor.close()
        return userExists
    }

    //DEBUG
    fun countSuppliers() : Int {
        val db = readableDatabase
        val cursor = db.query("suppliers", null, null, null, null, null, null, null)
        val supps = cursor.count
        cursor.close()
        return supps
    }
    fun insertSuppliers() {
        val suppliers = listOf("Dallmayr", "Cafizzio", "NESCAFE", "Food&Drinks", "Office Things")
        for(s in suppliers) {
            val values = ContentValues().apply {
                put("name", s)
            }
            val db = writableDatabase

            db.insert("suppliers", null, values)
        }
    }
    fun insertProduct(name: String, s: Int, price: Int) {
        val values = ContentValues().apply {
            put("name", name)
            put("supplier", s)
            put("price", price)
        }
        val db = writableDatabase
        db.insert("products", null, values)
    }
    //----------------

    companion object {
        private const val DB_NAME = "appdata.db"
        private const val DB_VERSION = 14
    }
}