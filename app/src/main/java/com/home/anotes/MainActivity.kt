package com.home.anotes

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.home.anotes.provider.BackupProvider
import com.home.anotes.provider.payload.response.RestoreResponse
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
                    findViewById<TextView>(R.id.jsonText).text = restoreResp.notes.toString()
                } else {
                    toast("Cannot restore sry :/")
                }
            }
        }
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