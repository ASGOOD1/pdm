package com.example.pdm

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.SimpleAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AdapterExtendedActiveOrders(
    private val context: Context,
    private val data: List<Map<String, String>>,
    resource: Int,
    from: Array<String>,
    to: IntArray
) : SimpleAdapter(context, data, resource, from, to) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)
        val databaseHelper = DatabaseHelper(context)
        // Get the button reference
        val acceptOrder = view.findViewById<ImageButton>(R.id.activeOrderAccept)
        val cmd = data[position]["id"]
        // Set click listener
        acceptOrder.setOnClickListener {
            if(data[position]["type"] == "1" && cmd != null) {
                Commands.getCommandID(cmd.toInt())?.courierid = UserData.userid
                databaseHelper.updateCommandCourier(cmd.toInt(), UserData.userid)

            }
            else {
                if (cmd != null) {
                    val cmdTokenClient = Commands.getCommandID(cmd.toInt())?.clientid
                    Commands.commandsList.remove(Commands.getCommandID(cmd.toInt()))
                    databaseHelper.removeCommand(cmd.toInt())
                    var token = ""
                    if(cmdTokenClient != null)
                        token = databaseHelper.retrieveClientToken(cmdTokenClient)
                    if(token != "") {
                        CoroutineScope(Dispatchers.IO).launch {
                            FCMHelper.sendPing(token, "UD Courier", "Your university courier arrived at your location.")
                        }
                    }
                }
            }
            (context as Activity).recreate()

        }
        val c : Commands? = data[position]["id"]?.let { Commands.getCommandID(it.toInt()) }
        val acceptOrderDropdown = view.findViewById<Spinner>(R.id.orderDetailsDropdown)
        if(acceptOrderDropdown != null) {

            val suppliers = databaseHelper.loadSuppliers()
            val productslist = mutableListOf<String>()
            productslist.add("Order details")
            if (c != null) {
                for(p in c.getCommandsProducts()){
                    productslist.add("(" + suppliers.elementAt(Product.getSupplierID(p.prodid)) + ")"+p.number.toString() + "x" + Product.getProductNameFromID(p.prodid))
                }

            }
            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                productslist
            )

            acceptOrderDropdown.adapter = adapter
        }


        return view
    }
}