package com.example.playlistmaker.favorites.ui.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class FavoritesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.fragment_track_view, parent, false)
) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val trackArtist: TextView = itemView.findViewById(R.id.trackArtist)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    private val metrics = parent.context.resources

    fun bind(track: Track) {

        val noData = itemView.context.getString(R.string.noData)

        trackName.text = track.trackName ?: noData
        trackArtist.text = track.artistName ?: noData
        trackArtist.requestLayout()
        trackTime.text = track.trackTimeMillis ?: noData

        Glide.with(itemView)
            .load(track.artworkUrl100 ?: noData)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2F, metrics.displayMetrics
                    ).toInt()
                )
            )
            .into(trackImage)

    }
}