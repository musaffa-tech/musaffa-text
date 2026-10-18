package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Message
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.ui.theme.BubbleReceivedDark
import com.example.ui.theme.BubbleReceivedLight
import com.example.ui.theme.BubbleSentDark
import com.example.ui.theme.BubbleSentLight
import com.example.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatBubble(
    message: Message,
    onSecurityInfoClick: () -> Unit = {}
) {
    if (message.type == MessageType.SYSTEM_SECURITY) {
        SystemSecurityBanner(message = message, onClick = onSecurityInfoClick)
        return
    }

    val isSelf = message.isSelf
    val isDark = isSystemInDarkTheme()

    val bubbleColor = when {
        isSelf -> if (isDark) BubbleSentDark else BubbleSentLight
        else -> if (isDark) BubbleReceivedDark else BubbleReceivedLight
    }

    val textColor = when {
        isSelf -> if (isDark) Color(0xFFF1FDF6) else Color(0xFF064E3B)
        else -> if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    }

    val alignment = if (isSelf) Alignment.End else Alignment.Start
    val bubbleShape = if (isSelf) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            shadowElevation = 1.dp,
            modifier = Modifier
                .widthIn(min = 64.dp, max = 300.dp)
                .testTag("chat_bubble_${message.id}")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // If sent via Bluetooth Mesh
                if (message.isBluetoothDirect) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B82F6))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BLE Offline Mesh • Direct Peer",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3B82F6)
                        )
                    }
                }

                when (message.type) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.content,
                            color = textColor,
                            fontSize = 15.sp,
                            lineHeight = 20.sp
                        )
                    }
                    MessageType.VOICE_NOTE -> {
                        VoiceNotePlayer(
                            durationSec = message.audioDurationSec ?: 18,
                            isSelf = isSelf,
                            textColor = textColor
                        )
                    }
                    MessageType.LOCATION -> {
                        LocationSnippet(
                            locationName = message.content,
                            textColor = textColor
                        )
                    }
                    MessageType.IMAGE -> {
                        ImageBubbleContent(caption = message.content, textColor = textColor)
                    }
                    else -> {
                        Text(text = message.content, color = textColor, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom row: Timestamp + Encryption lock + Delivery status ticks
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (message.isEncrypted) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = textColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Text(
                        text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp)),
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.65f)
                    )

                    if (isSelf) {
                        when (message.status) {
                            MessageStatus.SENDING -> {
                                Text(text = "•", fontSize = 11.sp, color = textColor.copy(alpha = 0.5f))
                            }
                            MessageStatus.SENT -> {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Sent",
                                    tint = textColor.copy(alpha = 0.65f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            MessageStatus.DELIVERED -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Delivered",
                                    tint = textColor.copy(alpha = 0.65f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            MessageStatus.READ -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Read",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(14.dp)
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
fun VoiceNotePlayer(
    durationSec: Int,
    isSelf: Boolean,
    textColor: Color
) {
    var isPlaying by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationSec * 1000, easing = LinearEasing)
            )
            isPlaying = false
            progress.snapTo(0f)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                isPlaying = !isPlaying
            },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary)
                .testTag("play_pause_voice_note")
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Simulated waveform bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val heights = listOf(6, 12, 18, 22, 14, 8, 16, 20, 24, 18, 10, 14, 22, 16, 12, 8, 14, 20, 16, 10)
                heights.forEachIndexed { index, barHeight ->
                    val barFraction = index.toFloat() / heights.size
                    val isActive = barFraction <= progress.value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(barHeight.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isActive) EmeraldPrimary else textColor.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentSec = (progress.value * durationSec).toInt()
                Text(
                    text = String.format(Locale.US, "%d:%02d", currentSec / 60, currentSec % 60),
                    fontSize = 11.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
                Text(
                    text = String.format(Locale.US, "%d:%02d", durationSec / 60, durationSec % 60),
                    fontSize = 11.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun LocationSnippet(
    locationName: String,
    textColor: Color
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF264653)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Map Location",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Live Shared Location",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = locationName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun ImageBubbleContent(caption: String, textColor: Color) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            MusaffaLogo(size = 48.dp, tint = EmeraldPrimary.copy(alpha = 0.7f))
        }
        if (caption.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = caption, color = textColor, fontSize = 14.sp)
        }
    }
}

@Composable
fun SystemSecurityBanner(
    message: Message,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            shadowElevation = 0.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .testTag("security_banner_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "E2EE Shield",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message.content,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
