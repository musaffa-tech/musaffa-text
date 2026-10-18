package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MusaffaLogo
import com.example.ui.theme.EmeraldPrimary
import com.example.viewmodel.ActiveCallState
import com.example.viewmodel.MusaffaViewModel
import java.util.Locale

@Composable
fun CallScreen(
    viewModel: MusaffaViewModel
) {
    val callState by viewModel.activeCall.collectAsState()

    BackHandler {
        viewModel.endCall()
    }

    if (callState == null) return
    val state = callState ?: return
    val contact = state.contact

    val infiniteTransition = rememberInfiniteTransition(label = "call_pulse")
    val pulseRing by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseRing"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("active_call_screen")
    ) {
        if (state.isVideo && state.isVideoEnabled) {
            // Simulated Video stream background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF064E3B), Color(0xFF0F172A), Color(0xFF021B14))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MusaffaLogo(size = 90.dp, tint = EmeraldPrimary.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Encrypted Video Stream",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // PiP Front Camera preview in top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                        .size(width = 100.dp, height = 140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E293B))
                        .border(2.dp, EmeraldPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "You", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "HD 1080p", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                }
            }
        }

        // Top info header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // E2EE Security Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF10B981).copy(alpha = 0.2f),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted Call",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "End-to-End Encrypted HD Call",
                        color = EmeraldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = contact.name,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (state.isConnected) {
                    String.format(Locale.US, "%02d:%02d", state.durationSec / 60, state.durationSec % 60)
                } else {
                    if (state.isVideo) "Encrypted Video Calling…" else "Ringing…"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (state.isConnected) EmeraldPrimary else Color.White.copy(alpha = 0.7f)
            )
        }

        // Middle Avatar with Animated Soundwaves (if Voice call)
        if (!state.isVideo || !state.isVideoEnabled) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isConnected) {
                    // Outer wave ring 1
                    Box(
                        modifier = Modifier
                            .size(190.dp * pulseRing)
                            .clip(CircleShape)
                            .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.25f), CircleShape)
                    )
                    // Outer wave ring 2
                    Box(
                        modifier = Modifier
                            .size(150.dp * pulseRing)
                            .clip(CircleShape)
                            .border(2.dp, EmeraldPrimary.copy(alpha = 0.45f), CircleShape)
                    )
                }

                // Contact Avatar center circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(contact.avatarColorHex))
                        .border(3.dp, EmeraldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.avatarInitials,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 42.sp
                    )
                }
            }
        }

        // Bottom Controls Bar
        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color(0xFF111827).copy(alpha = 0.95f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    IconButton(
                        onClick = { viewModel.toggleCallMute() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (state.isMuted) Color(0xFFEF4444) else Color(0xFF1E293B))
                            .testTag("call_mute_toggle")
                    ) {
                        Icon(
                            imageVector = if (state.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Speaker Button
                    IconButton(
                        onClick = { viewModel.toggleCallSpeaker() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (state.isSpeakerOn) EmeraldPrimary else Color(0xFF1E293B))
                            .testTag("call_speaker_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Video Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleCallVideo() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (state.isVideoEnabled) EmeraldPrimary else Color(0xFF1E293B))
                            .testTag("call_video_toggle")
                    ) {
                        Icon(
                            imageVector = if (state.isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Toggle Video",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // End Call Button
                    IconButton(
                        onClick = { viewModel.endCall() },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .testTag("call_hangup_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
