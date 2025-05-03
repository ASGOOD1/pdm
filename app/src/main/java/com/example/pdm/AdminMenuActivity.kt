package com.example.pdm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnEditUser = findViewById<Button>(R.id.editUserButton)
        btnEditUser.setOnClickListener {
            val intent = Intent(this, EditUserActivity::class.java)
            startActivity(intent)
        }
        val btnViewOrders = findViewById<Button>(R.id.buttonViewCommands)
        btnViewOrders.setOnClickListener {
            val intent = Intent(this, ViewOrders::class.java)
            startActivity(intent)
        }
        val btnEditProductsAdmin = findViewById<Button>(R.id.buttonEditProducts)
        btnEditProductsAdmin.setOnClickListener {
            val intent = Intent(this, EditProductsActivity::class.java)
            startActivity(intent)
        }
    }
}