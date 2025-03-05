package com.example.pdm

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
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

        val products = databaseHelper.loadProducts(1)

        for (p in products) {
            val datum: MutableMap<String, String> = HashMap(2)
            datum["name"] = p.name
            datum["price"] = "Price: "+p.price.toString()+"RON"
            data.add(datum)
        }
        val smpAdapter = SimpleAdapter(
            this@CartActivity, data,
            R.layout.cart_item,
            arrayOf("name", "price"),
            intArrayOf(
                R.id.itemName,
                android.R.id.text2
            )
        )
        if(products.isNotEmpty()) {
            productsListView.adapter = smpAdapter
            productsListView.visibility = View.VISIBLE
            productsListView.setOnItemClickListener { pparent, view, pos, _ ->
                if(view.id == R.id.removeItems) {
                        val selectedItem = pparent.getItemAtPosition(pos)
                    }
                }
            }
        }

    }