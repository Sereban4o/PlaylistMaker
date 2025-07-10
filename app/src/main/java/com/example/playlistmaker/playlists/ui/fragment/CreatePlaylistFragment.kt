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
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.playlists.domain.model.Playlist
import com.example.playlistmaker.playlists.ui.view_model.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.lazy
import androidx.core.net.toUri

open class CreatePlaylistFragment() : Fragment() {

    companion object {

        private const val PLAYLIST_VIEW = "PLAYLIST_VIEW"
        fun createArgs(playlistArg: Playlist?): Bundle =
            bundleOf(PLAYLIST_VIEW to playlistArg)
    }

    private var playlistArg = null as Playlist?
    private val viewModel: CreatePlaylistViewModel by lazy { getViewModel { parametersOf(playlistArg) } }
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val requester = PermissionRequester.instance()
    private lateinit var textWatcher: TextWatcher
    private lateinit var nameEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var buttonTextView: TextView
    private lateinit var imageUri: Uri
    private var playlist = Playlist()
    private var updatePlaylist = Playlist()
    private lateinit var file: File
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var isSave = false
    private var text: String? = null

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
        if (arguments != null) {
            playlistArg = arguments?.getParcelable(PLAYLIST_VIEW)!!
            binding.playlistName.setText(playlistArg?.name)
            binding.playlistNote.setText(playlistArg?.note)
            if (playlistArg?.imageUri != "") {
                binding.playlistCover.setImageURI(playlistArg?.imageUri?.toUri())
            }
            binding.title.text = getString(R.string.editPlaylistTitle)
            binding.buttonCreate.text = getString(R.string.save)
            binding.buttonCreate.isEnabled = true
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {

                this.isEnabled = false
                close()
            }

        nameEditText = binding.playlistName
        noteEditText = binding.playlistNote
        buttonTextView = binding.buttonCreate
        if (playlistArg == null) {
            buttonTextView.isEnabled = false
        }
        imageUri = Uri.EMPTY

        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.Dialog)
            .setTitle(getString(R.string.dialogTitle))
            .setMessage(getString(R.string.dialogMessage))
            .setNegativeButton(getString(R.string.dialogCancel)) { dialog, which ->}
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

            var oldUri = ""

            if (playlistArg == null) {
                if (imageUri != Uri.EMPTY) {
                    saveImageToPrivateStorage(imageUri)

                    playlist = Playlist(
                        name = nameEditText.text.toString(),
                        note = noteEditText.text.toString(),
                        imageUri = file.absolutePath
                    )
                } else {
                    playlist = Playlist(
                        name = nameEditText.text.toString(),
                        note = noteEditText.text.toString(),
                        imageUri = ""
                    )
                }
                viewModel.addPlaylist(playlist)
                text =
                    activity?.resources?.getString(R.string.playlistCreated, playlist.name);


            } else {

                if (imageUri != Uri.EMPTY) {
                    saveImageToPrivateStorage(imageUri)
                    oldUri = playlistArg!!.imageUri
                    updatePlaylist = Playlist(
                        id = playlistArg!!.id,
                        name = nameEditText.text.toString(),
                        note = noteEditText.text.toString(),
                        imageUri = file.absolutePath
                    )


                } else {
                    updatePlaylist = Playlist(
                        id = playlistArg!!.id,
                        name = nameEditText.text.toString(),
                        note = noteEditText.text.toString(),
                        imageUri = playlistArg!!.imageUri
                    )
                }


                viewModel.updatePlaylist(updatePlaylist)
                if (oldUri.isNotEmpty()) {
                    deleteImageFromPrivateStorage(oldUri)
                }
                text =
                    activity?.resources?.getString(R.string.playlistUpdated, playlist.name)
            }

            isSave = true
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

                buttonTextView.isEnabled = (nameEditText.text.toString().isNotEmpty())

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
        if (!isSave && playlistArg == null && (nameEditText.text.toString()
                .isNotEmpty() || noteEditText.text.toString()
                .isNotEmpty() || imageUri != Uri.EMPTY)
        ) {
            confirmDialog.show()
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun deleteImageFromPrivateStorage(uri: String) {
        val file = File(uri)
        file.delete()
    }
}