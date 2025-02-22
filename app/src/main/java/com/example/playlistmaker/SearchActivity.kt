package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var text: String = ""
    private val trackList: MutableList<Track> = mutableListOf()
    private lateinit var tracksAdapter: TracksAdapter
    private val baseURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackApiService: TrackApi = retrofit.create(TrackApi::class.java)
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
        val searchHistory = SearchHistory()
        progressBar = findViewById(R.id.progressBar)

        recycler.layoutManager = LinearLayoutManager(this)
        tracksAdapter = TracksAdapter {
            if (clickDebounce()) {
                searchHistory.add(it, sharedPrefs)
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
            searchHistory.clear(sharedPrefs)
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
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun requestSong(
        inputEditText: EditText,
        errorSearch: View,
        emptySearch: View
    ) {
        if (inputEditText.text.toString().isEmpty()){
            return
        }
        trackList.clear()
        progressBar.isVisible = true
        trackApiService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    trackList.clear()
                    when (response.isSuccessful) {
                        true -> {
                            progressBar.isVisible = false
                            errorSearch.isVisible = false
                            val result = response.body()?.results
                            if (result.isNullOrEmpty()) {
                                emptySearch.isVisible = true
                            } else {
                                emptySearch.isVisible = false
                                result.let { trackList.addAll(it) }
                                tracksAdapter.notifyDataSetChanged()
                            }
                        }

                        else -> {
                            progressBar.isVisible = false
                            errorSearch.isVisible = true
                            emptySearch.isVisible = false
                        }
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    progressBar.isVisible = false
                    errorSearch.isVisible = true
                    emptySearch.isVisible = false
                }
            })
    }

    private fun historyTracks(
        vHistoryTrackList: RecyclerView,
        vHistoryTracks: View,
        visible: Boolean
    ) {
        val sharedPrefs = getSharedPreferences(PLAYLIST_PREF, MODE_PRIVATE)
        val historyTrackList: MutableList<Track> = SearchHistory().get(sharedPrefs)

        vHistoryTrackList.layoutManager = LinearLayoutManager(this)
        val historyAdapter = HistoryTracksAdapter {
            if (clickDebounce()) {
                val intent = Intent(this, TrackActivity::class.java)
                intent.putExtra(TRACK_VIEW, (it))
                startActivity(intent)
            }
        }
        historyAdapter.trackList = historyTrackList
        vHistoryTrackList.adapter = historyAdapter
        vHistoryTracks.isVisible = (historyTrackList.size != 0 && visible)
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