package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import com.example.ui.components.BannerAdView
import com.example.ui.components.NativeChatAdCard
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallLog
import com.example.model.CallType
import com.example.model.Chat
import com.example.model.Contact
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.ui.components.MusaffaLogo
import com.example.ui.theme.BluetoothBlue
import com.example.ui.theme.CallRed
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.viewmodel.DashboardTab
import com.example.viewmodel.MusaffaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainDashboardScreen(
    viewModel: MusaffaViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val callLogs by viewModel.callLogs.collectAsState()
    val meshManager = viewModel.bluetoothManager
    val peers by meshManager.peers.collectAsState()

    val totalUnreadChats = chats.sumOf { it.unreadCount }
    val missedCalls = callLogs.count { it.callType == CallType.MISSED }
    val connectedPeers = peers.count { it.isConnected }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("main_dashboard"),
        topBar = {
            DashboardTopBar(viewModel = viewModel)
        },
        bottomBar = {
            Column {
                BannerAdView(
                    sponsorName = "NordVPN Encrypted Tunnel",
                    tagline = "Military-grade encryption for all your web traffic.",
                    ctaText = "Install",
                    onAdClick = { viewModel.triggerInterstitialAd() }
                )
                DashboardBottomBar(
                    selectedTab = selectedTab,
                    onSelectTab = { viewModel.selectTab(it) },
                    unreadChatsCount = totalUnreadChats,
                    missedCallsCount = missedCalls,
                    connectedPeersCount = connectedPeers
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == DashboardTab.CHATS || selectedTab == DashboardTab.CONTACTS) {
                FloatingActionButton(
                    onClick = { viewModel.selectTab(DashboardTab.CONTACTS) },
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("compose_chat_fab")
                ) {
                    Icon(imageVector = Icons.Default.AddComment, contentDescription = "New Encrypted Chat")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                DashboardTab.CHATS -> ChatsTabContent(viewModel = viewModel)
                DashboardTab.CALLS -> CallsTabContent(viewModel = viewModel)
                DashboardTab.OFFLINE_MESH -> BluetoothMeshScreen(viewModel = viewModel)
                DashboardTab.CONTACTS -> ContactsScreen(viewModel = viewModel)
                DashboardTab.SETTINGS -> ProfileSettingsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun DashboardTopBar(viewModel: MusaffaViewModel) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MusaffaLogo(size = 38.dp, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Musaffa Text",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "End-to-End Encrypted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.selectTab(DashboardTab.OFFLINE_MESH) },
                    modifier = Modifier.testTag("top_offline_radar_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Bluetooth Mesh",
                        tint = BluetoothBlue
                    )
                }

                IconButton(
                    onClick = { viewModel.lockApp() },
                    modifier = Modifier.testTag("top_lock_app_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Vault",
                        tint = EmeraldPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardBottomBar(
    selectedTab: DashboardTab,
    onSelectTab: (DashboardTab) -> Unit,
    unreadChatsCount: Int,
    missedCallsCount: Int,
    connectedPeersCount: Int
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // Chats Tab with Unread Badge
        NavigationBarItem(
            selected = selectedTab == DashboardTab.CHATS,
            onClick = { onSelectTab(DashboardTab.CHATS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadChatsCount > 0) {
                            Badge(containerColor = EmeraldPrimary, contentColor = Color.White) {
                                Text("$unreadChatsCount")
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = "Chats")
                }
            },
            label = { Text("Chats", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("tab_chats")
        )

        // Calls Tab with Missed Calls Badge
        NavigationBarItem(
            selected = selectedTab == DashboardTab.CALLS,
            onClick = { onSelectTab(DashboardTab.CALLS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (missedCallsCount > 0) {
                            Badge(containerColor = CallRed, contentColor = Color.White) {
                                Text("$missedCallsCount")
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Calls")
                }
            },
            label = { Text("Calls", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("tab_calls")
        )

        // Bluetooth Mesh Tab with Peer Count Badge
        NavigationBarItem(
            selected = selectedTab == DashboardTab.OFFLINE_MESH,
            onClick = { onSelectTab(DashboardTab.OFFLINE_MESH) },
            icon = {
                BadgedBox(
                    badge = {
                        if (connectedPeersCount > 0) {
                            Badge(containerColor = BluetoothBlue, contentColor = Color.White) {
                                Text("$connectedPeersCount")
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "Mesh")
                }
            },
            label = { Text("Mesh", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BluetoothBlue,
                selectedTextColor = BluetoothBlue,
                indicatorColor = BluetoothBlue.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("tab_mesh")
        )

        // Contacts Tab
        NavigationBarItem(
            selected = selectedTab == DashboardTab.CONTACTS,
            onClick = { onSelectTab(DashboardTab.CONTACTS) },
            icon = {
                Icon(imageVector = Icons.Default.People, contentDescription = "Contacts")
            },
            label = { Text("Contacts", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("tab_contacts")
        )

        // Settings Tab
        NavigationBarItem(
            selected = selectedTab == DashboardTab.SETTINGS,
            onClick = { onSelectTab(DashboardTab.SETTINGS) },
            icon = {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            },
            label = { Text("Settings", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldPrimary,
                selectedTextColor = EmeraldPrimary,
                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("tab_settings")
        )
    }
}

@Composable
private fun ChatsTabContent(viewModel: MusaffaViewModel) {
    val chats by viewModel.chats.collectAsState()
    var searchFilter by remember { mutableStateOf("") }

    val filteredChats = remember(chats, searchFilter) {
        if (searchFilter.isBlank()) chats
        else chats.filter {
            it.contact.name.contains(searchFilter, ignoreCase = true) ||
            (it.lastMessage?.content?.contains(searchFilter, ignoreCase = true) == true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab to Chat Quick Switcher Ribbon
        TabToChatRibbon(chats = chats, onSelectChat = { viewModel.openChat(it.id) })

        // Search Input
        OutlinedTextField(
            value = searchFilter,
            onValueChange = { searchFilter = it },
            placeholder = { Text("Search encrypted chats & messages…", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("chats_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Chats List
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(filteredChats, key = { _, chat -> chat.id }) { index, chat ->
                ChatItemRow(
                    chat = chat,
                    onClick = { viewModel.openChat(chat.id) }
                )
                if (index == 1) {
                    NativeChatAdCard(
                        sponsorName = "Proton Privacy Pass",
                        description = "End-to-end encrypted storage & password manager with zero-knowledge architecture.",
                        rating = "4.9 ★",
                        ctaText = "Try Free",
                        onAdClick = { viewModel.triggerInterstitialAd() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabToChatRibbon(
    chats: List<Chat>,
    onSelectChat: (Chat) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TAB TO CHAT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
            Text(
                text = "Direct Switcher",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            chats.forEach { chat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectChat(chat) }
                        .testTag("tab_to_chat_${chat.id}")
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(chat.contact.avatarColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.contact.avatarInitials,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 17.sp
                            )
                        }
                        if (chat.contact.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chat.contact.name.split(" ").firstOrNull() ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatItemRow(
    chat: Chat,
    onClick: () -> Unit
) {
    val contact = chat.contact
    val lastMsg = chat.lastMessage

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("chat_row_${chat.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(contact.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.avatarInitials,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
                if (contact.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = contact.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (chat.isPinned) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    if (lastMsg != null) {
                        Text(
                            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(lastMsg.timestamp)),
                            fontSize = 12.sp,
                            color = if (chat.unreadCount > 0) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (chat.isBluetoothMesh) {
                            Icon(
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = "BLE",
                                tint = BluetoothBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        } else if (lastMsg?.isSelf == true) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Read",
                                tint = if (lastMsg.status == MessageStatus.READ) EmeraldPrimary else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        val snippet = when (lastMsg?.type) {
                            MessageType.VOICE_NOTE -> "🎤 Voice message (${lastMsg.audioDurationSec ?: 15}s)"
                            MessageType.LOCATION -> "📍 Shared location"
                            MessageType.IMAGE -> "📷 Photo attachment"
                            else -> lastMsg?.content ?: "Start encrypted conversation…"
                        }

                        Text(
                            text = snippet,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (chat.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (chat.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${chat.unreadCount}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CallsTabContent(viewModel: MusaffaViewModel) {
    val callLogs by viewModel.callLogs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Encrypted Calls",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "E2EE Calls", fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(callLogs, key = { it.id }) { log ->
                CallLogRow(
                    log = log,
                    onCallVoice = { viewModel.startCall(log.contact, isVideo = false) },
                    onCallVideo = { viewModel.startCall(log.contact, isVideo = true) }
                )
            }
        }
    }
}

@Composable
private fun CallLogRow(
    log: CallLog,
    onCallVoice: () -> Unit,
    onCallVideo: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(log.contact.avatarColorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = log.contact.avatarInitials,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = log.contact.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        when (log.callType) {
                            CallType.MISSED -> {
                                Icon(imageVector = Icons.Default.CallReceived, contentDescription = "Missed", tint = CallRed, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Missed", fontSize = 12.sp, color = CallRed)
                            }
                            CallType.INCOMING -> {
                                Icon(imageVector = Icons.Default.CallReceived, contentDescription = "Incoming", tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Incoming • ${log.durationFormatted}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            CallType.OUTGOING -> {
                                Icon(imageVector = Icons.Default.CallMade, contentDescription = "Outgoing", tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Outgoing • ${log.durationFormatted}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = if (log.isVideo) onCallVideo else onCallVoice,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary.copy(alpha = 0.12f))
            ) {
                Icon(
                    imageVector = if (log.isVideo) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = "Call Back",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
