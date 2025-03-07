package com.example.pdm

import android.content.Intent
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
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MenuActivity : AppCompatActivity() {
    private var cartItemsNr : Int = 0
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
        cartItemsNr = cartFull.countItems()
        var str = "$cartItemsNr items"
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
                            productsListView.setOnItemClickListener { pparent, _, pos, _ ->
                                val selectedItem = getItemName(pparent.getItemAtPosition(pos))
                                Toast.makeText(this@MenuActivity, "Ai selectat $selectedItem", Toast.LENGTH_SHORT).show()
                                var foundp : Product? = null
                                for(p in products) {
                                    if(selectedItem == p.name && p.supplier == id.toInt()) {
                                        foundp = p
                                        break
                                    }
                                }
                                if(foundp != null) {
                                    if (cartFull.isProductInList(foundp)) {
                                        val cartItem: Cartitem? = cartFull.findItemCartID(foundp)
                                        if(cartItem != null) {
                                            cartItem.qty++
                                            cartItem.price = cartItem.qty * foundp.price
                                        }
                                    }
                                    else {
                                        cartFull.addItemToCart(Cartitem(foundp, 1, foundp.price))
                                    }
                                    cartItemsNr = cartFull.countItems()
                                    str = "$cartItemsNr items"
                                    cartItemsText.setText(str)
                                }
                            }
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
                    val btnGoToCart: FloatingActionButton = findViewById(R.id.goToCartBtn)
                    btnGoToCart.setOnClickListener {
                        val intent = Intent(this@MenuActivity, CartActivity::class.java)

                        startActivity(intent)
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }

    }
    companion object {
        fun getItemName(i : Any?) : String {
            return i.toString().substringAfter("name=").substringBefore(',')
        }
    }
}