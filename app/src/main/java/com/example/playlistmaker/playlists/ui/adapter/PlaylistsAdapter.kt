package com.example.playlistmaker.playlists.ui.adapter


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlists.domain.model.Playlist

class PlaylistsAdapter(
    //private val onClickListener: OnClickListener
) : RecyclerView.Adapter<PlaylistsViewHolder>() {

    var playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {

        return PlaylistsViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
//        holder.itemView.setOnClickListener {
//            onClickListener.onClick(playlists[position])
//        }
    }

    fun interface OnClickListener {
        fun onClick(playlist: Playlist)
    }
}