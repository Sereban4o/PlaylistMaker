package com.example.playlistmaker

import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class TrackActivity : AppCompatActivity() {
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = intent.getParcelableExtra<Track>(TRACK_VIEW)!!
        val trackName = findViewById<TextView>(R.id.trackName)
        val trackImage = findViewById<ImageView>(R.id.trackImage)
        val artistName = findViewById<TextView>(R.id.artistName)
        val time = findViewById<TextView>(R.id.time)
        val trackTimeMills = findViewById<TextView>(R.id.trackTimeMills)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val country = findViewById<TextView>(R.id.country)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)

        trackName.text = track.trackName
        artistName.text = track.artistName
        time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        trackTimeMills.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate?.let {
            SimpleDateFormat("yyyy", Locale.getDefault()).format(
                it
            )
        }
        country.text = track.country
        primaryGenreName.text = track.primaryGenreName

        Glide.with(this)
            .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder_big)
            .centerCrop()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2F, getResources().displayMetrics
                    ).toInt()
                )
            )
            .into(trackImage)

        val arrowBack = findViewById<ImageButton>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }

    }


}