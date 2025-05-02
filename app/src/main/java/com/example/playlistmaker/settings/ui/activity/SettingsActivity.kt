package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private  val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.settingActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.openLink(getString(R.string.offer))
        }

        binding.share.setOnClickListener {
            viewModel.shareLink(getString(R.string.addressPracticum))
        }

        binding.support.setOnClickListener {
            viewModel.openEmail(
                EmailData(
                    R.string.emailAddress.toString(),
                    R.string.emailSubject.toString(),
                    R.string.mailMessage.toString()
                )
            )
        }

        viewModel.getCurrentTheme().observe(this) { darkTheme ->
            binding.switchTheme.isChecked = darkTheme
        }

        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSetting(checked)
        }
    }
}