package com.example.playlistmaker.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

internal class AudioService : Service(), AudioPlayerControl {

    private var mediaPlayer: MediaPlayer? = null
    private var track: Track? = null
    private val binder = AudioServiceBinder()
    private var timerJob: Job? = null
    private val _audioState = MutableStateFlow<AudioState>(AudioState.Default())
    val audioState = _audioState.asStateFlow()
    private var appName = ""

    inner class AudioServiceBinder : Binder() {
        fun getService(): AudioService = this@AudioService
    }

    override fun onBind(intent: Intent?): IBinder? {
        track = intent?.getParcelableExtra<Track>("track")
        initMediaPlayer()
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        appName = getString(R.string.app_name)
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            appName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = appName

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(appName)
            .setContentText("${track?.artistName} - ${track?.trackName}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    override fun onDestroy() {
        releasePlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initMediaPlayer()
        return START_NOT_STICKY
    }

    private fun initMediaPlayer() {
        if (track == null) return

        mediaPlayer?.setDataSource(track?.previewUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _audioState.value = AudioState.Prepared()
        }
        mediaPlayer?.setOnCompletionListener {
            _audioState.value = AudioState.Prepared()
            hideNotification()

        }
    }

    override fun getPlayerState(): StateFlow<AudioState> {
        return audioState
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _audioState.value = AudioState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _audioState.value = AudioState.Paused(getCurrentPlayerPosition())
    }

    override fun showNotification() {
        if (_audioState.value is AudioState.Playing) {
            createNotificationChannel()
            ServiceCompat.startForeground(
                this,
                SERVICE_NOTIFICATION_ID,
                createServiceNotification(),
                getForegroundServiceTypeConstant()
            )
        }
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun releasePlayer() {
        mediaPlayer?.stop()
        timerJob?.cancel()
        _audioState.value = AudioState.Default()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(DELAY)
                _audioState.value = AudioState.Playing(getCurrentPlayerPosition())

            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition)
            ?: "00:00"
    }

    private companion object {
        const val NOTIFICATION_CHANNEL_ID = "playmaker_service_channel"
        const val SERVICE_NOTIFICATION_ID = 100
        private const val DELAY = 300L
    }

}