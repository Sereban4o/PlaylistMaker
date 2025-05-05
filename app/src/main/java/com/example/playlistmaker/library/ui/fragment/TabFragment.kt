package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.ActivityTabBinding
import com.example.playlistmaker.library.ui.view_model.TabViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue


class TabFragment : Fragment() {
    companion object {
        private const val NUMBER = "number"
        private const val FAVORITES_TAB = 1
        private const val PLAYLISTS_TAB = 2

        fun newInstance(number: Int) = TabFragment().apply {
            arguments = Bundle().apply {
                putInt(NUMBER, number)
            }
        }
    }
    private val viewModel: TabViewModel by viewModel(){
        parametersOf(requireArguments().getInt(NUMBER))
    }
    private lateinit var binding: ActivityTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCurrentTab().observe(viewLifecycleOwner) { number ->
            binding.favorite.root.isVisible = (number == FAVORITES_TAB)
            binding.playlists.root.isVisible = (number == PLAYLISTS_TAB)
        }
    }
}