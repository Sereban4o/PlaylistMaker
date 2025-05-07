package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.example.playlistmaker.sharing.domain.model.EmailData
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.userAgreement.setOnClickListener {
            viewModel.openLink(getString(R.string.offer))
        }

        binding.share.setOnClickListener {
            viewModel.shareLink(getString(R.string.addressPracticum))
        }

        binding.support.setOnClickListener {
            viewModel.openEmail(
                EmailData(
                    getString(R.string.emailAddress),
                    getString(R.string.emailSubject),
                    getString(R.string.mailMessage)
                )
            )
        }

        viewModel.getCurrentTheme().observe(viewLifecycleOwner) { darkTheme ->
            binding.switchTheme.isChecked = darkTheme
        }

        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSetting(checked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}