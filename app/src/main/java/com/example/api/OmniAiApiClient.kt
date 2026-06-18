package com.example.api

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GeminiResult(
    val text: String?,
    val inlineMimeType: String?,
    val inlineDataBase64: String?,
    val thoughtProcess: String? = null
)

data class VeoOperationResult(
    val name: String,
    val done: Boolean,
    val videoUrl: String? = null,
    val videoBase64: String? = null,
    val error: String? = null
)

object OmniAiApiClient {
    private const val TAG = "OmniAiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key == "MY_GEMINI_API_KEY" || key.isEmpty()) {
            // Under simulation or actual injection, return key.
            key
        } else {
            key
        }
    }

    /**
     * Call generateContent on a specific model using raw OkHttp and org.json.JSONObject.
     * This is robust and avoids potential Moshi or Kotlin Serialization version conflicts.
     */
    suspend fun generateContent(
        model: String,
        prompt: String,
        imageBytes: ByteArray? = null,
        imageMimeType: String? = null,
        videoBytes: ByteArray? = null,
        videoMimeType: String? = null,
        audioBytes: ByteArray? = null,
        audioMimeType: String? = null,
        thinkingLevel: String? = null,
        imageSize: String? = null,
        aspectRatio: String? = null,
        prebuiltVoiceName: String? = null,
        isShortMusicClip: Boolean? = null,
        isFullMusicTrack: Boolean? = null,
        editingImageBytes: ByteArray? = null,
        editingImageMimeType: String? = null
    ): GeminiResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val url = "${BASE_URL}v1beta/models/$model:generateContent?key=$apiKey"

        try {
            val requestBodyJson = JSONObject()
            
            // Contents structure
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()

            // 1. Plain text prompt part
            val promptPart = JSONObject().put("text", prompt)
            partsArray.put(promptPart)

            // 2. Multimodal attachments
            if (imageBytes != null && imageMimeType != null) {
                val base64Data = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                val inlineDataObj = JSONObject()
                    .put("mimeType", imageMimeType)
                    .put("data", base64Data)
                val imagePart = JSONObject().put("inlineData", inlineDataObj)
                partsArray.put(imagePart)
            }

            if (videoBytes != null && videoMimeType != null) {
                val base64Data = Base64.encodeToString(videoBytes, Base64.NO_WRAP)
                val inlineDataObj = JSONObject()
                    .put("mimeType", videoMimeType)
                    .put("data", base64Data)
                val videoPart = JSONObject().put("inlineData", inlineDataObj)
                partsArray.put(videoPart)
            }

            if (audioBytes != null && audioMimeType != null) {
                val base64Data = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
                val inlineDataObj = JSONObject()
                    .put("mimeType", audioMimeType)
                    .put("data", base64Data)
                val audioPart = JSONObject().put("inlineData", inlineDataObj)
                partsArray.put(audioPart)
            }

            if (editingImageBytes != null && editingImageMimeType != null) {
                val base64Data = Base64.encodeToString(editingImageBytes, Base64.NO_WRAP)
                val inlineDataObj = JSONObject()
                    .put("mimeType", editingImageMimeType)
                    .put("data", base64Data)
                val editingImagePart = JSONObject().put("inlineData", inlineDataObj)
                partsArray.put(editingImagePart)
            }

            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestBodyJson.put("contents", contentsArray)

            // 3. Configurations
            val generationConfig = JSONObject()
            var hasConfig = false

            // Modalities (for image, audio, or music generation)
            if (model.contains("tts") || model.contains("lyria") || isShortMusicClip == true || isFullMusicTrack == true) {
                val modalities = JSONArray().put("AUDIO")
                generationConfig.put("responseModalities", modalities)
                hasConfig = true
            } else if (model.contains("image") || model.contains("imagen") || imageSize != null) {
                val modalities = JSONArray().put("TEXT").put("IMAGE")
                generationConfig.put("responseModalities", modalities)
                hasConfig = true
            }

            // High Thinking mode
            if (thinkingLevel != null) {
                // Must be "HIGH" or "LOW" etc.
                val thinkingConfig = JSONObject().put("thinkingLevel", thinkingLevel)
                generationConfig.put("thinkingConfig", thinkingConfig)
                hasConfig = true
                // Note: Guidelines say: Do not set maxOutputTokens for high thinking level
            }

            // Image Generation configs
            if (imageSize != null || aspectRatio != null) {
                val imageConfig = JSONObject()
                if (imageSize != null) {
                    imageConfig.put("imageSize", imageSize) // e.g. "1K", "2K", "4K"
                }
                if (aspectRatio != null) {
                    imageConfig.put("aspectRatio", aspectRatio) // e.g. "1:1", "16:9"
                }
                generationConfig.put("imageConfig", imageConfig)
                hasConfig = true
            }

            // Text to speech configs
            if (prebuiltVoiceName != null) {
                val speechConfig = JSONObject().put("voiceConfig", 
                    JSONObject().put("prebuiltVoiceConfig", 
                        JSONObject().put("voiceName", prebuiltVoiceName)
                    )
                )
                generationConfig.put("speechConfig", speechConfig)
                hasConfig = true
            }

            if (hasConfig) {
                requestBodyJson.put("generationConfig", generationConfig)
            }

            val requestBodyStr = requestBodyJson.toString()
            Log.d(TAG, "Request payload: $requestBodyStr")

            val request = Request.Builder()
                .url(url)
                .post(requestBodyStr.toRequestBody(jsonMediaType))
                .header("Content-Type", "application/json")
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API call non-successful: Code ${response.code}, Body: $errBody")
                    throw IOException("HTTP ${response.code}: $errBody")
                }

                val bodyStr = response.body?.string() ?: ""
                Log.d(TAG, "Response footprint length: ${bodyStr.length}")

                val rootObj = JSONObject(bodyStr)
                val candidates = rootObj.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext GeminiResult("No models generated any content.", null, null, null)
                }

                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content") ?: return@withContext GeminiResult("Empty candidate content.", null, null, null)
                val parts = content.optJSONArray("parts") ?: return@withContext GeminiResult("Empty content parts.", null, null, null)

                var responseText = ""
                var speechOrMusicBase64: String? = null
                var speechOrMusicMime: String? = null
                var imageBase64: String? = null
                var imageMime: String? = null
                var thoughtLog: String? = null

                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    
                    // Standard text output
                    if (part.has("text")) {
                        val txt = part.getString("text")
                        
                        // Check if this part of the model's output is marked as a thought process
                        val hasThoughtKey = part.optBoolean("thought", false)
                        if (hasThoughtKey) {
                            thoughtLog = if (thoughtLog == null) txt else thoughtLog + "\n" + txt
                        } else {
                            responseText += txt
                        }
                    }

                    // Inline data block (returns generated image/audio/etc.)
                    if (part.has("inlineData")) {
                        val inlineData = part.getJSONObject("inlineData")
                        val mime = inlineData.optString("mimeType", "")
                        val b64 = inlineData.optString("data", "")

                        if (mime.startsWith("image/")) {
                            imageBase64 = b64
                            imageMime = mime
                        } else if (mime.startsWith("audio/")) {
                            speechOrMusicBase64 = b64
                            speechOrMusicMime = mime
                        }
                    }
                }

                // If no specific thought log was tagged, but we set thinkingLevel = "HIGH", 
                // sometimes the API will group thought blocks or we can parse them. 
                // Return accumulated values.
                GeminiResult(
                    text = responseText.trim().ifEmpty { null },
                    inlineMimeType = imageMime ?: speechOrMusicMime,
                    inlineDataBase64 = imageBase64 ?: speechOrMusicBase64,
                    thoughtProcess = thoughtLog
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "generateContent Failed", e)
            throw e
        }
    }

    /**
     * Trigger Veo Video Generation (veo-3.1-fast-generate-preview)
     * Returns the Operation Name (LRO) to be polled.
     */
    suspend fun startVeoGeneration(
        prompt: String,
        aspectRatio: String, // "16:9" or "9:16"
        uploadedImageBytes: ByteArray? = null,
        uploadedImageMime: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val url = "${BASE_URL}v1beta/models/veo-3.1-fast-generate-preview:generateVideos?key=$apiKey"

        try {
            val requestBodyJson = JSONObject()
            requestBodyJson.put("prompt", prompt)

            val configObj = JSONObject()
            configObj.put("numberOfVideos", 1)
            configObj.put("resolution", "720p") // standard fast resolution
            configObj.put("aspectRatio", aspectRatio)
            requestBodyJson.put("config", configObj)

            // If drawing animation from static photo
            if (uploadedImageBytes != null && uploadedImageMime != null) {
                val base64Data = Base64.encodeToString(uploadedImageBytes, Base64.NO_WRAP)
                val inlineDataObj = JSONObject()
                    .put("mimeType", uploadedImageMime)
                    .put("data", base64Data)
                val imageObj = JSONObject().put("inlineData", inlineDataObj)
                
                // Veo accepts image-to-video input under 'image' property at top level
                requestBodyJson.put("image", imageObj)
            }

            val requestBodyStr = requestBodyJson.toString()
            Log.d(TAG, "Veo Request Payload: $requestBodyStr")

            val request = Request.Builder()
                .url(url)
                .post(requestBodyStr.toRequestBody(jsonMediaType))
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    throw IOException("Veo HTTP ${response.code}: $bodyStr")
                }
                
                val root = JSONObject(bodyStr)
                val operationName = root.optString("name", "")
                if (operationName.isEmpty()) {
                    throw IOException("Failed to obtain operation name from Veo. Response: $bodyStr")
                }
                operationName
            }
        } catch (e: Exception) {
            Log.e(TAG, "startVeoGeneration Failed", e)
            throw e
        }
    }

    /**
     * Poll a Long Running Operation (LRO) status.
     * Works for Veo video generation operations.
     */
    suspend fun pollOperation(operationName: String): VeoOperationResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        // operationName comes in the form "operations/some_id"
        val url = "${BASE_URL}v1beta/$operationName?key=$apiKey"

        try {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    throw IOException("Operation Poll HTTP ${response.code}: $bodyStr")
                }

                val root = JSONObject(bodyStr)
                val done = root.optBoolean("done", false)
                
                if (!done) {
                    return@withContext VeoOperationResult(name = operationName, done = false)
                }

                // Completed!
                val errorObj = root.optJSONObject("error")
                if (errorObj != null) {
                    val errMsg = errorObj.optString("message", "Unknown error")
                    return@withContext VeoOperationResult(name = operationName, done = true, error = errMsg)
                }

                val responseObj = root.optJSONObject("response")
                if (responseObj != null) {
                    // Try parsing generatedVideos
                    val generatedVideos = responseObj.optJSONArray("generatedVideos")
                    if (generatedVideos != null && generatedVideos.length() > 0) {
                        val videoObj = generatedVideos.getJSONObject(0)
                        
                        // Check if videoUri is returned
                        val videoUri = videoObj.optString("videoUri", "")
                        
                        // Check if inline file/data is returned
                        var b64Data: String? = null
                        val videoBytesObj = videoObj.optJSONObject("video")
                        if (videoBytesObj != null) {
                            val inlineData = videoBytesObj.optJSONObject("inlineData")
                            if (inlineData != null) {
                                b64Data = inlineData.optString("data", "")
                            }
                        }

                        return@withContext VeoOperationResult(
                            name = operationName,
                            done = true,
                            videoUrl = videoUri.ifEmpty { null },
                            videoBase64 = b64Data
                        )
                    }
                }

                // Alternative: some platforms pack content directly or use different keys
                return@withContext VeoOperationResult(
                    name = operationName,
                    done = true,
                    error = "No videos were found in the operation response."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "pollOperation Failed", e)
            return@withContext VeoOperationResult(name = operationName, done = true, error = e.localizedMessage)
        }
    }
}
