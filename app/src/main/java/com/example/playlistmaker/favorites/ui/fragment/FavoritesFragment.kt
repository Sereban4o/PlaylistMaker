package com.example.playlistmaker.favorites.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.favorites.domain.state.FavoritesState
import com.example.playlistmaker.favorites.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.player.ui.activity.TrackActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.favorites.ui.adapter.FavoritesAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance() = FavoritesFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    private val viewModel: FavoritesViewModel by viewModel()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private var isClickAllowed = true
    private lateinit var placeholderMessage: TextView
    private lateinit var favoritesList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = FavoritesAdapter() {
        if (clickDebounce()) {

            findNavController().navigate(
                R.id.action_libraryFragment_to_trackActivity,
                TrackActivity.createArgs(it)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeholderMessage = binding.placeholderMessage
        favoritesList = binding.favoritesList
        progressBar = binding.progressBar
        favoritesList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        favoritesList.adapter = adapter
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
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

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty(state.message)
            is FavoritesState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        favoritesList.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showEmpty(message: String) {
        favoritesList.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        placeholderMessage.text = message
    }

    private fun showContent(tracks: List<Track>) {
        favoritesList.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        progressBar.visibility = View.GONE
        adapter.trackList.clear()
        adapter.trackList.addAll(tracks)
        adapter.notifyDataSetChanged()
    }
}