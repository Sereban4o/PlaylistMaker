package com.example.playlistmaker.favorites.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.Track

class FavoritesAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<FavoritesViewHolder>() {

    var trackList: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(parent)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
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