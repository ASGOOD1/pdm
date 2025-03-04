package com.example.pdm

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MenuActivity : AppCompatActivity() {
    private val cartItemsNr : Int = 0
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        databaseHelper = DatabaseHelper(this)
        val suppliers = databaseHelper.loadSuppliers()
        if(databaseHelper.countSuppliers() < 5) {
            databaseHelper.insertSuppliers()
            databaseHelper.loadSuppliers()
        }
        super.onCreate(null)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cartItemsText : EditText = findViewById(R.id.cartItems)
        val str = "$cartItemsNr items"
        cartItemsText.setText(str)
        val spinner = findViewById<Spinner>(R.id.spinner_suppliers)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, suppliers
            )

            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    if(id.toInt() != 0) {
                        val productsListView: ListView = findViewById(R.id.productsList)
                        val data: MutableList<Map<String, String>> = mutableListOf()

                        val products = databaseHelper.loadProducts(id)

                        for (p in products) {
                            val datum: MutableMap<String, String> = HashMap(2)
                            datum["name"] = p.name
                            datum["price"] = "Price: "+p.price.toString()+"RON"
                            data.add(datum)
                        }
                        val smpAdapter = SimpleAdapter(
                            this@MenuActivity, data,
                            android.R.layout.simple_list_item_2,
                            arrayOf("name", "price"),
                            intArrayOf(
                                android.R.id.text1,
                                android.R.id.text2
                            )
                        )
                        if(products.isNotEmpty()) {

                            productsListView.adapter = smpAdapter
                            productsListView.visibility = View.VISIBLE
                        }
                        else {
                            Toast.makeText(this@MenuActivity, "No products listed.", Toast.LENGTH_SHORT).show()
                            productsListView.visibility = View.INVISIBLE
                        }
                    }
                    else {
                        val productsListView: ListView = findViewById(R.id.productsList)
                        productsListView.visibility = View.INVISIBLE
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }

    }
}