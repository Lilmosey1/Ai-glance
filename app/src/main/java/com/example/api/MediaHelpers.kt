package com.example.api

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object MediaHelpers {
    private const val TAG = "MediaHelpers"
    private var mediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null
    private var recordedFile: File? = null

    /**
     * Save base64 string to a local cache file.
     */
    fun saveBase64ToFile(context: Context, base64Str: String, fileName: String): File? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { fos ->
                fos.write(decodedBytes)
            }
            Log.d(TAG, "File saved successfully: ${file.absolutePath} (${file.length()} bytes)")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save base64 file", e)
            null
        }
    }

    /**
     * Save raw byte array to a local cache file.
     */
    fun saveBytesToFile(context: Context, bytes: ByteArray, fileName: String): File? {
        return try {
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
            }
            Log.d(TAG, "File saved successfully from bytes: ${file.absolutePath} (${file.length()} bytes)")
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save raw bytes file", e)
            null
        }
    }

    /**
     * Play local audio file inside cache using native MediaPlayer.
     */
    fun playAudio(file: File, onCompletion: () -> Unit, onError: (String) -> Unit) {
        try {
            stopAudio()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    onCompletion()
                    stopAudio()
                }
                setOnErrorListener { _, what, extra ->
                    val errMsg = "MediaPlayer error: what=$what, extra=$extra"
                    Log.e(TAG, errMsg)
                    onError(errMsg)
                    stopAudio()
                    true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio", e)
            onError(e.localizedMessage ?: "Unknown media player error")
        }
    }

    /**
     * Stop currently playing audio.
     */
    fun stopAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop media player", e)
        } finally {
            mediaPlayer = null
        }
    }

    /**
     * Retrieve media player details.
     */
    fun isAudioPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Start recording microphone input using MediaRecorder.
     */
    fun startRecording(context: Context, onStartSuccess: () -> Unit, onFailure: (String) -> Unit) {
        try {
            stopRecording()
            recordedFile = File(context.cacheDir, "recorded_temp_audio.mp4")
            
            @Suppress("DEPRECATION")
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(recordedFile!!.absolutePath)
                prepare()
                start()
            }
            
            Log.d(TAG, "Audio recording started: ${recordedFile!!.absolutePath}")
            onStartSuccess()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start audio recording", e)
            onFailure(e.localizedMessage ?: "Initialization error")
            recordedFile = null
            mediaRecorder = null
        }
    }

    /**
     * Stop recording.
     * Returns the recorded File if successful.
     */
    fun stopRecording(): File? {
        return try {
            mediaRecorder?.let {
                it.stop()
                it.release()
                Log.d(TAG, "Audio recording stopped successfully.")
            }
            recordedFile
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio recorder", e)
            null
        } finally {
            mediaRecorder = null
        }
    }
}
