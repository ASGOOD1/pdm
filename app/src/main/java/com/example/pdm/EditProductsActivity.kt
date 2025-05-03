/*
TODO:
    ADD PRODUCT ACTIVITY

 */

package com.example.pdm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class EditProductsActivity : AppCompatActivity() {
    companion object {
        var selectedProduct : Product? = null
        var productNameTextView : TextView? = null
        var productPriceTextView : TextView? = null
        var selectedSupplier: Int = 0
    }
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_products)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseHelper = DatabaseHelper(this)
        val suppliers = databaseHelper.loadSuppliers()
        val spinner = findViewById<Spinner>(R.id.editProductsSpinner)
        val editText1 = findViewById<EditText>(R.id.editProductsEditText)
        val editText2 = findViewById<EditText>(R.id.editProductsPriceText)
        val editButton = findViewById<Button>(R.id.editProductUpdateButton)
        val addProductButton = findViewById<FloatingActionButton>(R.id.editProductsAddFloatingButton)
        val addProductTextView = findViewById<TextView>(R.id.editProductsAddTextView)

        editText1.visibility = View.INVISIBLE
        editButton.visibility = View.INVISIBLE
        editText2.visibility = View.INVISIBLE
        addProductButton.visibility = View.INVISIBLE
        addProductTextView.visibility = View.INVISIBLE
        addProductButton.setOnClickListener {
            if(selectedSupplier != 0) {
                finish()
                val intent = Intent(this@EditProductsActivity, InsertProductActivity::class.java)
                startActivity(intent)
            }
        }
        editButton.setOnClickListener {
            if(selectedProduct != null) {
                databaseHelper.updateProduct(editText1.text.toString(), editText2.text.toString().toInt(), selectedProduct!!.id)
                selectedProduct!!.price = editText2.text.toString().toInt()
                selectedProduct!!.name = editText1.text.toString()
                Toast.makeText(this, "Product data updated succesfully!", Toast.LENGTH_SHORT).show()
                productNameTextView!!.text = selectedProduct!!.name
                productPriceTextView!!.text = "Price: " + selectedProduct!!.price + "RON"
            }
        }
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
                        addProductButton.visibility = View.VISIBLE
                        addProductTextView.visibility = View.VISIBLE
                        selectedSupplier = id.toInt()
                        val productsListView: ListView = findViewById(R.id.editProductsListView)
                        val data: MutableList<Map<String, String>> = mutableListOf()

                        val products = databaseHelper.loadProducts(id)

                        for (p in products) {
                            val datum: MutableMap<String, String> = HashMap(2)
                            datum["name"] = p.name
                            datum["price"] = "Price: "+p.price.toString()+"RON"
                            datum["supplier"] = id.toString()
                            data.add(datum)
                        }
                        val smpAdapter = AdapterExtendedEditProducts(
                            this@EditProductsActivity, data,
                            R.layout.edit_products_item,
                            arrayOf("name", "price"),
                            intArrayOf(
                                R.id.epiProductNameText,
                                R.id.epiProductPrice
                            ),findViewById(R.id.editProductsEditText)
                            , findViewById(R.id.editProductsPriceText)
                            , findViewById(R.id.editProductUpdateButton)
                        )
                        addProductButton.visibility = View.VISIBLE
                        if(products.isNotEmpty()) {
                            productsListView.adapter = smpAdapter
                            productsListView.visibility = View.VISIBLE
                        }
                        else {
                            Toast.makeText(this@EditProductsActivity, "No products listed.", Toast.LENGTH_SHORT).show()
                            productsListView.visibility = View.INVISIBLE
                        }

                        editText1.visibility = View.INVISIBLE
                        editButton.visibility = View.INVISIBLE
                        editText2.visibility = View.INVISIBLE
                    }
                    else {
                        val productsListView: ListView = findViewById(R.id.editProductsListView)
                        productsListView.visibility = View.INVISIBLE

                        editText1.visibility = View.INVISIBLE
                        editButton.visibility = View.INVISIBLE
                        editText2.visibility = View.INVISIBLE
                        addProductButton.visibility = View.INVISIBLE
                        addProductTextView.visibility = View.INVISIBLE

                    }


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }
}