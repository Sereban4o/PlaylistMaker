package com.example.playlistmaker.playlists.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.state.PlaylistsState
import com.example.playlistmaker.playlists.ui.adapter.PlaylistsAdapter
import com.example.playlistmaker.playlists.ui.view_model.PlaylistsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = PlaylistsFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    private val viewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private lateinit var placeholderMessage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var playlistsRecycler: RecyclerView
    private val adapter = PlaylistsAdapter {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playlistFragment,
            PlaylistFragment.createArgs(it)

        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeholderMessage = binding.placeholderMessage
        progressBar = binding.progressBar
        playlistsRecycler = binding.recyclerView
        playlistsRecycler.layoutManager = GridLayoutManager(
            requireContext(),
            2
        )
        playlistsRecycler.adapter = adapter
        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.createNewPlaylist.setOnClickListener {
            if (clickDebounce()) {
                findNavController().navigate(
                    R.id.action_libraryFragment_to_createPlaylistFragment
                )
            }
        }

    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty(state.message)
            is PlaylistsState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        playlistsRecycler.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showEmpty(message: String) {
        playlistsRecycler.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        placeholderMessage.text = message
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistsRecycler.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        progressBar.visibility = View.GONE
        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

}
