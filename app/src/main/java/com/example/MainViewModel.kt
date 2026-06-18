package com.example

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiResult
import com.example.api.MediaHelpers
import com.example.api.OmniAiApiClient
import com.example.api.VeoOperationResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

enum class WorkbenchScreen {
    CHAT,
    THINKING,
    IMAGE_STUDIO,
    VIDEO_VEO,
    SPEECH_TTS,
    MUSIC_STUDIO,
    MEDIA_SCANNER,
    ABOUT
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class VeoJob(
    val operationId: String,
    val prompt: String,
    val isImageToVideo: Boolean,
    var status: String, // "PENDING", "POLLING", "COMPLETED", "FAILED"
    var videoUrl: String? = null,
    var videoPath: String? = null,
    var errorMessage: String? = null,
    var progressSeconds: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context get() = getApplication()
    private val TAG = "MainViewModel"

    // App Screen Controller
    var currentScreen by mutableStateOf(WorkbenchScreen.CHAT)

    // --- 1. CHAT (LITE PROMPTING) STATES ---
    val chatMessages = mutableStateListOf<ChatMessage>().apply {
        add(ChatMessage("Hello! I am Gemini 3.1 Flash Lite. Ask me anything for a rapid-burst response!", false))
    }
    var chatInput by mutableStateOf("")
    var isChatSending by mutableStateOf(false)

    // --- 2. HIGH THINKING (REASONING) STATES ---
    var thinkingInput by mutableStateOf("Explain how SHA-256 collision resistance works step-by-step and show the math details.")
    var thinkingResponse by mutableStateOf<String?>(null)
    var thinkingThoughtLog by mutableStateOf<String?>(null)
    var isThinkingGenerating by mutableStateOf(false)
    var thinkingError by mutableStateOf<String?>(null)

    // --- 3. IMAGE STUDIO STATES ---
    var imagePrompt by mutableStateOf("A vibrant cyberpunk hacker workstation with multiple holographic floating monitors, 3D render, high-contrast neon teal and jade tones")
    var isGeneratingImage by mutableStateOf(false)
    var generatedImageFile by mutableStateOf<File?>(null)
    var imageAspectRatio by mutableStateOf("1:1") // "1:1", "16:9", "4:3", "9:16"
    var imageSizeSelected by mutableStateOf("1K") // "1K", "2K", "4K"
    var imageQualitySelected by mutableStateOf("High Quality (Gemini 3 Pro Image)") // or "Fast Preview (Gemini 3.1 Flash Image)"
    var imageError by mutableStateOf<String?>(null)

    // Image Editing States
    var selectedEditImageUri by mutableStateOf<Uri?>(null)
    var selectedEditImageBytes by mutableStateOf<ByteArray?>(null)
    var isEditingImage by mutableStateOf(false)

    // --- 4. VIDEO VEO STATES ---
    var videoPrompt by mutableStateOf("Cinematographic camera panning shot around an neon holographic glowing pyramid in a mystical obsidian desert, photorealistic, 4k")
    var videoAspectRatio by mutableStateOf("16:9") // "16:9", "9:16"
    var isSubmittingVideoJob by mutableStateOf(false)
    val veoJobs = mutableStateListOf<VeoJob>()
    var videoError by mutableStateOf<String?>(null)
    
    // Image to Video animates
    var selectedAnimateImageUri by mutableStateOf<Uri?>(null)
    var selectedAnimateImageBytes by mutableStateOf<ByteArray?>(null)

    // Active polling jobs registry
    private val pollingJobs = mutableMapOf<String, Job>()

    // --- 5. SPEECH STUDIO (TTS) STATES ---
    var speechInput by mutableStateOf("In the heart of the digital landscape, the artificial light never fades, casting a brilliant jade hue across the infinite corridors of code.")
    var voiceSelected by mutableStateOf("Kore") // "Kore" (default), "Puck", "Fenrir", "Aoede", "Charon"
    var isGeneratingSpeech by mutableStateOf(false)
    var generatedSpeechFile by mutableStateOf<File?>(null)
    var isSpeechPlaying by mutableStateOf(false)
    var speechError by mutableStateOf<String?>(null)

    // --- 6. MUSIC STUDIO (LYRIA) STATES ---
    var musicPrompt by mutableStateOf("Generate a rhythmic cyberpunk synthwave beats track with pulsing bassline, energetic speed, retro synthesizer sweeps")
    var isShortMusicClip by mutableStateOf(true) // true for lyria-3-clip-preview (up to 30s), false for lyria-3-pro-preview
    var isGeneratingMusic by mutableStateOf(false)
    var generatedMusicFile by mutableStateOf<File?>(null)
    var isMusicPlaying by mutableStateOf(false)
    var musicError by mutableStateOf<String?>(null)

    // --- 7. MEDIA SCANNER (VISION) STATES ---
    var scannedImageUri by mutableStateOf<Uri?>(null)
    var scannedImageBytes by mutableStateOf<ByteArray?>(null)
    var imageAnalysisPrompt by mutableStateOf("Analyze this photo in detail. Highlight objects, key textures, dominant colors, and mood.")
    var imageAnalysisResult by mutableStateOf<String?>(null)
    var isScanningImage by mutableStateOf(false)
    var imageScannerError by mutableStateOf<String?>(null)

    var scannedVideoUri by mutableStateOf<Uri?>(null)
    var scannedVideoBytes by mutableStateOf<ByteArray?>(null)
    var videoAnalysisPrompt by mutableStateOf("Summarize the visual information in this video, identifying notable motions and objects.")
    var videoAnalysisResult by mutableStateOf<String?>(null)
    var isScanningVideo by mutableStateOf(false)
    var videoScannerError by mutableStateOf<String?>(null)

    // --- 8. VOICE TRANSCRIBER STATES ---
    var isRecordingAudio by mutableStateOf(false)
    var recordedAudioFile by mutableStateOf<File?>(null)
    var isTranscribingSource by mutableStateOf(false)
    var transcribedTextResult by mutableStateOf<String?>(null)
    var recordingTimerSeconds by mutableStateOf(0)
    var transcriberError by mutableStateOf<String?>(null)
    private var recordTimerJob: Job? = null

    // Cleanup resources
    override fun onCleared() {
        super.onCleared()
        MediaHelpers.stopAudio()
        recordTimerJob?.cancel()
        pollingJobs.values.forEach { it.cancel() }
    }

    // ==========================================
    // ACTION CONTROLLERS
    // ==========================================

    // --- 1. CHAT ---
    fun sendChatMessage() {
        val messageText = chatInput.trim()
        if (messageText.isEmpty() || isChatSending) return

        chatMessages.add(ChatMessage(messageText, true))
        chatInput = ""
        isChatSending = true

        viewModelScope.launch {
            try {
                // Call gemini-3.1-flash-lite model
                val result = OmniAiApiClient.generateContent(
                    model = "gemini-3.1-flash-lite-preview",
                    prompt = messageText
                )
                chatMessages.add(ChatMessage(result.text ?: "No output text generated.", false))
            } catch (e: Exception) {
                chatMessages.add(ChatMessage("Error: ${e.localizedMessage}", false))
            } finally {
                isChatSending = false
            }
        }
    }

    // --- 2. THINKING ---
    fun runHighThinkingExplanation() {
        val q = thinkingInput.trim()
        if (q.isEmpty() || isThinkingGenerating) return

        isThinkingGenerating = true
        thinkingResponse = null
        thinkingThoughtLog = null
        thinkingError = null

        viewModelScope.launch {
            try {
                // Guidelines say: You MUST use the gemini-3.1-pro-preview model and set thinkingLevel to ThinkingLevel.HIGH
                val result = OmniAiApiClient.generateContent(
                    model = "gemini-3.1-pro-preview",
                    prompt = q,
                    thinkingLevel = "HIGH"
                )
                thinkingResponse = result.text
                thinkingThoughtLog = result.thoughtProcess
                if (result.text == null && result.thoughtProcess == null) {
                    thinkingError = "Model did not return response content."
                }
            } catch (e: Exception) {
                thinkingError = e.localizedMessage ?: "Unknown logic exception during High Thinking model execution."
            } finally {
                isThinkingGenerating = false
            }
        }
    }

    // --- 3. IMAGE STUDIO ---
    fun runImageGeneration() {
        val p = imagePrompt.trim()
        if (p.isEmpty() || isGeneratingImage) return

        isGeneratingImage = true
        generatedImageFile = null
        imageError = null

        viewModelScope.launch {
            try {
                // Check if user is editing or creating
                val editingBytes = selectedEditImageBytes
                val editingMime = if (selectedEditImageUri != null) context.contentResolver.getType(selectedEditImageUri!!) ?: "image/jpeg" else null

                val isHighQuality = imageQualitySelected.contains("Pro")
                // Model rules from skill:
                // Create with flash-image: gemini-3.1-flash-image-preview
                // Create with pro-image: gemini-3-pro-image-preview
                val targetModel = if (isHighQuality) "gemini-3-pro-image-preview" else "gemini-3.1-flash-image-preview"

                val result = OmniAiApiClient.generateContent(
                    model = targetModel,
                    prompt = p,
                    imageSize = imageSizeSelected,
                    aspectRatio = imageAspectRatio,
                    editingImageBytes = editingBytes,
                    editingImageMimeType = editingMime
                )

                if (result.inlineDataBase64 != null) {
                    val savedFile = MediaHelpers.saveBase64ToFile(
                        context, 
                        result.inlineDataBase64, 
                        "img_gen_${System.currentTimeMillis()}.png"
                    )
                    if (savedFile != null) {
                        generatedImageFile = savedFile
                    } else {
                        imageError = "Failed to write generated image to device cache."
                    }
                } else if (result.text != null) {
                    // Fallback description
                    imageError = "Model produced explanation but no image payload: ${result.text}"
                } else {
                    imageError = "Model returned empty generation block."
                }
            } catch (e: Exception) {
                imageError = e.localizedMessage ?: "Image generation pipeline exception."
            } finally {
                isGeneratingImage = false
            }
        }
    }

    fun clearEditImage() {
        selectedEditImageUri = null
        selectedEditImageBytes = null
        isEditingImage = false
    }

    // --- 4. VIDEO (VEO) ---
    fun triggerVeoJob() {
        val p = videoPrompt.trim()
        if (p.isEmpty() || isSubmittingVideoJob) return

        isSubmittingVideoJob = true
        videoError = null

        viewModelScope.launch {
            try {
                val operationName = OmniAiApiClient.startVeoGeneration(
                    prompt = p,
                    aspectRatio = videoAspectRatio,
                    uploadedImageBytes = selectedAnimateImageBytes,
                    uploadedImageMime = if (selectedAnimateImageUri != null) context.contentResolver.getType(selectedAnimateImageUri!!) else null
                )

                val newJob = VeoJob(
                    operationId = operationName,
                    prompt = p,
                    isImageToVideo = selectedAnimateImageBytes != null,
                    status = "PENDING"
                )
                
                veoJobs.add(0, newJob)
                startPollingVeoJob(newJob)
                
                // Clear any animate photo selection
                selectedAnimateImageUri = null
                selectedAnimateImageBytes = null
            } catch (e: Exception) {
                videoError = e.localizedMessage ?: "Veo task dispatch exception."
            } finally {
                isSubmittingVideoJob = false
            }
        }
    }

    private fun startPollingVeoJob(job: VeoJob) {
        val jobName = job.operationId
        // Cancel any existing active jobs with the same name
        pollingJobs[jobName]?.cancel()

        val pJob = viewModelScope.launch {
            job.status = "POLLING"
            var elapsedSeconds = 0
            val timeoutSeconds = 300 // 5 minutes max

            while (elapsedSeconds < timeoutSeconds) {
                delay(4000) // Poll every 4 seconds
                elapsedSeconds += 4
                job.progressSeconds = elapsedSeconds

                try {
                    val pollResponse = OmniAiApiClient.pollOperation(jobName)
                    if (pollResponse.done) {
                        if (pollResponse.error != null) {
                            job.status = "FAILED"
                            job.errorMessage = pollResponse.error
                        } else {
                            job.status = "COMPLETED"
                            job.videoUrl = pollResponse.videoUrl
                            
                            // Save base64 video to storage if present, or use the direct URI
                            if (pollResponse.videoBase64 != null) {
                                val savedFile = MediaHelpers.saveBase64ToFile(
                                    context,
                                    pollResponse.videoBase64,
                                    "veo_${System.currentTimeMillis()}.mp4"
                                )
                                job.videoPath = savedFile?.absolutePath
                            }
                        }
                        break
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Poller fetched connection fault", e)
                }
            }

            if (job.status == "POLLING") {
                job.status = "FAILED"
                job.errorMessage = "Timed out waiting for Veo server video generation."
            }
            // Trigger Compose update by replacing list item
            val index = veoJobs.indexOfFirst { it.operationId == job.operationId }
            if (index != -1) {
                veoJobs[index] = job.copy()
            }
        }
        pollingJobs[jobName] = pJob
    }

    fun clearAnimateImage() {
        selectedAnimateImageUri = null
        selectedAnimateImageBytes = null
    }

    // --- 5. SPEECH STUDIO (TTS) ---
    fun runSpeechGeneration() {
        val textStr = speechInput.trim()
        if (textStr.isEmpty() || isGeneratingSpeech) return

        isGeneratingSpeech = true
        generatedSpeechFile = null
        speechError = null
        MediaHelpers.stopAudio()
        isSpeechPlaying = false

        viewModelScope.launch {
            try {
                // TTS model specified: gemini-3.1-flash-tts-preview
                val result = OmniAiApiClient.generateContent(
                    model = "gemini-3.1-flash-tts-preview",
                    prompt = "Say clearly: $textStr",
                    prebuiltVoiceName = voiceSelected
                )

                if (result.inlineDataBase64 != null) {
                    val file = MediaHelpers.saveBase64ToFile(
                        context,
                        result.inlineDataBase64,
                        "speech_tts_${System.currentTimeMillis()}.mp3"
                    )
                    if (file != null) {
                        generatedSpeechFile = file
                    } else {
                        speechError = "Could not save speech file to disk cache."
                    }
                } else {
                    speechError = "Model did not output any audio inlineData: ${result.text ?: "Empty responses."}"
                }
            } catch (e: Exception) {
                speechError = e.localizedMessage ?: "TTS audio speech engine error."
            } finally {
                isGeneratingSpeech = false
            }
        }
    }

    fun toggleSpeechPlayback() {
        val file = generatedSpeechFile ?: return
        if (isSpeechPlaying) {
            MediaHelpers.stopAudio()
            isSpeechPlaying = false
        } else {
            isSpeechPlaying = true
            MediaHelpers.playAudio(
                file = file,
                onCompletion = {
                    isSpeechPlaying = false
                },
                onError = { err ->
                    speechError = err
                    isSpeechPlaying = false
                }
            )
        }
    }

    // --- 6. MUSIC STUDIO (LYRIA) ---
    fun runMusicGeneration() {
        val p = musicPrompt.trim()
        if (p.isEmpty() || isGeneratingMusic) return

        isGeneratingMusic = true
        generatedMusicFile = null
        musicError = null
        MediaHelpers.stopAudio()
        isMusicPlaying = false

        viewModelScope.launch {
            try {
                // Models from specs: short clips -> lyria-3-clip-preview, track -> lyria-3-pro-preview
                val selectedModel = if (isShortMusicClip) "lyria-3-clip-preview" else "lyria-3-pro-preview"
                
                val result = OmniAiApiClient.generateContent(
                    model = selectedModel,
                    prompt = p,
                    isShortMusicClip = isShortMusicClip,
                    isFullMusicTrack = !isShortMusicClip
                )

                if (result.inlineDataBase64 != null) {
                    val file = MediaHelpers.saveBase64ToFile(
                        context,
                        result.inlineDataBase64,
                        "lyria_music_${System.currentTimeMillis()}.mp3"
                    )
                    if (file != null) {
                        generatedMusicFile = file
                    } else {
                        musicError = "Could not write generated music block to local storage cache."
                    }
                } else {
                    musicError = "Model did not output any audio block: ${result.text ?: "Empty."}"
                }
            } catch (e: Exception) {
                musicError = e.localizedMessage ?: "Lyria Music Synthesis Pipeline crash."
            } finally {
                isGeneratingMusic = false
            }
        }
    }

    fun toggleMusicPlayback() {
        val file = generatedMusicFile ?: return
        if (isMusicPlaying) {
            MediaHelpers.stopAudio()
            isMusicPlaying = false
        } else {
            isMusicPlaying = true
            MediaHelpers.playAudio(
                file = file,
                onCompletion = {
                    isMusicPlaying = false
                },
                onError = { err ->
                    musicError = err
                    isMusicPlaying = false
                }
            )
        }
    }

    // --- 7. MEDIA SCANNER (VISION/ANALYSIS) ---
    fun runImageAnalysis() {
        val bytes = scannedImageBytes
        if (bytes == null || isScanningImage) {
            imageScannerError = "Please select or import a valid image first."
            return
        }

        isScanningImage = true
        imageAnalysisResult = null
        imageScannerError = null

        viewModelScope.launch {
            try {
                // Analyze using gemini-3.1-pro-preview
                val mime = if (scannedImageUri != null) context.contentResolver.getType(scannedImageUri!!) ?: "image/jpeg" else "image/jpeg"
                Log.d(TAG, "Starting image analysis... size: ${bytes.size} bytes, mime: $mime")
                val result = OmniAiApiClient.generateContent(
                    model = "gemini-3.1-pro-preview",
                    prompt = imageAnalysisPrompt,
                    imageBytes = bytes,
                    imageMimeType = mime
                )
                imageAnalysisResult = result.text ?: "Analysis succeeded but returned empty text."
            } catch (e: Exception) {
                imageScannerError = e.localizedMessage ?: "Analysis pipeline failed."
            } finally {
                isScanningImage = false
            }
        }
    }

    fun clearScannedImage() {
        scannedImageUri = null
        scannedImageBytes = null
        imageAnalysisResult = null
    }

    fun loadSelectedImage(uri: Uri, isForAnalysis: Boolean, isForEditing: Boolean, isForAnimation: Boolean) {
        viewModelScope.launch {
            try {
                val bytes = readUriBytes(uri)
                if (isForAnalysis) {
                    scannedImageUri = uri
                    scannedImageBytes = bytes
                } else if (isForEditing) {
                    selectedEditImageUri = uri
                    selectedEditImageBytes = bytes
                } else if (isForAnimation) {
                    selectedAnimateImageUri = uri
                    selectedAnimateImageBytes = bytes
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load picked image", e)
            }
        }
    }

    fun runVideoAnalysis() {
        val bytes = scannedVideoBytes
        if (bytes == null || isScanningVideo) {
            videoScannerError = "Please import a valid, small video first."
            return
        }

        isScanningVideo = true
        videoAnalysisResult = null
        videoScannerError = null

        viewModelScope.launch {
            try {
                // Video analysis using gemini-3.1-pro-preview
                val mime = if (scannedVideoUri != null) context.contentResolver.getType(scannedVideoUri!!) ?: "video/mp4" else "video/mp4"
                
                val result = OmniAiApiClient.generateContent(
                    model = "gemini-3.1-pro-preview",
                    prompt = videoAnalysisPrompt,
                    videoBytes = bytes,
                    videoMimeType = mime
                )
                videoAnalysisResult = result.text ?: "Video analysis returned no summary report."
            } catch (e: Exception) {
                videoScannerError = e.localizedMessage ?: "Detailed Video analyzer parser fault."
            } finally {
                isScanningVideo = false
            }
        }
    }

    fun clearScannedVideo() {
        scannedVideoUri = null
        scannedVideoBytes = null
        videoAnalysisResult = null
    }

    fun loadSelectedVideo(uri: Uri) {
        viewModelScope.launch {
            try {
                // Ensure safety: limit video analysis upload size to prevent OOM
                val bytesObj = readUriBytes(uri)
                val safeBytes = if (bytesObj.size > 8 * 1024 * 1024) {
                    // Truncate or crop or print a warning
                    Log.w(TAG, "Video file is very large (${bytesObj.size} bytes). Truncating to 8MB for analysis safety.")
                    bytesObj.copyOfRange(0, 8 * 1024 * 1024)
                } else {
                    bytesObj
                }
                scannedVideoUri = uri
                scannedVideoBytes = safeBytes
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load picked video", e)
            }
        }
    }

    // --- 8. AUDIO TRANSCRIBER ---
    fun toggleRecording() {
        if (isRecordingAudio) {
            // Stop recording
            val file = MediaHelpers.stopRecording()
            isRecordingAudio = false
            recordTimerJob?.cancel()
            if (file != null && file.exists()) {
                recordedAudioFile = file
                triggerAudioTranscription()
            } else {
                transcriberError = "No recorded audio sample found."
            }
        } else {
            // Start recording
            recordedAudioFile = null
            transcribedTextResult = null
            transcriberError = null
            recordingTimerSeconds = 0
            
            MediaHelpers.startRecording(
                context = context,
                onStartSuccess = {
                    isRecordingAudio = true
                    startRecordTimer()
                },
                onFailure = { err ->
                    transcriberError = "Microphone error: $err"
                }
            )
        }
    }

    private fun startRecordTimer() {
        recordTimerJob?.cancel()
        recordTimerJob = viewModelScope.launch {
            while (isRecordingAudio) {
                delay(1000)
                recordingTimerSeconds++
            }
        }
    }

    fun triggerAudioTranscription() {
        val file = recordedAudioFile ?: return
        if (!file.exists() || isTranscribingSource) return

        isTranscribingSource = true
        transcribedTextResult = null
        transcriberError = null

        viewModelScope.launch {
            try {
                val bytes = file.readBytes()
                Log.d(TAG, "Running speech transcription via gemini-3.5-flash... Size: ${bytes.size} bytes")
                
                // Transcribe using gemini-3.5-flash
                val result = OmniAiApiClient.generateContent(
                    model = "gemini-3.5-flash",
                    prompt = "Please transcribe this microphone audio recording completely and accurately. Output only the transcription, verbatim.",
                    audioBytes = bytes,
                    audioMimeType = "audio/mp4" // standard MediaRecorder format
                )
                transcribedTextResult = result.text ?: "Transcription complete, but no text outputted."
            } catch (e: Exception) {
                transcriberError = e.localizedMessage ?: "Transcription failed due to model service exception."
            } finally {
                isTranscribingSource = false
            }
        }
    }

    // --- CORE URI UTILS ---
    private fun readUriBytes(uri: Uri): ByteArray {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 4096
        val buffer = ByteArray(bufferSize)
        inputStream?.use { stream ->
            var len: Int
            while (stream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
        }
        return byteBuffer.toByteArray()
    }
}
