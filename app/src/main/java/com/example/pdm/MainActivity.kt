package com.example.pdm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.auth.oauth2.GoogleCredentials
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.FileInputStream
import java.nio.file.Paths
object FCMHelper {
    private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/pdmfireb/messages:send"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAccessToken(): String {
        val credentials = GoogleCredentials.fromStream(FileInputStream("/data/data/com.example.pdm/files/pdmfireb-firebase-adminsdk-fbsvc-d782dd9456.json"))
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        credentials.refreshIfExpired()
        return credentials.accessToken.tokenValue
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendPing(receiverToken: String, title: String, message: String) {
        val client = OkHttpClient()
        val accessToken = getAccessToken()

        val json = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", receiverToken)
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", message)
                })
            })
        }

        val body = json.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(FCM_URL)
            .post(body)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                println("FCM Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                println("FCM Response: ${response.body?.string()}")
            }
        })
    }
}
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        val btn_login_activity: Button = findViewById(R.id.button3)
        btn_login_activity.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val btn_register_activity: Button = findViewById(R.id.button4)
        btn_register_activity.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}