package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth.BluetoothMeshManager
import com.example.model.BluetoothPeer
import com.example.model.CallLog
import com.example.model.CallType
import com.example.model.Chat
import com.example.model.Contact
import com.example.model.Message
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.model.UserProfile
import com.example.security.EncryptionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class DashboardTab {
    CHATS,
    CALLS,
    OFFLINE_MESH,
    CONTACTS,
    SETTINGS
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class ChatWallpaper {
    EMERALD_MINT,
    MIDNIGHT_SLATE,
    FOREST_MIST,
    MINIMAL
}

data class ActiveCallState(
    val contact: Contact,
    val isVideo: Boolean,
    val isIncoming: Boolean = false,
    val isConnected: Boolean = false,
    val durationSec: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isVideoEnabled: Boolean = true
)

class MusaffaViewModel(application: Application) : AndroidViewModel(application) {

    val bluetoothManager = BluetoothMeshManager(application)

    // Navigation & Auth State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _selectedTab = MutableStateFlow(DashboardTab.CHATS)
    val selectedTab: StateFlow<DashboardTab> = _selectedTab.asStateFlow()

    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()

    private val _safetyContact = MutableStateFlow<Contact?>(null)
    val safetyContact: StateFlow<Contact?> = _safetyContact.asStateFlow()

    // App Preferences
    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _chatWallpaper = MutableStateFlow(ChatWallpaper.EMERALD_MINT)
    val chatWallpaper: StateFlow<ChatWallpaper> = _chatWallpaper.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Monetization: Ads State
    private val _showInterstitialAd = MutableStateFlow(false)
    val showInterstitialAd: StateFlow<Boolean> = _showInterstitialAd.asStateFlow()

    private val _personalizedAds = MutableStateFlow(true)
    val personalizedAds: StateFlow<Boolean> = _personalizedAds.asStateFlow()

    // Contacts
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    // Chats & Messages
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    // Calls
    private val _callLogs = MutableStateFlow<List<CallLog>>(emptyList())
    val callLogs: StateFlow<List<CallLog>> = _callLogs.asStateFlow()

    // Typing Status per chat
    private val _peerTypingMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val peerTypingMap: StateFlow<Map<String, Boolean>> = _peerTypingMap.asStateFlow()

    private var callTimerJob: Job? = null

    init {
        initializeSampleData()
    }

    private fun initializeSampleData() {
        val contactSarah = Contact(
            id = "c_sarah",
            name = "Sarah Chen",
            phone = "+1 (555) 948-2831",
            avatarInitials = "SC",
            avatarColorHex = 0xFF10B981,
            statusBio = "Encrypted communications only 🔐",
            isOnMusaffa = true,
            isOnline = true,
            safetyNumber = EncryptionManager.generateSafetyNumber("+1 (555) 234-5678", "+1 (555) 948-2831"),
            isSafetyNumberVerified = true
        )

        val contactOmar = Contact(
            id = "c_omar",
            name = "Omar Farooq",
            phone = "+971 50 123 4567",
            avatarInitials = "OF",
            avatarColorHex = 0xFF059669,
            statusBio = "Offline Bluetooth Mesh testing",
            isOnMusaffa = true,
            isOnline = true,
            safetyNumber = EncryptionManager.generateSafetyNumber("+1 (555) 234-5678", "+971 50 123 4567"),
            isSafetyNumberVerified = true
        )

        val contactAyesha = Contact(
            id = "c_ayesha",
            name = "Dr. Ayesha Malik",
            phone = "+44 7700 900077",
            avatarInitials = "AM",
            avatarColorHex = 0xFF0D9488,
            statusBio = "Busy in clinic • Ping for urgent calls",
            isOnMusaffa = true,
            isOnline = false,
            lastSeen = "today at 11:20 AM",
            safetyNumber = EncryptionManager.generateSafetyNumber("+1 (555) 234-5678", "+44 7700 900077"),
            isSafetyNumberVerified = false
        )

        val contactZayn = Contact(
            id = "c_zayn",
            name = "Zayn Ahmed",
            phone = "+1 (555) 302-8844",
            avatarInitials = "ZA",
            avatarColorHex = 0xFF14B8A6,
            statusBio = "Hey there! I am using Musaffa Text.",
            isOnMusaffa = true,
            isOnline = false,
            lastSeen = "yesterday",
            safetyNumber = EncryptionManager.generateSafetyNumber("+1 (555) 234-5678", "+1 (555) 302-8844"),
            isSafetyNumberVerified = false
        )

        val contactDavid = Contact(
            id = "c_david",
            name = "David Miller",
            phone = "+1 (555) 771-4433",
            avatarInitials = "DM",
            avatarColorHex = 0xFF64748B,
            statusBio = "Not on Musaffa Text yet",
            isOnMusaffa = false,
            isOnline = false,
            lastSeen = "offline"
        )

        _contacts.value = listOf(contactSarah, contactOmar, contactAyesha, contactZayn, contactDavid)

        // Seed messages
        val sarahMsgs = listOf(
            Message(
                id = "m1",
                chatId = "chat_sarah",
                senderId = "system",
                content = "Messages and calls are end-to-end encrypted with Signal Protocol (X25519 & AES-256-GCM). No one outside of this chat can read or listen to them. Tap to verify safety number.",
                type = MessageType.SYSTEM_SECURITY,
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            Message(
                id = "m2",
                chatId = "chat_sarah",
                senderId = "c_sarah",
                content = "Hey Mohsin! Did you verify the cryptographic fingerprint on the new Musaffa Text build?",
                timestamp = System.currentTimeMillis() - 3600000L,
                status = MessageStatus.READ,
                isSelf = false
            ),
            Message(
                id = "m3",
                chatId = "chat_sarah",
                senderId = "user_me",
                content = "Yes, verified! The 60-digit safety numbers match 100%. The biometric lock and offline mesh work seamlessly.",
                timestamp = System.currentTimeMillis() - 1800000L,
                status = MessageStatus.READ,
                isSelf = true
            ),
            Message(
                id = "m4",
                chatId = "chat_sarah",
                senderId = "c_sarah",
                content = "Awesome! I recorded a quick voice briefing on the upcoming peer-to-peer relay tests:",
                timestamp = System.currentTimeMillis() - 900000L,
                status = MessageStatus.READ,
                isSelf = false
            ),
            Message(
                id = "m5",
                chatId = "chat_sarah",
                senderId = "c_sarah",
                content = "Voice note briefing",
                type = MessageType.VOICE_NOTE,
                audioDurationSec = 24,
                timestamp = System.currentTimeMillis() - 850000L,
                status = MessageStatus.READ,
                isSelf = false
            )
        )

        val omarMsgs = listOf(
            Message(
                id = "m_o1",
                chatId = "chat_omar",
                senderId = "system",
                content = "Bluetooth-to-Bluetooth Offline Mesh active. Direct peer connection established without cellular/internet data.",
                type = MessageType.SYSTEM_SECURITY,
                timestamp = System.currentTimeMillis() - 7200000L
            ),
            Message(
                id = "m_o2",
                chatId = "chat_omar",
                senderId = "c_omar",
                content = "Testing local mesh hops! I just disconnected my Wi-Fi and 5G.",
                timestamp = System.currentTimeMillis() - 1200000L,
                status = MessageStatus.READ,
                isSelf = false,
                isBluetoothDirect = true
            ),
            Message(
                id = "m_o3",
                chatId = "chat_omar",
                senderId = "user_me",
                content = "Received loud and clear via BLE direct advertising! Packet latency is under 20ms.",
                timestamp = System.currentTimeMillis() - 600000L,
                status = MessageStatus.READ,
                isSelf = true,
                isBluetoothDirect = true
            )
        )

        val ayeshaMsgs = listOf(
            Message(
                id = "m_a1",
                chatId = "chat_ayesha",
                senderId = "system",
                content = "Messages are end-to-end encrypted. Tap to verify security keys.",
                type = MessageType.SYSTEM_SECURITY,
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            Message(
                id = "m_a2",
                chatId = "chat_ayesha",
                senderId = "c_ayesha",
                content = "Sharing clinic coordinates for tomorrow's meeting:",
                timestamp = System.currentTimeMillis() - 7200000L,
                status = MessageStatus.READ,
                isSelf = false
            ),
            Message(
                id = "m_a3",
                chatId = "chat_ayesha",
                senderId = "c_ayesha",
                content = "Central Medical Centre, Suite 402, North Avenue",
                type = MessageType.LOCATION,
                timestamp = System.currentTimeMillis() - 7100000L,
                status = MessageStatus.READ,
                isSelf = false
            )
        )

        _messages.value = mapOf(
            "chat_sarah" to sarahMsgs,
            "chat_omar" to omarMsgs,
            "chat_ayesha" to ayeshaMsgs
        )

        _chats.value = listOf(
            Chat(
                id = "chat_sarah",
                contact = contactSarah,
                lastMessage = sarahMsgs.last(),
                unreadCount = 0,
                isPinned = true
            ),
            Chat(
                id = "chat_omar",
                contact = contactOmar,
                lastMessage = omarMsgs.last(),
                unreadCount = 1,
                isPinned = true,
                isBluetoothMesh = true
            ),
            Chat(
                id = "chat_ayesha",
                contact = contactAyesha,
                lastMessage = ayeshaMsgs.last(),
                unreadCount = 0
            ),
            Chat(
                id = "chat_zayn",
                contact = contactZayn,
                lastMessage = Message(
                    id = "mz",
                    chatId = "chat_zayn",
                    senderId = "c_zayn",
                    content = "Catch you later for the secure voice call!",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    status = MessageStatus.READ,
                    isSelf = false
                ),
                unreadCount = 0
            )
        )

        _callLogs.value = listOf(
            CallLog(
                id = "call_1",
                contact = contactSarah,
                timestamp = System.currentTimeMillis() - 10800000L,
                durationFormatted = "14:28",
                isVideo = true,
                callType = CallType.OUTGOING
            ),
            CallLog(
                id = "call_2",
                contact = contactOmar,
                timestamp = System.currentTimeMillis() - 43200000L,
                durationFormatted = "04:12",
                isVideo = false,
                callType = CallType.INCOMING
            ),
            CallLog(
                id = "call_3",
                contact = contactAyesha,
                timestamp = System.currentTimeMillis() - 86400000L,
                durationFormatted = "Missed",
                isVideo = false,
                callType = CallType.MISSED
            )
        )
    }

    fun selectTab(tab: DashboardTab) {
        _selectedTab.value = tab
    }

    fun openChat(chatId: String) {
        _activeChatId.value = chatId
        // Clear unread count for this chat
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(unreadCount = 0) else it
        }
    }

    fun closeChat() {
        _activeChatId.value = null
    }

    fun openChatByContact(contact: Contact) {
        val existingChat = _chats.value.find { it.contact.id == contact.id }
        if (existingChat != null) {
            openChat(existingChat.id)
        } else {
            val newChatId = "chat_${contact.id}"
            val newChat = Chat(
                id = newChatId,
                contact = contact,
                lastMessage = null,
                unreadCount = 0
            )
            _chats.value = listOf(newChat) + _chats.value
            _messages.value = _messages.value + (newChatId to listOf(
                Message(
                    id = UUID.randomUUID().toString(),
                    chatId = newChatId,
                    senderId = "system",
                    content = "End-to-end encrypted session initialized with ${contact.name}.",
                    type = MessageType.SYSTEM_SECURITY,
                    timestamp = System.currentTimeMillis()
                )
            ))
            openChat(newChatId)
        }
    }

    fun sendMessage(chatId: String, text: String, isBluetoothDirect: Boolean = false) {
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = "user_me",
            content = text,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            type = MessageType.TEXT,
            isSelf = true,
            isEncrypted = true,
            isBluetoothDirect = isBluetoothDirect
        )

        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + newMsg))

        // Update chat's last message
        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(lastMessage = newMsg) else it
        }

        // Simulate peer delivery & automated intelligent reply
        simulatePeerReply(chatId, newMsg)
    }

    fun sendVoiceNote(chatId: String, durationSec: Int) {
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = "user_me",
            content = "Voice note ($durationSec sec)",
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            type = MessageType.VOICE_NOTE,
            audioDurationSec = durationSec,
            isSelf = true,
            isEncrypted = true
        )

        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + newMsg))

        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(lastMessage = newMsg) else it
        }

        simulatePeerReply(chatId, newMsg)
    }

    fun sendLocationMessage(chatId: String, locationName: String) {
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = "user_me",
            content = locationName,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            type = MessageType.LOCATION,
            isSelf = true,
            isEncrypted = true
        )

        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + newMsg))

        _chats.value = _chats.value.map {
            if (it.id == chatId) it.copy(lastMessage = newMsg) else it
        }
    }

    private fun simulatePeerReply(chatId: String, userMsg: Message) {
        viewModelScope.launch {
            // First mark as delivered
            delay(800)
            updateMessageStatus(chatId, userMsg.id, MessageStatus.DELIVERED)

            // Then mark as read
            delay(1200)
            updateMessageStatus(chatId, userMsg.id, MessageStatus.READ)

            // Peer typing indicator
            _peerTypingMap.value = _peerTypingMap.value + (chatId to true)
            delay(2200)
            _peerTypingMap.value = _peerTypingMap.value + (chatId to false)

            // Peer replies with context
            val replyText = when {
                userMsg.type == MessageType.VOICE_NOTE -> "Heard your voice note clearly! Crystal clear encrypted audio."
                userMsg.isBluetoothDirect -> "Received offline via Bluetooth mesh hop! Zero internet used 📡"
                userMsg.content.contains("hello", ignoreCase = true) || userMsg.content.contains("hi", ignoreCase = true) ->
                    "Hello! Everything is running smooth and fully encrypted."
                userMsg.content.contains("call", ignoreCase = true) ->
                    "Ready when you are! Hit the video or voice call button above."
                else -> "Got your message securely! (Verified with Signal Protocol E2EE)"
            }

            val chat = _chats.value.find { it.id == chatId }
            val senderId = chat?.contact?.id ?: "peer"

            val replyMsg = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = senderId,
                content = replyText,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.READ,
                type = MessageType.TEXT,
                isSelf = false,
                isEncrypted = true,
                isBluetoothDirect = userMsg.isBluetoothDirect
            )

            val currentList = _messages.value[chatId] ?: emptyList()
            _messages.value = _messages.value + (chatId to (currentList + replyMsg))

            _chats.value = _chats.value.map {
                if (it.id == chatId) it.copy(lastMessage = replyMsg) else it
            }
        }
    }

    private fun updateMessageStatus(chatId: String, msgId: String, status: MessageStatus) {
        val list = _messages.value[chatId] ?: return
        _messages.value = _messages.value + (chatId to list.map {
            if (it.id == msgId) it.copy(status = status) else it
        })
    }

    // Calls
    fun startCall(contact: Contact, isVideo: Boolean) {
        _activeCall.value = ActiveCallState(
            contact = contact,
            isVideo = isVideo,
            isIncoming = false,
            isConnected = false
        )

        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            delay(2000) // Call ringing
            _activeCall.value = _activeCall.value?.copy(isConnected = true)

            // Increment call duration timer
            while (_activeCall.value != null && _activeCall.value?.isConnected == true) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let { it.copy(durationSec = it.durationSec + 1) }
            }
        }
    }

    fun endCall() {
        val currentCall = _activeCall.value
        if (currentCall != null) {
            val durationText = String.format("%02d:%02d", currentCall.durationSec / 60, currentCall.durationSec % 60)
            val newLog = CallLog(
                id = UUID.randomUUID().toString(),
                contact = currentCall.contact,
                timestamp = System.currentTimeMillis(),
                durationFormatted = if (currentCall.isConnected) durationText else "Missed",
                isVideo = currentCall.isVideo,
                callType = CallType.OUTGOING
            )
            _callLogs.value = listOf(newLog) + _callLogs.value
        }
        callTimerJob?.cancel()
        _activeCall.value = null
        // Monetization: Display post-call interstitial advertisement
        _showInterstitialAd.value = true
    }

    fun dismissInterstitialAd() {
        _showInterstitialAd.value = false
    }

    fun triggerInterstitialAd() {
        _showInterstitialAd.value = true
    }

    fun togglePersonalizedAds(enabled: Boolean) {
        _personalizedAds.value = enabled
    }

    fun toggleCallMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleCallVideo() {
        _activeCall.value = _activeCall.value?.let { it.copy(isVideoEnabled = !it.isVideoEnabled) }
    }

    // Safety Number & Encryption Dialog
    fun showSafetyDialog(contact: Contact) {
        _safetyContact.value = contact
    }

    fun dismissSafetyDialog() {
        _safetyContact.value = null
    }

    fun toggleContactSafetyVerified(contactId: String, verified: Boolean) {
        _contacts.value = _contacts.value.map {
            if (it.id == contactId) it.copy(isSafetyNumberVerified = verified) else it
        }
        _chats.value = _chats.value.map {
            if (it.contact.id == contactId) it.copy(contact = it.contact.copy(isSafetyNumberVerified = verified)) else it
        }
        _safetyContact.value = _safetyContact.value?.let {
            if (it.id == contactId) it.copy(isSafetyNumberVerified = verified) else it
        }
    }

    // App Lock & Biometrics
    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun toggleBiometricEnabled(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(biometricEnabled = enabled)
    }

    // Profile Settings
    fun updateUserProfile(name: String, statusBio: String) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            statusBio = statusBio,
            avatarInitials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun setChatWallpaper(wallpaper: ChatWallpaper) {
        _chatWallpaper.value = wallpaper
    }

    // Authentication (Phone login)
    fun loginWithPhone(phoneNumber: String) {
        _userProfile.value = _userProfile.value.copy(phone = phoneNumber)
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
