package com.example.playlistmaker.settings.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.example.playlistmaker.sharing.domain.model.EmailData
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private var checkedState = mutableStateOf(false)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {

            setContent {
                checkedState = remember { mutableStateOf(viewModel.getCurrentTheme()) }
                viewModel.updateThemeSetting(checkedState.value)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = colorResource(R.color.background),
                )
                {
                    Settings()
                }
            }
        }
    }


    @Composable
    fun Settings() {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
        ) {
            TopBar()
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.dark_theme),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 21.dp),
                    style = TextStyle(
                        color = colorResource(R.color.textToolbarColor),
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                        fontSize = 16.sp
                    )
                )
                Switch(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = checkedState.value,
                    onCheckedChange = {
                        checkedState.value = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource( R.color.checkedThumbColor),
                        checkedTrackColor = colorResource(R.color.checkedTrackColor),
                        uncheckedThumbColor = colorResource( R.color.uncheckedThumbColor),
                        uncheckedTrackColor = colorResource(R.color.uncheckedTrackColor)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource( R.string.share),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 21.dp)
                        .clickable(
                            onClick = { viewModel.shareLink(getString(R.string.addressPracticum)) }
                        ),
                    style = TextStyle(
                        color = colorResource(R.color.textToolbarColor),
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                        fontSize = 16.sp
                    )
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    painter = painterResource(R.drawable.share),
                    contentDescription = null,
                    tint = colorResource(R.color.iconSettingsColor)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource( R.string.support),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 21.dp)
                        .clickable(onClick = {
                            viewModel.openEmail(
                                EmailData(
                                    getString(R.string.emailAddress),
                                    getString(R.string.emailSubject),
                                    getString(R.string.mailMessage)
                                )
                            )

                        }),
                    style = TextStyle(
                        color = colorResource(id = R.color.textToolbarColor),
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                        fontSize = 16.sp
                    )
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    painter = painterResource(R.drawable.support),
                    contentDescription = null,
                    tint = colorResource(R.color.iconSettingsColor)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.user_agreement),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 21.dp)
                        .clickable(onClick = { viewModel.openLink(getString(R.string.offer)) }),
                    style = TextStyle(
                        color = colorResource(id = R.color.textToolbarColor),
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                        fontSize = 16.sp
                    )
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    painter = painterResource(R.drawable.right_arrow),
                    contentDescription = null,
                    tint = colorResource( R.color.iconSettingsColor)
                )
            }

        }
    }

    @Composable
    fun TopBar() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
           ) {
                Text(

                    stringResource(R.string.settings),
                    style = TextStyle(
                        color = colorResource( R.color.textToolbarColor),
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(top = 14.dp, bottom = 16.dp)
                )
        }
    }

}