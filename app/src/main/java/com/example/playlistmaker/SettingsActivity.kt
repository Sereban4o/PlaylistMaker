package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
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
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.offer))
            )
            startActivity(intent)
        }

        val share = findViewById<Button>(R.id.share)
        share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.addressPracticum)
            );
            startActivity(intent)

        }

        val support = findViewById<Button>(R.id.support)
        support.setOnClickListener {
            val message = getString(R.string.mailMessage)
            val title = getString(R.string.mailTitle)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mailAddress)))
            intent.putExtra(Intent.EXTRA_TEXT, title)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)

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