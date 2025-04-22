package com.example.playlistmaker.ui.settings

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.NIGHT_MODE
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator


class SettingsActivity : AppCompatActivity() {
    private val externalNavigation = Creator.provideExternalNavigation()
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.setting_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val userAgreement = findViewById<Button>(R.id.userAgreement)
        userAgreement.setOnClickListener {
            externalNavigation.openUrl(this)
        }

        val share = findViewById<Button>(R.id.share)
        share.setOnClickListener {
            externalNavigation.share(this)
        }

        val support = findViewById<Button>(R.id.support)
        support.setOnClickListener {
            externalNavigation.mail(this)
        }

        val switchTheme = findViewById<Switch>(R.id.switchTheme)
        val sharedPrefs = getSharedPreferences(NIGHT_MODE, MODE_PRIVATE)
        val darkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = darkModeFlags == Configuration.UI_MODE_NIGHT_YES
        val darkTheme = sharedPrefs.getBoolean(NIGHT_MODE, isDarkModeOn)

        switchTheme.isChecked = darkTheme

        switchTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}