package com.example.pdm

import android.os.Bundle

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pdm.MenuActivity.Companion.getItemName

class EditUserActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var searchView: SearchView
    private lateinit var accountsSearchLV: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        databaseHelper = DatabaseHelper(this)
        val usernameEditText = findViewById<TextView>(R.id.usernameEditText)
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val emailAddresEditView = findViewById<EditText>(R.id.emailAddresEditView)
        val userTypeTextView = findViewById<TextView>(R.id.userTypeTextView)
        val userTypeSpinner = findViewById<Spinner>(R.id.userTypeSpinner)
        val editUserSaveButton = findViewById<Button>(R.id.editUserSaveButton)

        usernameEditText.visibility = View.INVISIBLE
        emailTextView.visibility =  View.INVISIBLE
        emailAddresEditView.visibility = View.INVISIBLE
        userTypeTextView.visibility =  View.INVISIBLE
        userTypeSpinner.visibility =  View.INVISIBLE
        editUserSaveButton.visibility =  View.INVISIBLE
        accountsSearchLV = findViewById(R.id.accountSearchListView)
        searchView = findViewById(R.id.userSearchView)
        val createdUsersList = mutableListOf<String>()
        for (u in UserData.accountList) {
            createdUsersList.add(u.name)
        }
        val listAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            createdUsersList
        )
        accountsSearchLV.adapter = listAdapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                if (createdUsersList.contains(query)) {
                    // if query exist within list we
                    // are filtering our list adapter.
                    listAdapter.filter.filter(query)
                } else {
                    // if query is not present we are displaying
                    // a toast message as no  data found..
                    Toast.makeText(this@EditUserActivity, "No user found", Toast.LENGTH_LONG)
                        .show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                listAdapter.filter.filter(newText)
                return false
            }
        })
        accountsSearchLV.setOnItemClickListener { pparent, _, pos, _ ->
            val selectedItem = getItemName(pparent.getItemAtPosition(pos))
            val usr: UserData = UserData.retrieveUser(selectedItem) ?: return@setOnItemClickListener
            var itemPosition = usr.usertype
            usernameEditText.text = buildString {
                append("Username: ")
                append(usr.name)
            }
            emailAddresEditView.setText(usr.email)
            usernameEditText.visibility = View.VISIBLE
            emailTextView.visibility =  View.VISIBLE
            emailAddresEditView.visibility = View.VISIBLE
            userTypeTextView.visibility =  View.VISIBLE
            userTypeSpinner.visibility =  View.VISIBLE
            editUserSaveButton.visibility =  View.VISIBLE

            val userTypes = mutableListOf<String>()
            when (usr.usertype) {
                0 -> userTypes.add("Normal User")
                1 -> userTypes.add("Courier")
                2 -> userTypes.add("Professor")
                else -> userTypes.add("Administrator")
            }
            userTypes.add("Normal User")
            userTypes.add("Courier")
            userTypes.add("Professor")
            userTypes.add("Administrator")
            if (userTypeSpinner != null) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item, userTypes
                )
                userTypeSpinner.adapter = adapter

                userTypeSpinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        if(position > 0) itemPosition = position-1
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }

            editUserSaveButton.setOnClickListener {
                databaseHelper.updateUserData(usr.name, emailAddresEditView.text.toString(), itemPosition)
                usr.email = emailAddresEditView.text.toString()
                usr.usertype = itemPosition
                if(UserData.username == usr.name) {
                    UserData.usertype = itemPosition
                }
                finish()
            }
        }
    }
}