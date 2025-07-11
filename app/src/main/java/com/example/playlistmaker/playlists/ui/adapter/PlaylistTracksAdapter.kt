package com.example.playlistmaker.playlists.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track

class PlaylistTracksAdapter() :
    RecyclerView.Adapter<PlaylistTracksViewHolder>() {

    private var onClickListener: OnClickListener? = null

    var trackList: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTracksViewHolder {
        return PlaylistTracksViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlaylistTracksViewHolder, position: Int) {
        holder.bind(trackList[position])

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(trackList[position])
        }
        holder.itemView.setOnLongClickListener {
            onClickListener?.onLongClick(trackList[position])
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = trackList.size

    interface OnClickListener {
        fun onClick(track: Track)
        fun onLongClick(track: Track)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }
}