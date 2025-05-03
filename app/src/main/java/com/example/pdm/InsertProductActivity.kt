package com.example.pdm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pdm.EditProductsActivity.Companion.selectedSupplier
import com.example.pdm.Product.Companion.addProduct
import com.example.pdm.Product.Companion.getProdList

class InsertProductActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_insert_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val supplierText = findViewById<EditText>(R.id.ipEditSupplierAuto)
        val nameText = findViewById<EditText>(R.id.ipNameEditText)
        val priceText = findViewById<EditText>(R.id.ipPriceEditText)
        val buttonInsertProduct = findViewById<Button>(R.id.ipInsertButton)
        databaseHelper = DatabaseHelper(this)
        val suppliers = databaseHelper.loadSuppliers()
        supplierText.setText(suppliers[selectedSupplier].toString())
        buttonInsertProduct.setOnClickListener {
            if(nameText.text.isEmpty() || priceText.text.isEmpty()) {
                Toast.makeText(this, "Invalid product name or price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val prod = getProdList().find { it.name == nameText.text.toString() && it.supplier == selectedSupplier }
            if(prod != null) {
                Toast.makeText(this, "This supplier already has this product", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            val id = databaseHelper.insertProduct(nameText.text.toString(), selectedSupplier, priceText.text.toString().toInt())
            addProduct(Product(id, selectedSupplier, nameText.text.toString(), priceText.text.toString().toInt()))
            Toast.makeText(this, "You succesfully added a new product.", Toast.LENGTH_SHORT).show()
            finish()
            val intent = Intent(this@InsertProductActivity, EditProductsActivity::class.java)
            startActivity(intent);
        }
    }
}