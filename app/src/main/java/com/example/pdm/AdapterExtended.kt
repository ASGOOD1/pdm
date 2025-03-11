package com.example.pdm

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SimpleAdapter


class AdapterExtended(
    private val context: Context,
    private val data: List<Map<String, String>>,
    resource: Int,
    from: Array<String>,
    to: IntArray
) : SimpleAdapter(context, data, resource, from, to) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent)

        // Get the button reference
        val removeButton = view.findViewById<ImageButton>(R.id.removeItems)

        // Set click listener
        removeButton.setOnClickListener {
            data[position]["id"]?.let { it1 ->
                CartFull.getCartID(it1)
                    ?.let { it2 -> CartFull.removeItemFromCart(it2) }
            }
            (context as Activity).recreate()

        }


        return view
    }
}