package com.example.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(private var data: List<Track>) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(data[position])

        holder.itemView.setOnClickListener {
            SearchHistory().add(
                data[position],
                holder.itemView.context.getSharedPreferences(PLAYLIST_PREF, MODE_PRIVATE)
            )
        }
    }

    override fun getItemCount(): Int = data.size
}