package com.example.pdm

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pdm.CourierMenuActivity

class ViewOrders : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_orders)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseHelper = DatabaseHelper(this)
        val activeOrdersListView: ListView = findViewById(R.id.adminOrdersListView)
        val data: MutableList<Map<String, String>> = mutableListOf()
        var activeCommands = 0
        for (c in Commands.commandsList) {

            val datum: MutableMap<String, String> = HashMap(2)
            datum["room"] = "Room:" + c.room
            datum["id"] = c.commandid.toString()
            datum["cmdid"] = "Command #" + c.commandid.toString()
            if(c.courierid == 0)
                datum["courier"] = "Courier: None"

            else
                datum["courier"] = "Courier: " + databaseHelper.retrieveName(c.courierid)


            data.add(datum)
            activeCommands ++

        }
        val smpAdapter = AdapterExtendedAdminOrderss(
            this@ViewOrders, data,
            R.layout.admin_order_item,
            arrayOf("room", "cmdid", "courier"),
            intArrayOf(
                R.id.orderRoom,
                R.id.textViewCommandID,
                R.id.textViewCourier
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