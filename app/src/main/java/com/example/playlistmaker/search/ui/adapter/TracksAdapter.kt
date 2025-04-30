package com.example.playlistmaker.search.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track


class TracksAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<TracksViewHolder>() {

    var trackList: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        return TracksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            onClickListener.onClick(trackList[position])
        }
    }

    override fun getItemCount(): Int = trackList.size

    fun interface OnClickListener {
        fun onClick(track: Track)
    }
}