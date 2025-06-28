package com.example.playlistmaker.playlists.ui.adapter

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.model.Playlist

class PlaylistsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.fragmet_playlist_view, parent, false)
) {

    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImage)
    private val countTracks: TextView = itemView.findViewById(R.id.countTracks)
    private val metrics = parent.context.resources
    private val context = parent.context


    @SuppressLint("SetTextI18n")
    fun bind(playlist: Playlist) {
        playlistName.text = playlist.name
        countTracks.text = context.resources.getQuantityString(
            R.plurals.tracks,
            playlist.countTracks,
            playlist.countTracks
        )

        Glide.with(itemView)
            .load(playlist.imageUri)
            .placeholder(R.drawable.no_cover)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2F, metrics.displayMetrics
                    ).toInt()
                )
            )
            .into(playlistImage)

    }
}