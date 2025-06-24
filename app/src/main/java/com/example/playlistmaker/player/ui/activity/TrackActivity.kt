package com.example.playlistmaker.player.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.view_model.TrackViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class TrackActivity : AppCompatActivity() {

    companion object {

        private const val TRACK_VIEW = "TRACK_VIEW"

        fun createArgs(trackArg: Track): Bundle =
            bundleOf(TRACK_VIEW to trackArg)
    }

    private var track = Track()
    private val viewModel: TrackViewModel by lazy { getViewModel { parametersOf(track) } }
    private lateinit var binding: ActivityTrackBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.activityTrack) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        track = intent.getParcelableExtra<Track>(TRACK_VIEW)!!

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeMills.text = track.trackTimeMillis
        binding.collectionName.text = track.collectionName
        binding.releaseDate.text = track.releaseDate
        binding.country.text = track.country
        binding.primaryGenreName.text = track.primaryGenreName

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
            .into(binding.trackImage)


        binding.arrowBack.setOnClickListener {
            viewModel.release()
            finish()
        }
        viewModel.preparePlayer()


        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.addFavoriteButton.setOnClickListener {
            viewModel.editFavorite(track)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun render(state: PlayerState) {
        if (state.isPlaying) {
            binding.playButton.setImageDrawable(getDrawable(R.drawable.pause))
        } else {
            binding.playButton.setImageDrawable(getDrawable(R.drawable.play))
        }
        binding.time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(state.progress)

        if (state.inFavorite) {
            binding.addFavoriteButton.setImageDrawable(getDrawable(R.drawable.favorite))
        } else {
            binding.addFavoriteButton.setImageDrawable(getDrawable(R.drawable.add_favorite))
        }
    }
}