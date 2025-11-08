package com.example.playlistmaker.library.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.favorites.domain.state.FavoritesState
import com.example.playlistmaker.favorites.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.library.ui.adapter.TabViewPageAdapter
import com.example.playlistmaker.library.ui.view_model.LibraryViewModel
import com.example.playlistmaker.player.ui.activity.TrackActivity
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.domain.state.PlaylistsState
import com.example.playlistmaker.playlists.ui.fragment.PlaylistFragment
import com.example.playlistmaker.playlists.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.state.TrackListHistoryState
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class LibraryFragment : Fragment() {


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
                )
                {
                    Library()
                }
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun Library() {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { 2 })
        val viewModelFavorites: FavoritesViewModel by viewModel()
        val favoritesState by viewModelFavorites.observeState().observeAsState()
        val viewModelPlaylistsState: PlaylistsViewModel by viewModel()
        val playlistsState by viewModelPlaylistsState.observeState().observeAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)

        ) {
            TopBar()
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                containerColor = colorResource(R.color.background),
                indicator = { tabPositions ->

                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = colorResource(R.color.tab)
                    )
                },
                divider = { }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },

                    text = {
                        Text(
                            text = stringResource(R.string.favorite_tracks),
                            style = TextStyle(
                                color = colorResource(R.color.tab),
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                                fontSize = 14.sp
                            )
                        )
                    },
                )

                Tab(
                    selected = pagerState.currentPage == 1,

                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.playlists),
                            style = TextStyle(
                                color = colorResource(R.color.tab),
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                                fontSize = 14.sp
                            )
                        )
                    },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> Favorites(favoritesState)
                    1 -> Playlists(playlistsState)
                }
            }
        }
    }


    @Composable
    fun Favorites(favoritesState: FavoritesState?) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        )
        {
            when (favoritesState) {
                is FavoritesState.Content -> favoritesState.tracks.let {
                    if (it.isNotEmpty()) {
                        LazyFavoritesTrackList(
                            it
                        )
                    }
                }

                is FavoritesState.Empty -> EmptySearch()
                else -> {
                    ProgressBar()
                }
            }
        }

    }

    @Composable
    fun LazyFavoritesTrackList(trackList: List<Track>) {
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

                    findNavController().navigate(

                        R.id.action_libraryFragment_to_trackActivity,
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

            Column(
                modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
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
    fun Playlists(playlistsState: PlaylistsState?) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    findNavController().navigate(
                        R.id.action_libraryFragment_to_createPlaylistFragment
                    )
                }, modifier = Modifier
                    .padding(top = 24.dp, bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.refreshButtonBackground)
                )
            ) {
                Text(
                    text = stringResource(R.string.new_playlist),
                    style = TextStyle(
                        color = colorResource(R.color.refreshButtonText),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.ys_display_medium))
                    ),
                )
            }
            when (playlistsState) {
                is PlaylistsState.Content -> playlistsState.playlists.let {
                    if (it.isNotEmpty()) {
                        LazyPlaylists(
                            it
                        )
                    }
                }

                is PlaylistsState.Empty -> EmptyPlaylists()
                else -> {
                    ProgressBar()
                }
            }

        }
    }

    @Composable
    fun LazyPlaylists(playlists: List<Playlist>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(playlists) {
                PlaylistItem(it)
            }

        }
    }

    @Composable
    fun PlaylistItem(playlist: Playlist, modifier: Modifier = Modifier) {

        Column(
            modifier
                .clickable(onClick = {
                    findNavController().navigate(
                        R.id.action_libraryFragment_to_playlistFragment,
                        PlaylistFragment.createArgs(playlist)

                    )
                }
                ),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Column() {

                AsyncImage(
                    model = playlist.imageUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp, 160.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = painterResource(id = R.drawable.no_cover)
                )
                Text(
                    text = playlist.name,
                    modifier.padding(top = 4.dp),
                    style = TextStyle(
                        color = colorResource(R.color.playlistEditText),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular))
                    )
                )
                Text(
                    text = pluralStringResource(
                        id = R.plurals.tracks,
                        playlist.countTracks,
                        playlist.countTracks
                    ),
                    modifier.padding(top = 4.dp),
                    style = TextStyle(
                        color = colorResource(R.color.playlistEditText),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular))
                    )
                )
            }
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(

            stringResource(R.string.library),
            style = TextStyle(
                color = colorResource(R.color.textToolbarColor),
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = 22.sp
            ),
            modifier = Modifier.padding(top = 14.dp, bottom = 16.dp)
        )
    }
}

@Composable
fun EmptySearch() {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Image(
            contentDescription = null,
            painter = painterResource(R.drawable.empty_search),
            modifier = Modifier
                .padding(top = 110.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.empty_favorite),
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
fun EmptyPlaylists() {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Image(
            contentDescription = null,
            painter = painterResource(R.drawable.empty_search),
            modifier = Modifier
                .padding(top = 110.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(R.string.empty_playlists),
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
    }
}