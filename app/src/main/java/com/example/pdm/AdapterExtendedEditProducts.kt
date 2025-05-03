package com.example.pdm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.pdm.EditProductsActivity.Companion.productNameTextView
import com.example.pdm.EditProductsActivity.Companion.productPriceTextView
import com.example.pdm.EditProductsActivity.Companion.selectedProduct
import com.example.pdm.Product.Companion.getProdList


class AdapterExtendedEditProducts(
    private val context: Context,
    private val data: MutableList<Map<String, String>>,
    resource: Int,
    from: Array<String>,
    to: IntArray,
    private val editNameText: EditText,
    private val editPriceText: EditText,
    private val editButton: Button
) : SimpleAdapter(context, data, resource, from, to) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)
        val databaseHelper = DatabaseHelper((context as Activity))
        // Get the button reference
        val configButton = view.findViewById<ImageButton>(R.id.epiEditProductButton)
        val deleteButton = view.findViewById<ImageButton>(R.id.epiDeleteProductButton)

        configButton.setOnClickListener {
            var prod : Product? = null
            for(p in getProdList()) {
                if(p.name == data[position]["name"] && p.supplier == data[position]["supplier"]?.toInt()) {
                    prod = p
                    break
                }
            }

            if(prod == null) {
                return@setOnClickListener
            }
            else{

                editNameText.setText(prod.name)
                editPriceText.setText(prod.price.toString())
                selectedProduct = prod
                productNameTextView = view.findViewById<TextView>(R.id.epiProductNameText)
                productPriceTextView = view.findViewById<TextView>(R.id.epiProductPrice)

                editNameText.visibility = View.VISIBLE
                editButton.visibility = View.VISIBLE
                editPriceText.visibility = View.VISIBLE
            }

        }
        deleteButton.setOnClickListener {
            val prod = getProdList().find { it.name == data[position]["name"] && it.supplier == data[position]["supplier"]?.toInt() }

            prod?.let{
                databaseHelper.removeProduct(prod.id)
                getProdList().remove(prod)
                data.removeAt(position)
                notifyDataSetChanged()
            }
        }



        return view
    }
}