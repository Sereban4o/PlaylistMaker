package com.example.playlistmaker.playlists.ui.fragment

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.ui.view_model.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class CreatePlaylistFragment() : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val requester = PermissionRequester.instance()
    private lateinit var textWatcher: TextWatcher
    private lateinit var nameEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var buttonTextView: TextView
    private var addImage = false
    private var imageUri: Uri? = null
    private var playlist = Playlist()
    private lateinit var file: File
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var isSave = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {

                this.isEnabled = false
                close()
            }

        nameEditText = binding.playlistName
        noteEditText = binding.playlistNote
        buttonTextView = binding.buttonCreate
        buttonTextView.isEnabled = false

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialogTitle))
            .setMessage(getString(R.string.dialogMessage))
            .setNeutralButton(getString(R.string.dialogCancel)) { dialog, which ->

            }
            .setPositiveButton(getString(R.string.dialogClose)) { dialog, which ->
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

                if (uri != null) {
                    binding.playlistCover.setImageURI(uri)
                    imageUri = uri
                    binding.playlistCover.setPadding(0)
                    binding.playlistCover.background = null
                    binding.playlistCover.setScaleType(ImageView.ScaleType.CENTER_CROP)
                    addImage = true
                    buttonTextView.isEnabled =
                        (nameEditText.text.toString().isNotEmpty() && addImage)

                }
            }

        binding.playlistCover.setOnClickListener {
            lifecycleScope.launch {
                requester.request(Manifest.permission.READ_MEDIA_IMAGES).collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }

                        is PermissionResult.Denied.DeniedPermanently -> {

                        }

                        is PermissionResult.Denied.NeedsRationale -> {

                        }

                        is PermissionResult.Cancelled -> {
                            return@collect
                        }
                    }
                }
            }
        }

        binding.arrowBack.setOnClickListener {
            close()
        }

        binding.buttonCreate.setOnClickListener {
            saveImageToPrivateStorage(imageUri!!)
            playlist = Playlist(
                name = nameEditText.text.toString(),
                note = noteEditText.text.toString(),
                imageUri = file.absolutePath
            )
            viewModel.addPlaylist(playlist)
            isSave = true
            val text =
                activity?.resources?.getString(R.string.playlistCreated, playlist.name);

            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()

        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.playlistNameSmall.isVisible = nameEditText.text.toString().isNotEmpty()
                binding.playlistNoteSmall.isVisible = noteEditText.text.toString().isNotEmpty()
                if (nameEditText.text.toString().isNotEmpty()) {
                    nameEditText.setBackgroundResource(R.drawable.edit_text_playlist_fill)
                } else {
                    nameEditText.setBackgroundResource(R.drawable.edit_text_playlist)
                }

                if (noteEditText.text.toString().isNotEmpty()) {
                    noteEditText.setBackgroundResource(R.drawable.edit_text_playlist_fill)
                } else {
                    noteEditText.setBackgroundResource(R.drawable.edit_text_playlist)
                }

                buttonTextView.isEnabled = (nameEditText.text.toString().isNotEmpty() && addImage)

            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher.let { nameEditText.addTextChangedListener(it) }
        textWatcher.let { noteEditText.addTextChangedListener(it) }

    }

    private fun saveImageToPrivateStorage(uri: Uri) {

        val filePath =
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "@string/covers"
            )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val filename = UUID.randomUUID().toString().substring(0, 35) + ".jpg"
        file = File(filePath, filename)

        val inputStream = requireActivity().contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun close() {
        if (!isSave && (nameEditText.text.toString().isNotEmpty() || noteEditText.text.toString()
                .isNotEmpty() || addImage)
        ) {
            confirmDialog.show()
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

}