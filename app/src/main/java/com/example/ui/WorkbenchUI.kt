package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.WorkbenchScreen
import com.example.ChatMessage
import com.example.VeoJob
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun OmniAiWorkbenchMain(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize().background(CyberBg)) {
        val isWideScreen = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (isWideScreen) {
                WorkbenchNavigationRail(
                    currentScreen = viewModel.currentScreen,
                    onScreenSelected = { viewModel.currentScreen = it }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                WorkbenchTopBar()

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (viewModel.currentScreen) {
                        WorkbenchScreen.CHAT -> ChatPanel(viewModel)
                        WorkbenchScreen.THINKING -> HighThinkingPanel(viewModel)
                        WorkbenchScreen.IMAGE_STUDIO -> ImageStudioPanel(viewModel)
                        WorkbenchScreen.VIDEO_VEO -> VideoStudioPanel(viewModel)
                        WorkbenchScreen.SPEECH_TTS -> SpeechStudioPanel(viewModel)
                        WorkbenchScreen.MUSIC_STUDIO -> MusicStudioPanel(viewModel)
                        WorkbenchScreen.MEDIA_SCANNER -> MediaScannerPanel(viewModel)
                        WorkbenchScreen.ABOUT -> AboutPanel()
                    }
                }

                if (!isWideScreen) {
                    WorkbenchBottomNavigationBar(
                        currentScreen = viewModel.currentScreen,
                        onScreenSelected = { viewModel.currentScreen = it }
                    )
                }
            }
        }
    }
}

@Composable
fun WorkbenchTopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberBg)
            .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(CyberSecondary, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OPERATIONAL: FIDELITY",
                    color = CyberSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp
                )
            }
            Text(
                text = "v4.0.2_TERMINAL",
                color = CyberTextSecondary.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "OMNIAI WORKBENCH",
            color = Color.White,
            fontWeight = FontWeight.Light,
            fontSize = 24.sp,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = "DIRECT ACCESS CHANNEL — SESSION #4821",
            color = CyberTextSecondary.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun WorkbenchNavigationRail(
    currentScreen: WorkbenchScreen,
    onScreenSelected: (WorkbenchScreen) -> Unit
) {
    NavigationRail(
        containerColor = CyberCard,
        modifier = Modifier.fillMaxHeight().border(1.dp, CyberCardLight)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        NavigationRailItem(
            icon = { Icon(Icons.Default.Send, contentDescription = "Chat") },
            label = { Text("Chat", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.CHAT,
            onClick = { onScreenSelected(WorkbenchScreen.CHAT) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Thinking") },
            label = { Text("Thinking", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.THINKING,
            onClick = { onScreenSelected(WorkbenchScreen.THINKING) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.Edit, contentDescription = "Image Studio") },
            label = { Text("Images", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.IMAGE_STUDIO,
            onClick = { onScreenSelected(WorkbenchScreen.IMAGE_STUDIO) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Video Veo") },
            label = { Text("Videos", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.VIDEO_VEO,
            onClick = { onScreenSelected(WorkbenchScreen.VIDEO_VEO) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.Refresh, contentDescription = "TTS") },
            label = { Text("Speech", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.SPEECH_TTS,
            onClick = { onScreenSelected(WorkbenchScreen.SPEECH_TTS) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.Share, contentDescription = "Music") },
            label = { Text("Music", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.MUSIC_STUDIO,
            onClick = { onScreenSelected(WorkbenchScreen.MUSIC_STUDIO) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Scanner") },
            label = { Text("Scanner", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.MEDIA_SCANNER,
            onClick = { onScreenSelected(WorkbenchScreen.MEDIA_SCANNER) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationRailItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "About") },
            label = { Text("About", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.ABOUT,
            onClick = { onScreenSelected(WorkbenchScreen.ABOUT) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )
    }
}

@Composable
fun WorkbenchBottomNavigationBar(
    currentScreen: WorkbenchScreen,
    onScreenSelected: (WorkbenchScreen) -> Unit
) {
    NavigationBar(
        containerColor = CyberCard,
        modifier = Modifier.border(1.dp, CyberCardLight)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Send, contentDescription = "Chat") },
            label = { Text("Chat", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.CHAT,
            onClick = { onScreenSelected(WorkbenchScreen.CHAT) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Thinking") },
            label = { Text("Think", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.THINKING,
            onClick = { onScreenSelected(WorkbenchScreen.THINKING) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Edit, contentDescription = "Images") },
            label = { Text("Images", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.IMAGE_STUDIO,
            onClick = { onScreenSelected(WorkbenchScreen.IMAGE_STUDIO) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Videos") },
            label = { Text("Videos", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = currentScreen == WorkbenchScreen.VIDEO_VEO,
            onClick = { onScreenSelected(WorkbenchScreen.VIDEO_VEO) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "More") },
            label = { Text("More", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
            selected = (currentScreen == WorkbenchScreen.MEDIA_SCANNER || currentScreen == WorkbenchScreen.MUSIC_STUDIO || currentScreen == WorkbenchScreen.SPEECH_TTS || currentScreen == WorkbenchScreen.ABOUT),
            onClick = {
                if (currentScreen == WorkbenchScreen.SPEECH_TTS) {
                    onScreenSelected(WorkbenchScreen.MUSIC_STUDIO)
                } else if (currentScreen == WorkbenchScreen.MUSIC_STUDIO) {
                    onScreenSelected(WorkbenchScreen.MEDIA_SCANNER)
                } else if (currentScreen == WorkbenchScreen.MEDIA_SCANNER) {
                    onScreenSelected(WorkbenchScreen.ABOUT)
                } else {
                    onScreenSelected(WorkbenchScreen.SPEECH_TTS)
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberPrimary,
                unselectedIconColor = CyberTextSecondary,
                selectedTextColor = CyberPrimary,
                unselectedTextColor = CyberTextSecondary,
                indicatorColor = CyberCardLight
            )
        )
    }
}

@Composable
fun ChatPanel(viewModel: MainViewModel) {
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.chatMessages.size) {
        if (viewModel.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.chatMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "FAST CHAT ENGINE [gemini-3.1-flash-lite]",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CyberCard)
                .border(1.dp, Color(0x13FFFFFF), RoundedCornerShape(24.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.chatMessages) { msg ->
                ChatBubble(msg)
            }
            if (viewModel.isChatSending) {
                item {
                    ChatLoadingIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = listOf("Explain Quantum Computing", "Write a joke", "Draft a workout")
            suggestions.forEach { prompt ->
                Box(
                    modifier = Modifier
                        .background(CyberCardLight, CircleShape)
                        .clickable { viewModel.chatInput = prompt }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(text = prompt, color = CyberPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberCardLight, CircleShape)
                .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.chatInput,
                onValueChange = { viewModel.chatInput = it },
                placeholder = { Text("Ask anything instantly...", color = CyberTextSecondary.copy(alpha = 0.5f)) },
                modifier = Modifier.weight(1f),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = CyberTextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            Button(
                onClick = { viewModel.sendChatMessage() },
                enabled = viewModel.chatInput.isNotEmpty() && !viewModel.isChatSending,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberPrimary,
                    contentColor = Color.Black,
                    disabledContainerColor = CyberCardLight
                ),
                shape = CircleShape,
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Send prompt",
                    tint = if (viewModel.chatInput.isNotEmpty() && !viewModel.isChatSending) Color.Black else CyberTextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
    val bg = if (msg.isUser) CyberCardLight else CyberBg
    val borderColors = if (msg.isUser) CyberPrimary else CyberSecondary

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(bg, RoundedCornerShape(12.dp))
                .border(1.dp, borderColors.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = msg.text,
                color = CyberTextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        Text(
            text = if (msg.isUser) "USER" else "GEMINI",
            fontSize = 9.sp,
            color = CyberTextSecondary,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ChatLoadingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = CyberSecondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "OmniFlash generating burst response...",
            fontSize = 11.sp,
            color = CyberTextSecondary,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun HighThinkingPanel(viewModel: MainViewModel) {
    var showThoughts by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "ADVANCED REASONING SYSTEM [gemini-3.1-pro-preview]",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Operates with high-level background thought expansion. Perfect for STEM and algorithms.",
            fontSize = 11.sp,
            color = CyberTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = viewModel.thinkingInput,
            onValueChange = { viewModel.thinkingInput = it },
            placeholder = { Text("Enter highly complex queries...", color = CyberTextSecondary.copy(alpha = 0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberPrimary.copy(alpha = 0.6f),
                unfocusedBorderColor = CyberCardLight,
                focusedContainerColor = CyberCard,
                unfocusedContainerColor = CyberCard
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.runHighThinkingExplanation() },
            enabled = viewModel.thinkingInput.isNotEmpty() && !viewModel.isThinkingGenerating,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black)
        ) {
            if (viewModel.isThinkingGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("INTELLIGENCE ENGINE ACTIVE...", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENGAGE COGNITIVE MODEL", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.thinkingError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberError.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CyberError.copy(alpha = 0.1f))
            ) {
                Text(
                    text = "FAULT: ${viewModel.thinkingError}",
                    color = CyberError,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (viewModel.thinkingThoughtLog != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color(0x13FFFFFF), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showThoughts = !showThoughts },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "BACKGROUND THOUGHT LOGS",
                                        color = CyberSecondary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = if (showThoughts) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = CyberSecondary
                                )
                            }

                            if (showThoughts) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = viewModel.thinkingThoughtLog ?: "",
                                    color = CyberTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.thinkingResponse != null) {
                item {
                    Text(
                        text = "SYNTHESIZED CONCLUSION",
                        fontSize = 11.sp,
                        color = CyberPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Text(
                            text = viewModel.thinkingResponse ?: "",
                            color = CyberTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageStudioPanel(viewModel: MainViewModel) {
    var isEditTabActive by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.loadSelectedImage(uri, isForAnalysis = false, isForEditing = true, isForAnimation = false)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "IMAGE SYNTHESIZER [Gemini 3 Pro / 3.1 Flash Image]",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(CyberCard, RoundedCornerShape(8.dp))
                .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                    .background(if (!isEditTabActive) CyberPrimary else Color.Transparent)
                    .clickable { isEditTabActive = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CREATE NEW",
                    color = if (!isEditTabActive) Color.Black else CyberTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                    .background(if (isEditTabActive) CyberPrimary else Color.Transparent)
                    .clickable { isEditTabActive = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EDIT IMAGE",
                    color = if (isEditTabActive) Color.Black else CyberTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ASPECT RATIO", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                        val ratios = listOf("1:1", "16:9", "4:3", "9:16")
                        var expanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCard, RoundedCornerShape(4.dp))
                                .border(1.dp, CyberCardLight, RoundedCornerShape(4.dp))
                                .clickable { expanded = true }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(viewModel.imageAspectRatio, color = CyberPrimary, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = CyberPrimary)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                ratios.forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(r) },
                                        onClick = {
                                            viewModel.imageAspectRatio = r
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1.5f)) {
                        Text("ENGINE CAPABILITY", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                        val models = listOf(
                            "High Quality (Gemini 3 Pro Image)",
                            "Fast Preview (Gemini 3.1 Flash Image)"
                        )
                        var expanded by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCard, RoundedCornerShape(4.dp))
                                .border(1.dp, CyberCardLight, RoundedCornerShape(4.dp))
                                .clickable { expanded = true }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(viewModel.imageQualitySelected, color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = CyberSecondary)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                models.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m) },
                                        onClick = {
                                            viewModel.imageQualitySelected = m
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (viewModel.imageQualitySelected.contains("Pro")) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("IMAGE RESOLUTION (PRO EXCLUSIVE)", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val sizes = listOf("1K", "2K", "4K")
                            sizes.forEach { size ->
                                val selected = viewModel.imageSizeSelected == size
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (selected) CyberPrimary else CyberCard, RoundedCornerShape(4.dp))
                                        .border(1.dp, if (selected) CyberPrimary else CyberCardLight, RoundedCornerShape(4.dp))
                                        .clickable { viewModel.imageSizeSelected = size }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(size, color = if (selected) Color.Black else CyberTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (isEditTabActive) {
                item {
                    Text("BASE PHOTO FOR EDITING", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                    
                    if (viewModel.selectedEditImageUri == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp))
                                .background(CyberCard)
                                .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("SELECT IMAGE TO EDIT", color = CyberPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("Pick PNG or JPEG image file", color = CyberTextSecondary, fontSize = 9.sp)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCard, RoundedCornerShape(8.dp))
                                .border(1.dp, CyberSecondary, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = viewModel.selectedEditImageUri,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SOURCE IMAGE LOADED", color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Size: ${(viewModel.selectedEditImageBytes?.size ?: 0) / 1024} KB", color = CyberTextSecondary, fontSize = 10.sp)
                            }
                            IconButton(onClick = { viewModel.clearEditImage() }) {
                                Icon(Icons.Default.Warning, contentDescription = "Clear", tint = CyberError)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Text(
                    text = if (isEditTabActive) "EDIT INSTRUCTIONS / PROMPT" else "GENERATION PROMPT",
                    fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace
                )
                OutlinedTextField(
                    value = viewModel.imagePrompt,
                    onValueChange = { viewModel.imagePrompt = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberCardLight,
                        focusedContainerColor = CyberCard,
                        unfocusedContainerColor = CyberCard
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.runImageGeneration() },
                    enabled = viewModel.imagePrompt.isNotEmpty() && !viewModel.isGeneratingImage,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black)
                ) {
                    if (viewModel.isGeneratingImage) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SYNTHESIZING IMAGE PIXELS...", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = if (isEditTabActive) "EXECUTE IMAGE MODIFICATION" else "SYNTHESIZE ARTWORK",
                            fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (viewModel.imageError != null) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberError.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = "ERROR: ${viewModel.imageError}",
                            color = CyberError,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (viewModel.generatedImageFile != null) {
                item {
                    Text(
                        text = "SYNTHESIZED RAW MATRIX OUT",
                        fontSize = 11.sp,
                        color = CyberSecondary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberSecondary, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCard)
                    ) {
                        AsyncImage(
                            model = viewModel.generatedImageFile,
                            contentDescription = "Generated art preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoStudioPanel(viewModel: MainViewModel) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.loadSelectedImage(uri, isForAnalysis = false, isForEditing = false, isForAnimation = true)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "VEO 3 VIDEO STUDIO [veo-3.1-fast-generate-preview]",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Generate stunning, ultra-fast cinematic video clips from text prompts or uploaded images.",
            fontSize = 11.sp,
            color = CyberTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ASPECT RATIO", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            listOf("16:9", "9:16").forEach { ratio ->
                                val active = viewModel.videoAspectRatio == ratio
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (active) CyberPrimary else CyberCard, RoundedCornerShape(4.dp))
                                        .border(1.dp, if (active) CyberPrimary else CyberCardLight, RoundedCornerShape(4.dp))
                                        .clickable { viewModel.videoAspectRatio = ratio }
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(ratio, color = if (active) Color.Black else CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text("ANIMATE STATIC IMAGE (OPTIONAL)", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                if (viewModel.selectedAnimateImageUri == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp))
                            .background(CyberCard)
                            .clickable { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("TAP TO ADD FRAME FOR VIDEO ANIMATION", color = CyberSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberCard, RoundedCornerShape(8.dp))
                            .border(1.dp, CyberSecondary, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = viewModel.selectedAnimateImageUri,
                            contentDescription = "",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ANIMATION SEED LOADED", color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.clearAnimateImage() }) {
                            Icon(Icons.Default.Warning, contentDescription = "Clear", tint = CyberError)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text("VEO TEXT MOTION PROMPT", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                OutlinedTextField(
                    value = viewModel.videoPrompt,
                    onValueChange = { viewModel.videoPrompt = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberCardLight,
                        focusedContainerColor = CyberCard,
                        unfocusedContainerColor = CyberCard
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.triggerVeoJob() },
                    enabled = viewModel.videoPrompt.isNotEmpty() && !viewModel.isSubmittingVideoJob,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black)
                ) {
                    if (viewModel.isSubmittingVideoJob) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("QUEUE VEO GENERATION", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (viewModel.videoError != null) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberError.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = "VEO DISPATCH FAULT: ${viewModel.videoError}",
                            color = CyberError,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "VEO TASK MANAGER (${viewModel.veoJobs.size} ACTIVE/PAST)",
                    fontSize = 11.sp,
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(viewModel.veoJobs) { job ->
                VeoJobCard(job)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun VeoJobCard(job: VeoJob) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                when (job.status) {
                    "COMPLETED" -> CyberSecondary.copy(alpha = 0.4f)
                    "FAILED" -> CyberError.copy(alpha = 0.4f)
                    else -> CyberPrimary.copy(alpha = 0.4f)
                },
                RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CyberCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VEO JOB-${job.operationId.takeLast(6).uppercase()}",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                Box(
                    modifier = Modifier
                        .background(
                            when (job.status) {
                                "COMPLETED" -> CyberSecondary.copy(alpha = 0.2f)
                                "FAILED" -> CyberError.copy(alpha = 0.2f)
                                "POLLING" -> CyberPrimary.copy(alpha = 0.2f)
                                else -> CyberCardLight
                            },
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = job.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (job.status) {
                            "COMPLETED" -> CyberSecondary
                            "FAILED" -> CyberError
                            "POLLING" -> CyberPrimary
                            else -> CyberTextSecondary
                        },
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\"${job.prompt}\"",
                fontSize = 12.sp,
                color = CyberTextPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (job.isImageToVideo) {
                Text(
                    text = "⚡ Built from static Image Guide",
                    color = CyberSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (job.status == "POLLING") {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = CyberPrimary,
                    trackColor = CyberCardLight
                )
                Text(
                    text = "Elapsed: ${job.progressSeconds}s | Polling Long Running server operation...",
                    color = CyberTextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (job.status == "FAILED") {
                Text(
                    text = "Fault: ${job.errorMessage}",
                    color = CyberError,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (job.status == "COMPLETED") {
                val context = LocalContext.current
                Spacer(modifier = Modifier.height(8.dp))

                if (job.videoPath != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setVideoPath(job.videoPath)
                                    setOnPreparedListener { mp ->
                                        mp.isLooping = true
                                        start()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else if (job.videoUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberCardLight, RoundedCornerShape(4.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.videoUrl))
                                context.startActivity(intent)
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("STREAM COMPLETED VIDEO URL", color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(job.videoUrl ?: "", color = CyberTextSecondary, fontSize = 9.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpeechStudioPanel(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SPEECH EMULATION LAB [gemini-3.1-flash-tts-preview]",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Convert technical documents or creative lines into clear spoken audio.",
            fontSize = 11.sp,
            color = CyberTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Text("TARGET EMULATOR VOICE", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                val voices = listOf("Kore", "Puck", "Fenrir", "Aoede", "Charon")
                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(CyberCard, RoundedCornerShape(4.dp))
                        .border(1.dp, CyberCardLight, RoundedCornerShape(4.dp))
                        .clickable { expanded = true }
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(viewModel.voiceSelected, color = CyberPrimary, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = CyberPrimary)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        voices.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text(voice) },
                                onClick = {
                                    viewModel.voiceSelected = voice
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text("TEXT SCORE", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                OutlinedTextField(
                    value = viewModel.speechInput,
                    onValueChange = { viewModel.speechInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberCardLight,
                        focusedContainerColor = CyberCard,
                        unfocusedContainerColor = CyberCard
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.runSpeechGeneration() },
                    enabled = viewModel.speechInput.isNotEmpty() && !viewModel.isGeneratingSpeech,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black)
                ) {
                    if (viewModel.isGeneratingSpeech) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SYNTHESIZING SPEECH MATRIX...", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    } else {
                        Text("GENERATED SPOKEN WAVEFORM", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (viewModel.speechError != null) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberError.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = "EMULATOR CRASH: ${viewModel.speechError}",
                            color = CyberError,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (viewModel.generatedSpeechFile != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "SYNTHESIZED EMULATION PLAYER",
                        fontSize = 11.sp,
                        color = CyberSecondary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberSecondary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (viewModel.isSpeechPlaying) CyberSecondary else CyberCardLight,
                                            CircleShape
                                        )
                                        .clickable { viewModel.toggleSpeechPlayback() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (viewModel.isSpeechPlaying) Icons.Default.Warning else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Stop",
                                        tint = if (viewModel.isSpeechPlaying) Color.Black else CyberSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Voice Output Wavefile", color = CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(
                                        text = if (viewModel.isSpeechPlaying) "WAVE PLAYING..." else "READY FOR AUDIO PLAY",
                                        color = if (viewModel.isSpeechPlaying) CyberSecondary else CyberTextSecondary,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MusicStudioPanel(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CYBER MUSIC STUDIO [lyria-3-clip / lyria-3-pro]",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Generate synthesized tracks. Toggle clip mode (30s max) vs full-length track.",
            fontSize = 11.sp,
            color = CyberTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (viewModel.isShortMusicClip) "SHORT 30S CLIP ENGINE" else "FULL TRACK ENGINE",
                        fontSize = 11.sp,
                        color = CyberPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Switch(
                        checked = viewModel.isShortMusicClip,
                        onCheckedChange = { viewModel.isShortMusicClip = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberPrimary,
                            checkedTrackColor = CyberCardLight,
                            uncheckedThumbColor = CyberSecondary,
                            uncheckedTrackColor = CyberCardLight
                        )
                    )
                }
                Text(
                    text = if (viewModel.isShortMusicClip) "Uses lyria-3-clip-preview for rapid 30s clips." else "Uses lyria-3-pro-preview for premium tracks.",
                    color = CyberTextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                Text("MUSIC DESCRIPTION SCORE", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                OutlinedTextField(
                    value = viewModel.musicPrompt,
                    onValueChange = { viewModel.musicPrompt = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CyberTextPrimary,
                        unfocusedTextColor = CyberTextPrimary,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberCardLight,
                        focusedContainerColor = CyberCard,
                        unfocusedContainerColor = CyberCard
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.runMusicGeneration() },
                    enabled = viewModel.musicPrompt.isNotEmpty() && !viewModel.isGeneratingMusic,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black)
                ) {
                    if (viewModel.isGeneratingMusic) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("COMPILING AUDIO FREQUENCIES...", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    } else {
                        Text("GENERATE LYRIA MUSICAL TRACK", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (viewModel.musicError != null) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberError.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = "STUDIO GLITCH: ${viewModel.musicError}",
                            color = CyberError,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (viewModel.generatedMusicFile != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "COMPILED SYNTH MATRIX PLAYBACK",
                        fontSize = 11.sp,
                        color = CyberSecondary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberSecondary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = CyberCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (viewModel.isMusicPlaying) CyberSecondary else CyberCardLight,
                                        CircleShape
                                    )
                                    .clickable { viewModel.toggleMusicPlayback() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isMusicPlaying) Icons.Default.Warning else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Stop",
                                    tint = if (viewModel.isMusicPlaying) Color.Black else CyberSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Lyria Output Wave (.mp3)", color = CyberTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(
                                    text = if (viewModel.isMusicPlaying) "AUDIO GENERATING WAVE..." else "READY FOR AUDIO PLAY",
                                    color = if (viewModel.isMusicPlaying) CyberSecondary else CyberTextSecondary,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MediaScannerPanel(viewModel: MainViewModel) {
    var isVideoTabActive by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.loadSelectedImage(uri, isForAnalysis = true, isForEditing = false, isForAnimation = false)
            }
        }
    )

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.loadSelectedVideo(uri)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "INTELLIGENT MEDIA SCANNING LAB [gemini-3.1-pro-preview / 3.5-flash]",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(CyberCard, RoundedCornerShape(8.dp))
                .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                    .background(if (!isVideoTabActive) CyberSecondary else Color.Transparent)
                    .clickable { isVideoTabActive = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "IMAGE SCANNER",
                    color = if (!isVideoTabActive) Color.Black else CyberTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                    .background(if (isVideoTabActive) CyberSecondary else Color.Transparent)
                    .clickable { isVideoTabActive = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MIC / VIDEO",
                    color = if (isVideoTabActive) Color.Black else CyberTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (!isVideoTabActive) {
                item {
                    Text("SELECT TARGET IMAGE", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                    if (viewModel.scannedImageUri == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp))
                                .background(CyberCard)
                                .clickable { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("TAP TO IMPORT TARGET PHOTO/BITMAP", color = CyberSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCard, RoundedCornerShape(8.dp))
                                .border(1.dp, CyberSecondary, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = viewModel.scannedImageUri,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SOURCE IMAGE LOADED", color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Ready for deep pixel scanning", color = CyberTextSecondary, fontSize = 10.sp)
                            }
                            IconButton(onClick = { viewModel.clearScannedImage() }) {
                                Icon(Icons.Default.Warning, contentDescription = "Clear", tint = CyberError)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Text("ANALYSIS PROMPT SCORE", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                    OutlinedTextField(
                        value = viewModel.imageAnalysisPrompt,
                        onValueChange = { viewModel.imageAnalysisPrompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberCardLight,
                            focusedContainerColor = CyberCard,
                            unfocusedContainerColor = CyberCard
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.runImageAnalysis() },
                        enabled = viewModel.scannedImageBytes != null && !viewModel.isScanningImage,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black)
                    ) {
                        if (viewModel.isScanningImage) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        } else {
                            Text("RUN OPTICAL ANALYSIS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (viewModel.imageScannerError != null) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CyberError.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "SCAN FAULT: ${viewModel.imageScannerError}",
                                color = CyberError,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                if (viewModel.imageAnalysisResult != null) {
                    item {
                        Text(
                            text = "SCANNER SYSTEM REPORT",
                            fontSize = 11.sp,
                            color = CyberSecondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberSecondary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberCard)
                        ) {
                            Text(
                                text = viewModel.imageAnalysisResult ?: "",
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            } else {
                item {
                    VoiceTranscriberMainWidget(viewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text("SELECT MINI TARGET VIDEO (MP4)", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                    if (viewModel.scannedVideoUri == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp))
                                .background(CyberCard)
                                .clickable { videoPicker.launch("video/mp4") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("TAP TO IMPORT TARGET VIDEO FILE", color = CyberSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCard, RoundedCornerShape(8.dp))
                                .border(1.dp, CyberSecondary, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.Black, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberSecondary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("VIDEO MATRIX LOADED", color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { viewModel.clearScannedVideo() }) {
                                Icon(Icons.Default.Warning, contentDescription = "Clear", tint = CyberError)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Text("VIDEO INSIGHT SCANNING SCORE", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                    OutlinedTextField(
                        value = viewModel.videoAnalysisPrompt,
                        onValueChange = { viewModel.videoAnalysisPrompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberCardLight,
                            focusedContainerColor = CyberCard,
                            unfocusedContainerColor = CyberCard
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.runVideoAnalysis() },
                        enabled = viewModel.scannedVideoBytes != null && !viewModel.isScanningVideo,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black)
                    ) {
                        if (viewModel.isScanningVideo) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                        } else {
                            Text("RUN VIDEO TEMPORAL SCANS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (viewModel.videoScannerError != null) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CyberError.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "SCAN GLITCH: ${viewModel.videoScannerError}",
                                color = CyberError,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(12.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                if (viewModel.videoAnalysisResult != null) {
                    item {
                        Text(
                            text = "SCANNER SYSTEM REPORT (gemini-3.1-pro-preview)",
                            fontSize = 11.sp,
                            color = CyberSecondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberSecondary.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = CyberCard)
                        ) {
                            Text(
                                text = viewModel.videoAnalysisResult ?: "",
                                color = CyberTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceTranscriberMainWidget(viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REAL-TIME SPEECH TRANSCRIBER [gemini-3.5-flash]",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = CyberPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Text(
                text = "Saves base-64 high-fidelity mic stream. Emits full transcript via fast model API.",
                fontSize = 9.sp,
                color = CyberTextSecondary,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (viewModel.isRecordingAudio) CyberError.copy(alpha = 0.15f) else CyberCardLight
                    )
                    .border(
                        2.dp,
                        if (viewModel.isRecordingAudio) CyberError else CyberPrimary,
                        CircleShape
                    )
                    .clickable { viewModel.toggleRecording() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Mic button",
                    tint = if (viewModel.isRecordingAudio) CyberError else CyberPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.isRecordingAudio) {
                Text(
                    text = "RECORDING: 00:${viewModel.recordingTimerSeconds.toString().padStart(2, '0')} / 00:30 MAX",
                    color = CyberError,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Speak. Tap button again to transcribe.",
                    color = CyberTextSecondary,
                    fontSize = 10.sp
                )
            } else {
                Text(
                    text = "MIC OFFLINE",
                    color = CyberTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Tap circle to capture live stream.",
                    color = CyberTextSecondary,
                    fontSize = 9.sp
                )
            }

            if (viewModel.isTranscribingSource) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(color = CyberSecondary, trackColor = CyberCardLight, modifier = Modifier.fillMaxWidth())
                Text(
                    text = "Sonic waveform analysis active...",
                    fontSize = 10.sp,
                    color = CyberSecondary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (viewModel.transcriberError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "GLITCH: ${viewModel.transcriberError}",
                    color = CyberError,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (viewModel.transcribedTextResult != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "TRANSCRIPTION OUTPUT",
                    fontSize = 10.sp,
                    color = CyberSecondary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberCardLight, RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = viewModel.transcribedTextResult ?: "",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AboutPanel() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SYSTEM INFORMATION",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = CyberTextSecondary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "OmniAI Studio is an advanced multi-modal artificial intelligence workbench client for direct REST API integrations.",
            fontSize = 12.sp,
            color = CyberTextPrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "INTEGRATED ENGINE REGISTRY",
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val specRegistry = listOf(
                    "Chat (Low Latency)" to "gemini-3.1-flash-lite-preview",
                    "Cognitive Reasoning" to "gemini-3.1-pro-preview (HIGH THINKING)",
                    "Artwork Generator (Standard)" to "gemini-3.1-flash-image-preview",
                    "Artwork Generator (Pro)" to "gemini-3-pro-image-preview (1K/2K/4K)",
                    "Video Generator (Veo)" to "veo-3.1-fast-generate-preview",
                    "Speech Emulation (TTS)" to "gemini-3.1-flash-tts-preview",
                    "Music Synthesis (Lyria)" to "lyria-3-clip-preview / lyria-3-pro-preview",
                    "Optical Scanner (Image/Video)" to "gemini-3.1-pro-preview",
                    "Voice Transcriber (Mic)" to "gemini-3.5-flash"
                )

                specRegistry.forEach { (role, model) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(role, color = CyberTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(model, color = CyberSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCardLight, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CORRESPONDENT CLIENT OWNER",
                    color = CyberSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Owner Email: stevekondejnr@gmail.com",
                    color = CyberTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Device storage path: cache_matrix_system",
                    color = CyberTextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
