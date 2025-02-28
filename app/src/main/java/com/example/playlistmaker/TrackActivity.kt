package com.example.playlistmaker

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.text.SimpleDateFormat
import java.util.Locale


class TrackActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 100L
    }

    private lateinit var playButton: ImageButton
    private lateinit var time: TextView
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var previewUrl: String
    private var mainThreadHandler: Handler? = null
    private var trackTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainThreadHandler = Handler(Looper.getMainLooper())
        val track = intent.getParcelableExtra<Track>(TRACK_VIEW)!!
        val trackName = findViewById<TextView>(R.id.trackName)
        val trackImage = findViewById<ImageView>(R.id.trackImage)
        val artistName = findViewById<TextView>(R.id.artistName)
        time = findViewById(R.id.time)
        val trackTimeMills = findViewById<TextView>(R.id.trackTimeMills)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val country = findViewById<TextView>(R.id.country)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        playButton = findViewById(R.id.playButton)

        trackName.text = track.trackName
        artistName.text = track.artistName
        time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
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

        previewUrl = track.previewUrl.toString()

        val arrowBack = findViewById<ImageButton>(R.id.arrow_back)
        arrowBack.setOnClickListener {
            finish()
        }

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageDrawable(getDrawable(R.drawable.play))
            playerState = STATE_PREPARED
            trackTime = 0L
            time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageDrawable(getDrawable(R.drawable.pause))
        playerState = STATE_PLAYING
        startTimer()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageDrawable(getDrawable(R.drawable.play))
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun startTimer() {

        mainThreadHandler?.post(
            createUpdateTimerTask()
        )
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    trackTime = mediaPlayer.currentPosition.toLong()
                    time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
                    mainThreadHandler?.postDelayed(this, DELAY)
                }
            }

        }
    }
}