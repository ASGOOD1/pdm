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
        if (data[position]["type"] == "1") {
            acceptOrder.setImageResource(R.drawable.free30x30_confirm_order_icon_download_in_svg_png_gif_file_formats_approve_placed_final_food_services_pack)
        }
        else acceptOrder.setImageResource(R.drawable.package_order_ready_checkmark_512)
        val cmd = data[position]["id"]
        // Set click listener
        acceptOrder.setOnClickListener {
            if(data[position]["type"] == "1" && cmd != null) {
                Commands.getCommandID(cmd.toInt())?.courierid = UserData.userid
                databaseHelper.updateCommandCourier(cmd.toInt(), UserData.userid)

            }
            else {
                if (cmd != null) {
                    if (Commands.getCommandID(cmd.toInt()) in Commands.commandsList) Commands.commandsList.remove(
                        Commands.getCommandID(cmd.toInt())
                    )
                    databaseHelper.removeCommand(cmd.toInt())
                }
            }
            (context as Activity).recreate()

        }
        val c : Commands? = Commands.getCommandID(data[position]["id"]!!.toInt())
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