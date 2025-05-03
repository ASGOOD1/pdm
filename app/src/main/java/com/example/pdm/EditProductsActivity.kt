/*TODO: Edit products
    - Spinner with suppliers and listview with products
    - Product data underneath (can be updated or deleted by pressing a specific button on the list view)
    - Can be completed directly and then saved by pressing save(Update existing)
    - Product can be added by pressing + floating button(new)


 */

package com.example.pdm

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditProductsActivity : AppCompatActivity() {
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

        editText1.visibility = View.INVISIBLE
        editButton.visibility = View.INVISIBLE
        editText2.visibility = View.INVISIBLE
        addProductButton.visibility = View.VISIBLE
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

                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }
}