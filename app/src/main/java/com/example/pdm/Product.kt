package com.example.pdm
data class Product(val id: Int, val supplier: Int, val name: String, val price: Int) {
    companion object {
        private val productsList = mutableListOf<Product>()
        fun addProduct(p: Product) {
            productsList.add(p)
        }
        fun getProdList(): MutableList<Product> {
            return productsList
        }
        fun getProductNameFromID(id: Int): String {
            var name = "not defined"
            for (p in productsList) {
                if (id == p.id) name = p.name
            }
            return name
        }
        fun getSupplierID(id: Int): Int {
            var name = 0

            for (p in productsList) {
                if (id == p.id) {
                    name = p.supplier
                }
            }
            return name
        }

    }
}