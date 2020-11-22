package com.home.arcnotes

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.home.arcnotes.provider.BackupProvider
import com.home.arcnotes.provider.payload.response.RestoreResponse
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    val backupProvider: BackupProvider = BackupProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<MaterialButton>(R.id.jsonButton).setOnClickListener { _ ->
            grabJson()
        }
    }

    private fun grabJson() {
        doAsync {
            val restoreResp: RestoreResponse? = backupProvider.restore()

            uiThread {
                if (restoreResp != null) {
                    findViewById<TextView>(R.id.jsonText).text = restoreResp.toString()
                } else {
                    toast("Cannot restore sry :/")
                }
            }
        }
    }
}