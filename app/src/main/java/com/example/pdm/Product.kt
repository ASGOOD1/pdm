package com.example.pdm
data class Product(val id: Int, val supplier: Int, val name: String, val price: Int) {
    companion object {
        private val productsList = mutableListOf<Product>()
        fun addProduct(p: Product) {
            productsList.add(p)
        }
    }
}