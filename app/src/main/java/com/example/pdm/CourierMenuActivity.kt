package com.example.pdm

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CourierMenuActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_courier_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseHelper = DatabaseHelper(this)
        val activeOrdersListView: ListView = findViewById(R.id.activeOrdersListView)
        val data: MutableList<Map<String, String>> = mutableListOf()
        var activeCommands = 0
        for (c in Commands.commandsList) {
            if(c.courierid == 0) {
                val datum: MutableMap<String, String> = HashMap(2)
                datum["room"] = "Room:" + c.room
                datum["id"] = c.commandid.toString()


                data.add(datum)
                activeCommands ++
            }

        }
        val smpAdapter = AdapterExtendedActiveOrders(
            this@CourierMenuActivity, data,
            R.layout.active_orders_item,
            arrayOf("room"),
            intArrayOf(
                R.id.orderRoom
            )
        )
        if( activeCommands != 0 ) {
            activeOrdersListView.adapter = smpAdapter
            activeOrdersListView.visibility = View.VISIBLE
        }
        else {
            activeOrdersListView.visibility = View.INVISIBLE
        }
    }
}