package com.example.playlistmaker.playlists.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.player.ui.activity.TrackActivity
import com.example.playlistmaker.playlists.domain.state.PlaylistState
import com.example.playlistmaker.playlists.ui.adapter.PlaylistTracksAdapter
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {

    companion object {

        private const val PLAYLIST_VIEW = "PLAYLIST_VIEW"
        fun createArgs(playlistArg: Playlist): Bundle =
            bundleOf(PLAYLIST_VIEW to playlistArg)
    }

    private var playlist = Playlist()
    private val viewModel: PlaylistViewModel by lazy { getViewModel { parametersOf(playlist) } }
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val adapter = PlaylistTracksAdapter()
    private lateinit var trackList: RecyclerView
    private lateinit var menuBottomSheet: BottomSheetBehavior<LinearLayout>
    private lateinit var tracksBottomSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlist = arguments?.getParcelable(PLAYLIST_VIEW)!!
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        menuBottomSheet = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        tracksBottomSheet = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        tracksBottomSheet.addBottomSheetCallback(object :
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

        menuBottomSheet.addBottomSheetCallback(object :
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

        binding.menu.setOnClickListener {
            menuBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.root.setOnClickListener {
            if (menuBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                menuBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        trackList = binding.trackList
        trackList.adapter = adapter
        adapter.setOnClickListener(object : PlaylistTracksAdapter.OnClickListener {
            override fun onClick(track: Track) {
                findNavController().navigate(
                    R.id.action_playlistFragment_to_trackActivity,
                    TrackActivity.Companion.createArgs(track)
                )
            }

            override fun onLongClick(track: Track) {
                MaterialAlertDialogBuilder(requireContext(), R.style.Dialog)
                    .setMessage(getString(R.string.deleteTrack))
                    .setNeutralButton(getString(R.string.no)) { dialog, which ->

                    }
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        viewModel.deleteTrackFromPlaylist(track.trackId.toString(), playlist.id)
                    }.show()
            }
        })
        trackList.layoutManager = LinearLayoutManager(requireContext())

        binding.share.setOnClickListener {
            if (adapter.trackList.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.noTracksInPlaylist),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.sharePlaylist(playlist, adapter.trackList)
            }
        }
        binding.arrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.menuSharing.setOnClickListener {
            viewModel.sharePlaylist(playlist, adapter.trackList)
        }

        binding.menuDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.Dialog)
                .setMessage(getString(R.string.textDeletePlaylist, playlist.name))
                .setNeutralButton(getString(R.string.no)) { dialog, which ->

                }
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    viewModel.deletePlaylist(playlist.id)
                    if (playlist.imageUri.isNotEmpty()) {
                        deleteImageFromPrivateStorage(playlist.imageUri)
                    }

                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                .show()
        }

        binding.menuEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_createPlaylistFragment,
                CreatePlaylistFragment.createArgs(playlist)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistState) {
        binding.name.text = state.playlist.name
        binding.playlistName.text = state.playlist.name
        binding.countTracks.text =
            activity?.resources?.getQuantityString(R.plurals.tracks, state.count, state.count)
        binding.note.text = state.playlist.note
        binding.count.text =
            activity?.resources?.getQuantityString(R.plurals.tracks, state.count, state.count)
        Glide.with(requireContext())
            .load(state.playlist.imageUri)
            .placeholder(R.drawable.placeholder_big)
            .centerCrop()
            .into(binding.image)

        Glide.with(requireContext())
            .load(state.playlist.imageUri)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.playlistImage)
        binding.allTime.text = state.allTime
        adapter.trackList.clear()
        adapter.trackList.addAll(state.trackList)
        adapter.notifyDataSetChanged()
    }

    private fun deleteImageFromPrivateStorage(uri: String) {
        val file = File(uri)
        file.delete()
    }
}