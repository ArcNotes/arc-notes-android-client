package com.arcnotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arcnotes.provider.payload.model.NoteModel

class NotesRecyclerAdapter (
    private var notes: List<NoteModel>
) : RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val noteTitle: TextView = itemView.findViewById(R.id.tv_noteTitle)
        val notePreview: TextView = itemView.findViewById(R.id.tv_notePreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.note_layout, parent, false)
        val holder = ViewHolder(itemView)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.noteTitle.text = notes[position].title
        holder.notePreview.text = notes[position].text
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}