package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
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
    }

    private var text: String = ""
    private val trackList: MutableList<Track> = mutableListOf()
    private val baseURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val trackApiService: TrackApi = retrofit.create(TrackApi::class.java)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recycler = findViewById<RecyclerView>(R.id.trackList)
        val inputEditText = findViewById<EditText>(R.id.edit)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val emptySearch = findViewById<View>(R.id.emptySearch)
        val errorSearch = findViewById<View>(R.id.errorSearch)
        val errorButtonRefresh = findViewById<Button>(R.id.errorButtonRefresh)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TracksAdapter(trackList)

        errorButtonRefresh.setOnClickListener {
            requestSong(inputEditText, errorSearch, emptySearch, recycler)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requestSong(inputEditText, errorSearch, emptySearch, recycler)
            }
            false
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
            recycler.adapter?.notifyDataSetChanged()
            hideSoftKeyboard(it)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
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
        emptySearch: View,
        recycler: RecyclerView
    ) {
        trackApiService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    trackList.clear()
                    when (response.code()) {
                        200 -> {
                            errorSearch.isVisible = false
                            if (response.body()?.results?.isEmpty() == true) {
                                emptySearch.isVisible = true
                            } else {
                                emptySearch.isVisible = false
                                response.body()?.results?.let { trackList.addAll(it) }
                                recycler.adapter?.notifyDataSetChanged()
                            }
                        }

                        else -> {
                            errorSearch.isVisible = true
                            emptySearch.isVisible = false
                        }
                    }

                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    errorSearch.isVisible = true
                    emptySearch.isVisible = false
                }
            })
    }
}