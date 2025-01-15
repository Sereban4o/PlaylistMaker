package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val trackArtist: TextView = itemView.findViewById(R.id.trackArtist)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)

    fun bind(track: Track) {

        trackName.text = track.trackName
        trackArtist.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(15))
            .into(trackImage)
    }
}