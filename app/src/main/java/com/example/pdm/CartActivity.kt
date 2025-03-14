package com.example.pdm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        val totalTax = CartFull.countItems()

        for (p in CartFull.cartItemsList) {
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
        val btnSendCommand: FloatingActionButton = findViewById(R.id.sendOrder)
        val totalPriceText : TextView = findViewById(R.id.cartItemsTotalPrice)
        val totalPriceStr: String = "Total: " + totalPricePaid.toString()+"RON"
        totalPriceText.text = totalPriceStr
        if (CartFull.cartItemsList.isNotEmpty()) {
            productsListView.adapter =  smpAdapter
            productsListView.visibility = View.VISIBLE
            btnSendCommand.show()
            for (p in CartFull.cartItemsList) {
                CartFull.cartItemsList.remove(p)
            }
            btnSendCommand.setOnClickListener {
                val editTextRoom: EditText = findViewById(R.id.RoomNumber)
                if(editTextRoom.length() < 2 || editTextRoom.length() > 4) {
                    Toast.makeText(this, "Invalid room number.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if(!checkRoom(editTextRoom.text.toString())) {
                    Toast.makeText(this, "Invalid room number.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Your order has been placed sucesfully.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }


        }
        else btnSendCommand.hide()

    }
    companion object {
        fun checkRoom(s: String) : Boolean {
            if(s != "AIM" && s != "AM" && s[0] != 'A' && s[0] != 'F' && (s[0] < '0' || s[0] > '9'))
                return false
            else if(s.length > 2 && (s[1] < '0' || s[1] > '9')) return false
            else if(s.length > 3 && (s[2] < '0' || s[2] > '9')) return false
            if(s[0] == 'F' && s.length<4) return false
            else if(s[0] == 'A' && s.length<3) return false

            return true
        }
    }
}