package com.example.playlistmaker.search.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.TRACK_VIEW
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.activity.TrackActivity
import com.example.playlistmaker.search.domain.state.TrackListHistoryState
import com.example.playlistmaker.search.domain.state.TrackListState
import com.example.playlistmaker.search.ui.adapter.HistoryTracksAdapter
import com.example.playlistmaker.search.ui.adapter.TracksAdapter
import com.example.playlistmaker.search.ui.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: ActivitySearchBinding
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
            val intent = Intent(this, TrackActivity::class.java)
            intent.putExtra(TRACK_VIEW, (it))
            startActivity(intent)
        }
    }
    private val trackHistoryAdapter = HistoryTracksAdapter {
        if (clickDebounce()) {
            val intent = Intent(this, TrackActivity::class.java)
            intent.putExtra(TRACK_VIEW, (it))
            startActivity(intent)
        }
    }
    private var isClickAllowed = true

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputEditText = binding.edit
        trackList = binding.trackList
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = tracksAdapter
        progressBar = binding.progressBar
        errorSearch = binding.errorSearch.root
        emptySearch = binding.emptySearch.root
        historyTrackList = binding.viewHistoryTracks.historyTrackList
        historyTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyTrackList.adapter = trackHistoryAdapter
        trackViewHistory = binding.viewHistoryTracks.root

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeHistoryState().observe(this) {
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
            trackViewHistory.visibility = View.GONE
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(EDIT_TEXT).toString()
        }

        binding.clearIcon.setOnClickListener {
            inputEditText.setText("")
            tracksAdapter.trackList.clear()
            errorSearch.isVisible = false
            emptySearch.isVisible = false
            tracksAdapter.notifyDataSetChanged()
            hideSoftKeyboard(it)
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

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { inputEditText.removeTextChangedListener(it) }
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
        trackViewHistory.visibility = View.GONE
        trackList.visibility = View.GONE
        errorSearch.visibility = View.GONE
        emptySearch.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showError() {
        trackViewHistory.visibility = View.GONE
        trackList.visibility = View.GONE
        errorSearch.visibility = View.VISIBLE
        emptySearch.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showEmpty() {
        trackViewHistory.visibility = View.GONE
        trackList.visibility = View.GONE
        errorSearch.visibility = View.GONE
        emptySearch.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        trackViewHistory.visibility = View.GONE
        trackList.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        tracksAdapter.trackList.clear()
        tracksAdapter.trackList.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showHistoryContent(tracks: List<Track>) {
        trackViewHistory.isVisible = tracks.isNotEmpty()
        trackHistoryAdapter.trackList.clear()
        trackHistoryAdapter.trackList.addAll(tracks)
        trackHistoryAdapter.notifyDataSetChanged()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(EDIT_TEXT).toString()
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}