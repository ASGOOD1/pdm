package com.example.pdm

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CartActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseHelper = DatabaseHelper(this)

        val productsListView: ListView = findViewById(R.id.cartListView)
        val data: MutableList<Map<String, String>> = mutableListOf()

        var totalPricePaid = 0
        var totalTax = cartFull.countItems()

        for (p in cartFull.cartItemsList) {
            val datum: MutableMap<String, String> = HashMap(2)
            datum["name"] = p.product.name
            datum["price"] = "Price: " + p.price.toString() + "RON"
            datum["qtty"] = p.qty.toString()
            datum["id"] = p.product.toString()
            totalPricePaid += p.price

            data.add(datum)
        }
        totalPricePaid+=totalTax
        val smpAdapter = AdapterExtended(
            this@CartActivity, data,
            R.layout.cart_item,
            arrayOf("name", "price", "qtty"),
            intArrayOf(
                R.id.itemName, R.id.itemsPrice,
                R.id.changeItemsNumber,
            )
        )
        val totalPriceText : TextView = findViewById(R.id.cartItemsTotalPrice)
        val totalPriceStr: String = "Total: " + totalPricePaid.toString()+"RON"
        totalPriceText.setText(totalPriceStr)
        if (cartFull.cartItemsList.isNotEmpty()) {
            productsListView.adapter =  smpAdapter
            productsListView.visibility = View.VISIBLE


        }

    }

}