package com.example.playlistmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.TrackActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.state.TrackListHistoryState
import com.example.playlistmaker.search.domain.state.TrackListState
import com.example.playlistmaker.search.ui.adapter.HistoryTracksAdapter
import com.example.playlistmaker.search.ui.adapter.TracksAdapter
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {
    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var textWatcher: TextWatcher
    private var text: String = ""
    private lateinit var trackList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorSearch: View
    private lateinit var emptySearch: View
    private lateinit var historyTrackList: RecyclerView
    private lateinit var trackViewHistory: View
    private lateinit var inputEditText: EditText
    private val handler = Handler(Looper.getMainLooper())
    private val tracksAdapter = TracksAdapter {
        if (clickDebounce()) {
            viewModel.addToHistory(it)
            findNavController().navigate(
                R.id.action_searchFragment_to_trackActivity,
                TrackActivity.createArgs(it)
            )
        }
    }
    private val trackHistoryAdapter = HistoryTracksAdapter {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_searchFragment_to_trackActivity,
                TrackActivity.createArgs(it)
            )
        }
    }
    private var isClickAllowed = true
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputEditText = binding.edit
        trackList = binding.trackList
        trackList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackList.adapter = tracksAdapter
        progressBar = binding.progressBar
        errorSearch = binding.errorSearch.root
        emptySearch = binding.emptySearch.root
        historyTrackList = binding.viewHistoryTracks.historyTrackList
        historyTrackList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        historyTrackList.adapter = trackHistoryAdapter
        trackViewHistory = binding.viewHistoryTracks.root
        binding.clearIcon.isVisible = !inputEditText.text.isEmpty()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeHistoryState().observe(viewLifecycleOwner) {
            renderHistory(it)
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                viewModel.searchHistory()
            }
        }
        inputEditText.requestFocus()
        inputEditText.setText(text)

        binding.errorSearch.errorButtonRefresh.setOnClickListener {
            viewModel.searchDebounce(inputEditText.text.toString())
        }

        binding.viewHistoryTracks.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            trackViewHistory.isVisible = false
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(EDIT_TEXT).toString()
        }

        binding.clearIcon.setOnClickListener {
            inputEditText.setText("")
            viewModel.clearSearch()
            errorSearch.isVisible = false
            emptySearch.isVisible = false
            tracksAdapter.notifyDataSetChanged()
            hideSoftKeyboard(it)
            viewModel.searchHistory()
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = s.toString()
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                if (inputEditText.hasFocus() && inputEditText.text.isEmpty()) {
                    viewModel.searchHistory()
                } else {
                    trackViewHistory.isVisible = false
                }
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher.let { inputEditText.addTextChangedListener(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher.let { inputEditText.removeTextChangedListener(it) }
        _binding = null
    }

    private fun render(state: TrackListState) {
        when (state) {
            is TrackListState.Content -> state.tracks?.let { showContent(it) }
            is TrackListState.Empty -> showEmpty()
            is TrackListState.Error -> showError()
            is TrackListState.Loading -> showLoading()
        }
    }

    private fun renderHistory(state: TrackListHistoryState) {
        when (state) {
            is TrackListHistoryState.Content -> state.tracks?.let { showHistoryContent(it) }
        }

    }

    private fun showLoading() {
        trackViewHistory.isVisible = false
        trackList.isVisible = false
        errorSearch.isVisible = false
        emptySearch.isVisible = false
        progressBar.isVisible = true
    }

    private fun showError() {
        trackViewHistory.isVisible = false
        trackList.isVisible = false
        errorSearch.isVisible = true
        emptySearch.isVisible = false
        progressBar.isVisible = false
    }

    private fun showEmpty() {
        trackViewHistory.isVisible = false
        trackList.isVisible = false
        errorSearch.isVisible = false
        emptySearch.isVisible = true
        progressBar.isVisible = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        trackViewHistory.isVisible = false
        trackList.isVisible = true
        progressBar.isVisible = false
        tracksAdapter.trackList.clear()
        tracksAdapter.trackList.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistoryContent(tracks: List<Track>) {
        trackViewHistory.isVisible = tracks.isNotEmpty() && inputEditText.text.isEmpty()
        trackHistoryAdapter.trackList.clear()
        trackHistoryAdapter.trackList.addAll(tracks)
        trackHistoryAdapter.notifyDataSetChanged()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, text)
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}