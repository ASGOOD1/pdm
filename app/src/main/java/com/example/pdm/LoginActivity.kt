package com.example.pdm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging


data class UserData(private val context: Context) {
    companion object {
        var userid = 0
        var usertype = 0
    }
}
class LoginActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textInputEditTextUsername: TextInputEditText = findViewById(R.id.textInputUsername)
        val textInputEditTextPassword: TextInputEditText = findViewById(R.id.textInputPassword)
        val btnLogin: Button = findViewById(R.id.loginAction)
        textInputEditTextUsername.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                textInputEditTextPassword.requestFocus() // Move focus to the next field
                return@setOnEditorActionListener true
            }
            false
        }
        textInputEditTextPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                val view: View? = this.currentFocus
                if (view != null) {
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
            false
        }
        btnLogin.setOnClickListener {
            val username: String = textInputEditTextUsername.text.toString()
            val password: String = textInputEditTextPassword.text.toString()
            databaseHelper = DatabaseHelper(this)
            databaseHelper.loadCommands()
            databaseHelper.loadAllProducts()

            if(databaseHelper.loginAccountExists(username, password)) {
                UserData.userid = databaseHelper.retrieveID(username, password)
                UserData.usertype = 0
                Toast.makeText(this, "You succesfully logged in.", Toast.LENGTH_SHORT).show()
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result != null && !TextUtils.isEmpty(task.result)) {
                                val token: String = task.result!!

                                databaseHelper.updateAccount(token, username)

                            }
                        }
                    }
                if(UserData.usertype == 0 || UserData.usertype == 2) {
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                }
                else if(UserData.usertype == 1) {
                    val intent = Intent(this, CourierMenuActivity::class.java)
                    startActivity(intent)

                }

                finish()
            }
            else {
                Toast.makeText(this, "Wrong login credentials.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}