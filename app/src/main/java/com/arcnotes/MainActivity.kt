package com.arcnotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arcnotes.provider.BackupProvider
import com.arcnotes.provider.payload.model.NoteModel
import com.arcnotes.provider.payload.response.RestoreResponse
import com.google.android.material.button.MaterialButton
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    val backupProvider: BackupProvider = BackupProvider()

    var notes: ArrayList<NoteModel> = ArrayList()
    val notesAdapter: NotesRecyclerAdapter = NotesRecyclerAdapter(this.notes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        val rvNotesList = findViewById<RecyclerView>(R.id.rv_notesList)
        rvNotesList.layoutManager = LinearLayoutManager(this)
        rvNotesList.adapter = notesAdapter

        findViewById<MaterialButton>(R.id.jsonButton).setOnClickListener { _ ->
            refreshNotesList()
        }
    }

    private fun refreshNotesList() {
        doAsync {
            // Get notes
            val restoreResp: RestoreResponse? = backupProvider.restore()

            // Refresh if data exists
            if (restoreResp != null) {
                val notesToShow = restoreResp.notes
                    .mapIndexed { idx, note -> note.copy(title = "${idx + 1}. ${note.title}") }

                notes.clear()
                notes.addAll(notesToShow)

                uiThread {
                    notesAdapter.notifyDataSetChanged()
                }
            } else {
                toast("Cannot refresh sry :/")
            }
        }
    }
}