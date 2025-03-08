package com.example.pdm

import android.content.Context
import java.io.Serializable

data class Cartitem(val product: Product, var qty: Int, var price: Int) : Serializable

data class cartFull(private val context: Context) : Serializable {
    companion object {
        var cartItemsList = mutableListOf<Cartitem>()
        fun isProductInList(p: Any) : Boolean {
            for(c in cartItemsList) {
                if(c.product == p) return true
            }
            return false
        }
        fun findItemCartID(p: Product) : Cartitem? {
            for(c in cartItemsList) {
                if(c.product == p) return c
            }
            return null
        }
        fun addItemToCart(c: Cartitem) {
            cartItemsList.add(c)
        }
        fun removeItemFromCart(c: Cartitem) {
            cartItemsList.remove(c)
        }
        fun countItems() : Int {
            var totalItems: Int = 0
            for(c in cartItemsList) {
                if(c.qty > 0) totalItems += c.qty
            }

            return totalItems
        }
        fun getCartID(s: String) : Cartitem? {
            for(p in cartItemsList) {
                if(p.product.toString() == s)
                    return p
            }
            return null
        }
    }
}
