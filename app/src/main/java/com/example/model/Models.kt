package com.example.model

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

enum class MessageType {
    TEXT,
    VOICE_NOTE,
    IMAGE,
    LOCATION,
    SYSTEM_SECURITY
}

enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED
}

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val avatarInitials: String,
    val avatarColorHex: Long,
    val statusBio: String,
    val isOnMusaffa: Boolean = true,
    val isOnline: Boolean = false,
    val lastSeen: String = "recently",
    val safetyNumber: String = "48192 77019 38402 91823 88471 00293 84721 99182 37461 90182",
    val isSafetyNumberVerified: Boolean = false
)

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val type: MessageType = MessageType.TEXT,
    val isSelf: Boolean = false,
    val isEncrypted: Boolean = true,
    val audioDurationSec: Int? = null,
    val mediaUri: String? = null,
    val isBluetoothDirect: Boolean = false
)

data class Chat(
    val id: String,
    val contact: Contact,
    val lastMessage: Message?,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val isBluetoothMesh: Boolean = false,
    val encryptionAlgorithm: String = "Signal Protocol (X25519 / AES-256-GCM)",
    val disappearingTimerSec: Int = 0 // 0 = off
)

data class CallLog(
    val id: String,
    val contact: Contact,
    val timestamp: Long,
    val durationFormatted: String,
    val isVideo: Boolean,
    val callType: CallType,
    val isEncrypted: Boolean = true
)

data class BluetoothPeer(
    val id: String,
    val displayName: String,
    val macAddress: String,
    val rssi: Int, // e.g. -54 dBm
    val distanceEstimatedMeters: Double,
    val isConnected: Boolean,
    val hops: Int = 1,
    val batteryLevel: Int = 85
)

data class UserProfile(
    val id: String = "user_me",
    val name: String = "Mohsin Mirza",
    val phone: String = "+1 (555) 234-5678",
    val statusBio: String = "Always Encrypted with Musaffa Text 🔒",
    val avatarInitials: String = "MM",
    val avatarColorHex: Long = 0xFF10B981,
    val identityFingerprint: String = "E9A4 8C21 77FB 031A D089 42CC 9814 55F2 37B1 8904",
    val biometricEnabled: Boolean = true,
    val biometricTimeoutMinutes: Int = 0 // 0 = immediately
)
