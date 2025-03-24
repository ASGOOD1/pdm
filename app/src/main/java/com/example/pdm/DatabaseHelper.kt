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

        val createCommandsQuery = ("CREATE TABLE IF NOT EXISTS commands (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ClientID INTEGER DEFAULT 0," +
                "CourierID INTEGER DEFAULT 0," +
                "TotalPrice INTEGER DEFAULT 0," +
                "Room TEXT)")
        db?.execSQL(createCommandsQuery)

        val createCommandsProductsQuery = ("CREATE TABLE IF NOT EXISTS commands_products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ProductID INTEGER DEFAULT 0," +
                "NumberOfProducts INTEGER DEFAULT 0," +
                "CommandID INTEGER DEFAULT 0)")
        db?.execSQL(createCommandsProductsQuery)


    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        onCreate(db)

    }
    fun insertCommand(clientid: Int, courierid: Int, price: Int, room: String, prodList: MutableList<Cartitem>) {
        val values = ContentValues().apply {
            put("ClientID", clientid)
            put("CourierID", courierid)
            put("TotalPrice", price)
            put("Room", room)
        }
        val db = writableDatabase
        val id : Long =  db.insert("commands", null, values)
        val c = Commands(id.toInt(), clientid, courierid, price, room)
        Commands.insertCommand(c)
        for(p in prodList) {
            c.getCommandsProducts().add(ProductCommand(p.product.id, p.qty, id.toInt()))
            insertCommandProduct(p.product.id, p.qty, id.toInt())
        }
    }
    private fun insertCommandProduct(prodid: Int, number: Int, cmd: Int) {
        val values = ContentValues().apply {
            put("ProductID", prodid)
            put("NumberOfProducts", number)
            put("CommandID", cmd)
        }
        val db = writableDatabase
        db.insert("commands_products", null, values)
    }
    fun loadCommands() {
        val db = readableDatabase
        val cursor =  db.query("commands", null, "ID > ?", arrayOf("0"), null, null, null)
        while (cursor.moveToNext()) {
            Commands.insertCommand(Commands(cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("ClientID")),
                cursor.getInt(cursor.getColumnIndexOrThrow("CourierID")),
                cursor.getInt(cursor.getColumnIndexOrThrow("TotalPrice")),
                cursor.getString(cursor.getColumnIndexOrThrow("Room"))))
        }
        cursor.close()
        for(c in Commands.commandsList) {
            loadCommandProducts(c)
        }
    }
    private fun loadCommandProducts(c: Commands) {
        val db = readableDatabase
        val selection = "CommandID = ?"
        val selectionArgs = arrayOf(c.commandid.toString())
        val cursor = db.query("commands_products", null, selection, selectionArgs, null, null, null)
        while(cursor.moveToNext()) {
            c.insertCommandProduct(
                ProductCommand(
                    cursor.getInt(cursor.getColumnIndexOrThrow("ProductID")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("NumberOfProducts")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("CommandID"))
                )
            )
        }

        cursor.close()
    }

    fun loadAllProducts() {
        val db = readableDatabase
        val cursor = db.query("products", null, null, null, null, null, null)
        while(cursor.moveToNext()) {
            Product.addProduct(Product(cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("supplier")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getInt(cursor.getColumnIndexOrThrow("price"))))
        }
        cursor.close()
    }
    fun loadAllUserdata() {
        val db = readableDatabase
        val cursor = db.query("accounts", null, null, null, null, null, null)
        while(cursor.moveToNext()) {
            UserData.accountList.add(UserData(
                cursor.getString(cursor.getColumnIndexOrThrow("user")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getInt(cursor.getColumnIndexOrThrow("status"))
            ))
        }
        cursor.close()
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

    fun updateCommandCourier(cmd: Int, cour: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("CourierID", cour)
        }
        db.update("commands", values, "id=?", arrayOf(cmd.toString()))
    }
    fun removeCommand(cmd: Int) {
        val db = writableDatabase
        db.delete("commands", "id=?", arrayOf(cmd.toString()))
        db.delete("commands_products", "CommandID=?", arrayOf(cmd.toString()))
        Commands.getCommandID(cmd)?.getCommandsProducts()?.clear()
    }

    fun updateAccount(token: String, user: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("token", token)
        }
        db.update("accounts", values, "user=?", arrayOf(user))
    }

    fun updateUserData(user: String, email: String, type: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("status", type)
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

        val userExists = cursor.count>0

        cursor.close()
        return userExists
    }
    fun retrieveID(user: String, pwd: String) : Int {
        val db = readableDatabase
        val selection = "user = ? AND password = ?"
        val selectionArgs = arrayOf(user, pwd)
        val cursor = db.query("accounts", arrayOf("id"), selection, selectionArgs, null, null, null, null)
        cursor.moveToNext()
        val userExists = cursor.getInt(0)

        cursor.close()
        return userExists
    }
    fun retrieveClientToken(user: Int) : String {
        val db = readableDatabase
        val selection = "id=?"
        val selectionArgs = arrayOf(user.toString())
        val cursor = db.query("accounts", arrayOf("token"), selection, selectionArgs, null, null, null, null)
        cursor.moveToNext()
        val userExists = cursor.getString(0)

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
        val suppliers = listOf("Dallmayr", "Cafizzio", "NESCAFE", "Food&Drinks", "Office Things", "Professor")
        for(s in suppliers) {
            val values = ContentValues().apply {
                put("name", s)
            }
            val db = writableDatabase

            db.insert("suppliers", null, values)
        }
    }

    fun insertSupplier(name: String) {
        val values = ContentValues().apply {
            put("name", name)
        }
        val db = writableDatabase

        db.insert("suppliers", null, values)
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
        private const val DB_VERSION = 17
    }
}