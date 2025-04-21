package com.example.playlistmaker.ui.tracks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.PLAYLIST_PREF
import com.example.playlistmaker.R
import com.example.playlistmaker.TRACK_VIEW
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.ui.track.TrackActivity
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val tracksInteractor = Creator.provideTracksInteractor()
    private var text: String = ""
    private val trackList: MutableList<Track> = mutableListOf()
    private val historyTrackList: MutableList<Track> = mutableListOf()
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var inputEditText: EditText
    private lateinit var errorSearch: View
    private lateinit var emptySearch: View
    private lateinit var progressBar: ProgressBar
    private val searchRunnable = Runnable { requestSong(inputEditText, errorSearch, emptySearch) }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.search_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recycler = findViewById<RecyclerView>(R.id.trackList)
        val vHistoryTrackList = findViewById<RecyclerView>(R.id.historyTrackList)
        inputEditText = findViewById(R.id.edit)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        emptySearch = findViewById(R.id.emptySearch)
        errorSearch = findViewById(R.id.errorSearch)
        val errorButtonRefresh = findViewById<Button>(R.id.errorButtonRefresh)
        val buttonClearHistory = findViewById<Button>(R.id.clearHistory)
        val sharedPrefs = getSharedPreferences(PLAYLIST_PREF, MODE_PRIVATE)
        val vHistoryTracks = findViewById<View>(R.id.viewHistoryTracks)
        progressBar = findViewById(R.id.progressBar)


        recycler.layoutManager = LinearLayoutManager(this)
        tracksAdapter = TracksAdapter {
            if (clickDebounce()) {
                tracksInteractor.addToHistory(it, sharedPrefs)
                val intent = Intent(this, TrackActivity::class.java)
                intent.putExtra(TRACK_VIEW, (it))
                startActivity(intent)
            }
        }
        tracksAdapter.trackList = trackList
        recycler.adapter = tracksAdapter

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            historyTracks(
                vHistoryTrackList,
                vHistoryTracks,
                hasFocus && inputEditText.text.isEmpty()
            )
        }

        inputEditText.requestFocus()

        errorButtonRefresh.setOnClickListener {
            requestSong(inputEditText, errorSearch, emptySearch)
        }

        buttonClearHistory.setOnClickListener {
            tracksInteractor.clearHistory(sharedPrefs)
            vHistoryTracks.isVisible = false
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState != null) {
            text = savedInstanceState.getString(EDIT_TEXT).toString()
        }

        inputEditText.setText(text)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            trackList.clear()
            errorSearch.isVisible = false
            emptySearch.isVisible = false
            tracksAdapter.notifyDataSetChanged()
            hideSoftKeyboard(it)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                historyTracks(
                    vHistoryTrackList,
                    vHistoryTracks,
                    inputEditText.hasFocus() && inputEditText.text.isEmpty()
                )
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
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

    private fun requestSong(
        inputEditText: EditText,
        errorSearch: View,
        emptySearch: View
    ) {
        if (inputEditText.text.toString().isEmpty()) {
            return
        }
        trackList.clear()
        progressBar.isVisible = true

        tracksInteractor.searchTracks(
            inputEditText.text.toString(), object :
                TracksInteractor.TracksConsumer {
                @SuppressLint("NotifyDataSetChanged")
                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        progressBar.isVisible = false
                        errorSearch.isVisible = false

                        if (foundTracks.isEmpty()) {
                            emptySearch.isVisible = true
                        } else {
                            emptySearch.isVisible = false
                            foundTracks.let { trackList.addAll(it) }
                            tracksAdapter.notifyDataSetChanged()
                        }
                    }
                }
            },
            object : TracksInteractor.ErrorConsumer {
                override fun consume(error: Boolean) {
                    runOnUiThread {
                        progressBar.isVisible = false
                        errorSearch.isVisible = true
                    }
                }
            }
        )
    }

    private fun historyTracks(
        vHistoryTrackList: RecyclerView,
        vHistoryTracks: View,
        visible: Boolean
    ) {
        val sharedPrefs = getSharedPreferences(PLAYLIST_PREF, MODE_PRIVATE)

        vHistoryTrackList.layoutManager = LinearLayoutManager(this)
        val historyAdapter = HistoryTracksAdapter {
            if (clickDebounce()) {
                val intent = Intent(this, TrackActivity::class.java)
                intent.putExtra(TRACK_VIEW, (it))
                startActivity(intent)
            }
        }

        tracksInteractor.getHistory(sharedPrefs, object : TracksInteractor.TracksHistory {
            @SuppressLint("NotifyDataSetChanged")
            override fun consume(history: List<Track>) {

                runOnUiThread {
                    historyTrackList.clear()
                    historyTrackList.addAll(history)
                    historyAdapter.trackList = historyTrackList
                    vHistoryTrackList.adapter = historyAdapter
                    vHistoryTracks.isVisible = (historyTrackList.isNotEmpty() && visible)

                }
            }
        })
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}