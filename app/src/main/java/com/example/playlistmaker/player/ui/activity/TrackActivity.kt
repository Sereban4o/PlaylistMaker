package com.example.playlistmaker.player.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.view_model.TrackViewModel
import com.example.playlistmaker.playlists.ui.adapter.PlaylistPlayerAdapter
import com.example.playlistmaker.playlists.ui.fragment.CreatePlaylistFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class TrackActivity : AppCompatActivity() {

    companion object {

        private const val TRACK_VIEW = "TRACK_VIEW"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun createArgs(trackArg: Track): Bundle =
            bundleOf(TRACK_VIEW to trackArg)
    }

    private var track = Track()
    private val viewModel: TrackViewModel by lazy { getViewModel { parametersOf(track) } }
    private lateinit var binding: ActivityTrackBinding
    private var isClickAllowed = true
    private val adapter = PlaylistPlayerAdapter {
        if (clickDebounce()) {
            viewModel.addToPlaylist(it)
        }
    }
    private lateinit var playlists: RecyclerView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    @SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    binding.overlay.alpha = slideOffset
                }
            }
        })

        binding.addPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.createNewPlaylist.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val newFragment = CreatePlaylistFragment()
            fragmentTransaction.replace(R.id.activity_track, newFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        track = intent.getParcelableExtra<Track>(TRACK_VIEW)!!

        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observeMessage().observe(this) {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.observeIsAdded().observe(this) {
            if (!it) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        viewModel.getPlaylists()
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeMills.text = track.trackTimeMillis
        binding.collectionName.text = track.collectionName
        binding.releaseDate.text = track.releaseDate
        binding.country.text = track.country
        binding.primaryGenreName.text = track.primaryGenreName
        playlists = binding.playlists
        playlists.adapter = adapter
        playlists.layoutManager = LinearLayoutManager(this)

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


        binding.playButton.setOnTouchListener { v, event ->
            if (v.onTouchEvent(event)) {
                viewModel.playbackControl()
            }
            return@setOnTouchListener true
        }

        binding.addFavoriteButton.setOnClickListener {
            viewModel.editFavorite(track)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    private fun render(state: PlayerState) {

        if (state.state == 1) {
            binding.playButton.toggleIsPlaying()
        }

        binding.time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(state.progress)

        if (state.isFavorite) {
            binding.addFavoriteButton.setImageDrawable(getDrawable(R.drawable.favorite))
        } else {
            binding.addFavoriteButton.setImageDrawable(getDrawable(R.drawable.add_favorite))
        }

        if (state.playlists.isNotEmpty()) {
            adapter.playlists.clear()
            adapter.playlists.addAll(state.playlists)
            adapter.notifyDataSetChanged()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}