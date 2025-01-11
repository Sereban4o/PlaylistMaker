package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
    }
}