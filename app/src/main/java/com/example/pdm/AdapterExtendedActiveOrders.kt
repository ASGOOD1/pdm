package com.example.pdm

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.SimpleAdapter
import android.widget.Spinner


class AdapterExtendedActiveOrders(
    private val context: Context,
    private val data: List<Map<String, String>>,
    resource: Int,
    from: Array<String>,
    to: IntArray
) : SimpleAdapter(context, data, resource, from, to) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)
        val databaseHelper = DatabaseHelper(context)
        // Get the button reference
        val acceptOrder = view.findViewById<ImageButton>(R.id.activeOrderAccept)
        val cmd = data[position]["id"]
        // Set click listener
        acceptOrder.setOnClickListener {
            if (cmd != null) {
                Commands.getCommandID(cmd.toInt())?.courierid = UserData.userid
                databaseHelper.updateCommandCourier(cmd.toInt(), UserData.userid)

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