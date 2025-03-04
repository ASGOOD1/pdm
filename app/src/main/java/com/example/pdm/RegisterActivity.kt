package com.example.pdm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText


class RegisterActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textInputEditTextRegisterUsername: TextInputEditText = findViewById(R.id.textInputUsernameRegister)
        val textInputEditTextRegisterEmail: TextInputEditText = findViewById(R.id.textInputEmailRegister)
        val textInputEditTextRegisterPassword: TextInputEditText = findViewById(R.id.textInputPasswordRegister)
        val btnRegister: Button = findViewById(R.id.buttonRegister)
        textInputEditTextRegisterUsername.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                textInputEditTextRegisterEmail.requestFocus() // Move focus to the next field
                return@setOnEditorActionListener true
            }
            false
        }
        textInputEditTextRegisterEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                textInputEditTextRegisterPassword.requestFocus() // Move focus to the next field
                return@setOnEditorActionListener true
            }
            false
        }
        textInputEditTextRegisterPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                val view: View? = this.currentFocus
                if (view != null) {
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            false
        }
        btnRegister.setOnClickListener {
            val username: String = textInputEditTextRegisterUsername.text.toString()
            val password: String = textInputEditTextRegisterPassword.text.toString()
            val email: String = textInputEditTextRegisterEmail.text.toString()
            databaseHelper = DatabaseHelper(this);
            if(databaseHelper.userOrEmailExists(username, email)) {
                Toast.makeText(this, "Error! The username or email is already in use.", Toast.LENGTH_SHORT).show()
            }
            else {
                databaseHelper.insertAccount(username, email, password);
                Toast.makeText(this, "Account created succesfully.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent);
                finish()
            }
        }


    }
}