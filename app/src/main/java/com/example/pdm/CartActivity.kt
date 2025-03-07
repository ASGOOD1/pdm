package com.example.pdm

import android.os.Bundle
import android.view.View
import android.widget.ListView
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


        for (p in cartFull.cartItemsList) {
            val datum: MutableMap<String, String> = HashMap(2)
            datum["name"] = p.product.name
            datum["price"] = "Price: " + p.price.toString() + "RON"
            datum["qtty"] = p.qty.toString()
            datum["id"] = p.product.toString()
            data.add(datum)
        }
        val smpAdapter = AdapterExtended(
            this@CartActivity, data,
            R.layout.cart_item,
            arrayOf("name", "price", "qtty"),
            intArrayOf(
                R.id.itemName,
                android.R.id.text2,
                R.id.changeItemsNumber,
            )
        )
        if (cartFull.cartItemsList.isNotEmpty()) {
            productsListView.adapter = smpAdapter
            productsListView.visibility = View.VISIBLE



        }

    }
}