package com.example.playlistmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.activity.TrackActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.state.TrackListState
import com.example.playlistmaker.search.domain.state.TrackListHistoryState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {

            setContent {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = colorResource(R.color.background),
                ) {
                    Search(viewModel = viewModel)
                }
            }

        }
    }

    @Composable
    fun Search(viewModel: SearchViewModel) {
        val text = viewModel.text.collectAsState().value
        val trackListState by viewModel.observeState().observeAsState()
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val historyTrackListState by viewModel.observeHistoryState().observeAsState()

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
        ) {
            TopBar()
            OutlinedTextField(
                text,
                { viewModel.onTextChanged(it) },
                singleLine = true,
                placeholder = {
                    Text(
                        stringResource(R.string.search),
                        style = TextStyle(
                            color = colorResource(R.color.searchColor),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.ys_display_regular))
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { if (it.isFocused && text.isEmpty()) viewModel.searchHistory() },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null,
                        tint = colorResource(R.color.searchColor)
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) Icon(
                        painter = painterResource(R.drawable.search_clear),
                        contentDescription = null,
                        tint = colorResource(R.color.searchColor),
                        modifier = Modifier.clickable(onClick = {
                            viewModel.onTextChanged("")
                            keyboardController?.hide()
                            viewModel.clearSearch()
                            viewModel.searchHistory()
                        })
                    )
                },

                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colorResource(R.color.black),
                    unfocusedTextColor = colorResource(R.color.black),
                    cursorColor = colorResource(R.color.cursor),
                    focusedContainerColor = colorResource(R.color.editSearch),
                    unfocusedContainerColor = colorResource(R.color.editSearch),
                    focusedBorderColor = colorResource(R.color.editSearch),
                    unfocusedBorderColor = colorResource(R.color.editSearch),
                ),
                shape = RoundedCornerShape(8.dp)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            when (trackListState) {
                is TrackListState.Content -> (trackListState as TrackListState.Content).tracks?.let {
                    LazyTrackList(
                        it
                    )
                }

                is TrackListState.Empty -> EmptySearch()
                is TrackListState.Error -> ErrorSearch(text)
                is TrackListState.Loading -> ProgressBar()
                else -> {}
            }
            when (historyTrackListState) {
                is TrackListHistoryState.Content -> (historyTrackListState as TrackListHistoryState.Content).tracks?.let {


                    if (text.isEmpty() && it.isNotEmpty()) {
                        LazyHistoryTrackList(
                            it
                        )
                    }
                }

                else -> {}
            }
        }
    }

    @Composable
    fun LazyHistoryTrackList(trackList: List<Track>) {
        Text(
            text = stringResource(R.string.youSearch),
            Modifier
                .padding(top = 42.dp)
                .fillMaxWidth(),
            style = TextStyle(
                color = colorResource(R.color.textSettingsColor),
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.ys_display_medium))
            ), textAlign = TextAlign.Center
        )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
        ) {
            items(trackList) {
                TrackItem(it)
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    viewModel.clearHistory()
                }, Modifier.padding(top = 24.dp),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.refreshButtonBackground)
                )
            ) {
                Text(
                    text = stringResource(R.string.cleanHistory),
                    style = TextStyle(
                        color = colorResource(R.color.clearButtonText),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.ys_display_medium))
                    ),
                )
            }
        }
    }

    @Composable
    fun LazyTrackList(trackList: List<Track>) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            items(trackList) {
                TrackItem(it)
            }
        }
    }

    @Composable
    fun TrackItem(track: Track, modifier: Modifier = Modifier) {
        Row(
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(onClick = {
                    viewModel.addToHistory(track)
                    findNavController().navigate(

                        R.id.action_searchFragment_to_trackActivity,
                        TrackActivity.createArgs(track)
                    )
                }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track.artworkUrl100,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(2.dp)),
                placeholder = painterResource(id = R.drawable.placeholder)
            )

            Column(modifier.weight(1f).padding(start = 8.dp)) {
                track.trackName?.let {
                    Text(
                        text = it,
                        softWrap = false,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = colorResource(R.color.TextTrack),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily(Font(R.font.ys_display_regular))
                        )
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    track.artistName?.let {
                        Text(
                            text = it,
                            softWrap = false,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                color = colorResource(R.color.subTextTrack),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily(Font(R.font.ys_display_regular))
                            )
                        )
                    }
                    Icon(
                        modifier = Modifier.padding(horizontal = 5.dp),
                        contentDescription = null,
                        painter = painterResource(R.drawable.separator_playlist),
                        tint = colorResource(R.color.subTextTrack)
                    )
                    track.trackTimeMillis?.let {
                        Text(
                            text = it,
                            softWrap = false,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                color = colorResource(R.color.subTextTrack),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily(Font(R.font.ys_display_regular))
                            )
                        )
                    }
                }
            }

            Image(
                contentDescription = null,
                painter = painterResource(R.drawable.arrow_button),
                alignment = Alignment.CenterEnd
            )
        }
    }

    @Composable
    fun TopBar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Text(
                stringResource(R.string.search),
                style = TextStyle(
                    color = colorResource(id = R.color.textToolbarColor),
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(top = 14.dp, bottom = 16.dp)
            )
        }
    }

    @Composable
    fun ProgressBar() {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.progressBar),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 140.dp)
            )
        }

    }

    @Composable
    fun EmptySearch() {
        Column(Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
            Image(
                contentDescription = null,
                painter = painterResource(R.drawable.empty_search),
                modifier = Modifier
                    .padding(top = 110.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(R.string.emptySearch),
                style = TextStyle(
                    color = colorResource(id = R.color.textSettingsColor),
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 19.sp
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

    }


    @Composable
    fun ErrorSearch(text: String) {
        Column(Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
            Image(
                contentDescription = null,
                painter = painterResource(R.drawable.error_search),
                modifier = Modifier
                    .padding(top = 110.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(R.string.errorSearch),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = colorResource(id = R.color.textSettingsColor),
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 19.sp
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = {
                    viewModel.onTextChanged(text)
                }, modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.refreshButtonBackground)
                )
            ) {
                Text(
                    text = stringResource(R.string.refresh),
                    style = TextStyle(
                        color = colorResource(R.color.refreshButtonText),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.ys_display_medium))
                    ),
                )
            }
        }

    }
}