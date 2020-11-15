package com.home.anotes

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import okhttp3.*
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    val i = AtomicInteger()
    val okHttpClient: OkHttpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<MaterialButton>(R.id.jsonButton).setOnClickListener { _ ->
            grabJson()
        }
    }

    fun grabJson() {
        val url = "http://192.168.0.104:8080/api/restore"
        val fknToken = "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYwMzc0NjM1OSwiZXhwIjoxNjA2MTY1NTU5fQ.4saTXudbTFPsMHKx7pNvMsnBs-j0zuU7_DvAzjUGYE_CKgYRkFj9502u8zlfcBZh"

        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", fknToken)
            .url(url)
            .build()

        okHttpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("okHttp", "Fuck you, API", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        Log.i("okHttp", "Received json: $json")
                        runOnUiThread {
                            findViewById<TextView>(R.id.jsonText).setText(json)
                        }
                    }
                }
            });

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}