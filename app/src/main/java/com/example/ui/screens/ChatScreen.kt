package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Chat
import com.example.ui.components.ChatBubble
import com.example.ui.components.VoiceRecorderBar
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.viewmodel.ChatWallpaper
import com.example.viewmodel.MusaffaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    viewModel: MusaffaViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val chats by viewModel.chats.collectAsState()
    val chat = chats.find { it.id == chatId }
    val allMessages by viewModel.messages.collectAsState()
    val messages = allMessages[chatId] ?: emptyList()
    val peerTypingMap by viewModel.peerTypingMap.collectAsState()
    val isPeerTyping = peerTypingMap[chatId] == true
    val wallpaper by viewModel.chatWallpaper.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var isAttachmentSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size, isPeerTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (chat == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Chat not found")
        }
        return
    }

    val contact = chat.contact

    // Background color/styling depending on selected wallpaper
    val wallpaperColor = when (wallpaper) {
        ChatWallpaper.EMERALD_MINT -> MaterialTheme.colorScheme.background
        ChatWallpaper.MIDNIGHT_SLATE -> Color(0xFF0F172A)
        ChatWallpaper.FOREST_MIST -> Color(0xFF062318)
        ChatWallpaper.MINIMAL -> MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(wallpaperColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("chat_screen_${chatId}")
    ) {
        // Sleek Top App Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Contact Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(contact.avatarColorHex))
                        .clickable { viewModel.showSafetyDialog(contact) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.avatarInitials,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Name and Encryption / Status Subtitle
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.showSafetyDialog(contact) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "E2EE",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    if (isPeerTyping) {
                        Text(
                            text = "typing…",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                    } else if (chat.isBluetoothMesh) {
                        Text(
                            text = "BLE Offline Mesh • Hop 1",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3B82F6)
                        )
                    } else if (contact.isOnline) {
                        Text(
                            text = "Online • Verified E2EE",
                            fontSize = 11.sp,
                            color = EmeraldPrimary
                        )
                    } else {
                        Text(
                            text = "Last seen ${contact.lastSeen}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Call Actions: Voice Call, Video Call, Safety QR
                IconButton(
                    onClick = { viewModel.startCall(contact, isVideo = false) },
                    modifier = Modifier.testTag("voice_call_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = EmeraldPrimary
                    )
                }

                IconButton(
                    onClick = { viewModel.startCall(contact, isVideo = true) },
                    modifier = Modifier.testTag("video_call_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = EmeraldPrimary
                    )
                }

                IconButton(
                    onClick = { viewModel.showSafetyDialog(contact) },
                    modifier = Modifier.testTag("verify_keys_top_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Verify Encryption Keys",
                        tint = if (contact.isSafetyNumberVerified) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatBubble(
                    message = message,
                    onSecurityInfoClick = { viewModel.showSafetyDialog(contact) }
                )
            }

            if (isPeerTyping) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${contact.name} is typing…",
                                fontSize = 12.sp,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Voice Recorder & Text Input Bar
        VoiceRecorderBar(
            text = inputText,
            onTextChange = { inputText = it },
            onSendText = { text ->
                viewModel.sendMessage(chatId, text, isBluetoothDirect = chat.isBluetoothMesh)
            },
            onSendVoiceNote = { duration ->
                viewModel.sendVoiceNote(chatId, duration)
            },
            onAttachClick = { isAttachmentSheetOpen = true }
        )
    }

    // Attachment Modal Bottom Sheet
    if (isAttachmentSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isAttachmentSheetOpen = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Share with End-to-End Encryption",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AttachmentOption(
                        icon = Icons.Default.LocationOn,
                        title = "Location",
                        color = Color(0xFF10B981),
                        onClick = {
                            viewModel.sendLocationMessage(chatId, "Downtown Cyber Hub, 5th Avenue (Live GPS)")
                            isAttachmentSheetOpen = false
                        }
                    )
                    AttachmentOption(
                        icon = Icons.Default.Image,
                        title = "Gallery",
                        color = Color(0xFF3B82F6),
                        onClick = {
                            viewModel.sendMessage(chatId, "📷 Encrypted photo attachment: document_scan.png")
                            isAttachmentSheetOpen = false
                        }
                    )
                    AttachmentOption(
                        icon = Icons.Default.Person,
                        title = "Contact",
                        color = Color(0xFF8B5CF6),
                        onClick = {
                            viewModel.sendMessage(chatId, "👤 Contact Card: Omar Farooq (+971 50 123 4567)")
                            isAttachmentSheetOpen = false
                        }
                    )
                    AttachmentOption(
                        icon = Icons.Default.Security,
                        title = "Verify Key",
                        color = Color(0xFFF59E0B),
                        onClick = {
                            isAttachmentSheetOpen = false
                            viewModel.showSafetyDialog(contact)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun AttachmentOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
