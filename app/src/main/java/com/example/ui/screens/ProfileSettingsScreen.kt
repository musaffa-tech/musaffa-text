package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MusaffaLogo
import com.example.ui.theme.EmeraldPrimary
import com.example.viewmodel.ChatWallpaper
import com.example.viewmodel.MusaffaViewModel
import com.example.viewmodel.ThemeMode

@Composable
fun ProfileSettingsScreen(
    viewModel: MusaffaViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val currentWallpaper by viewModel.chatWallpaper.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showKeyDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.name) }
    var editBio by remember { mutableStateOf(userProfile.statusBio) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_settings_screen")
    ) {
        // User Profile Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.avatarInitials,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userProfile.phone,
                        fontSize = 13.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userProfile.statusBio,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }

                IconButton(
                    onClick = {
                        editName = userProfile.name
                        editBio = userProfile.statusBio
                        showEditProfileDialog = true
                    },
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = EmeraldPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Privacy & Security
        SettingsSectionHeader(title = "SECURITY & BIOMETRICS")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                // Biometric App Lock Toggle
                SettingsSwitchRow(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric App Lock",
                    subtitle = "Require fingerprint or Face ID to open app",
                    checked = userProfile.biometricEnabled,
                    onCheckedChange = { viewModel.toggleBiometricEnabled(it) },
                    testTag = "biometric_lock_toggle"
                )

                SettingsDivider()

                // Test App Lock
                SettingsActionRow(
                    icon = Icons.Default.Lock,
                    title = "Lock App Now",
                    subtitle = "Test biometric authentication immediately",
                    onClick = { viewModel.lockApp() },
                    actionColor = EmeraldPrimary,
                    testTag = "test_lock_app_btn"
                )

                SettingsDivider()

                // View E2EE Cryptographic Identity Keys
                SettingsActionRow(
                    icon = Icons.Default.Key,
                    title = "My Cryptographic Keys",
                    subtitle = "View Signal Protocol identity fingerprint",
                    onClick = { showKeyDialog = true },
                    testTag = "view_keys_btn"
                )

                SettingsDivider()

                // Disappearing Messages Default
                SettingsActionRow(
                    icon = Icons.Default.Timer,
                    title = "Default Message Timer",
                    subtitle = "Off (Keep messages indefinitely)",
                    onClick = { /* simulated */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Appearance & Theming
        SettingsSectionHeader(title = "APPEARANCE & THEME")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                // Dark Mode Quick Toggle
                SettingsSwitchRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = if (themeMode == ThemeMode.DARK) "Active (Obsidian & Emerald)" else "Inactive (Fresh Mint)",
                    checked = themeMode == ThemeMode.DARK,
                    onCheckedChange = { isDark ->
                        viewModel.setThemeMode(if (isDark) ThemeMode.DARK else ThemeMode.LIGHT)
                    },
                    testTag = "dark_mode_toggle"
                )

                SettingsDivider()

                // Chat Wallpaper Picker
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Wallpaper, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Chat Wallpaper Theme", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WallpaperChip(
                            title = "Mint",
                            isSelected = currentWallpaper == ChatWallpaper.EMERALD_MINT,
                            color = Color(0xFF10B981),
                            onClick = { viewModel.setChatWallpaper(ChatWallpaper.EMERALD_MINT) }
                        )
                        WallpaperChip(
                            title = "Slate",
                            isSelected = currentWallpaper == ChatWallpaper.MIDNIGHT_SLATE,
                            color = Color(0xFF0F172A),
                            onClick = { viewModel.setChatWallpaper(ChatWallpaper.MIDNIGHT_SLATE) }
                        )
                        WallpaperChip(
                            title = "Forest",
                            isSelected = currentWallpaper == ChatWallpaper.FOREST_MIST,
                            color = Color(0xFF062318),
                            onClick = { viewModel.setChatWallpaper(ChatWallpaper.FOREST_MIST) }
                        )
                        WallpaperChip(
                            title = "Minimal",
                            isSelected = currentWallpaper == ChatWallpaper.MINIMAL,
                            color = Color(0xFF1E293B),
                            onClick = { viewModel.setChatWallpaper(ChatWallpaper.MINIMAL) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Monetization & Ads
        SettingsSectionHeader(title = "MONETIZATION & ADS")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            val personalizedAds by viewModel.personalizedAds.collectAsState()

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                SettingsActionRow(
                    icon = Icons.Default.Info,
                    title = "Monetization Model",
                    subtitle = "100% Free • Funded Exclusively by Ads (No subscriptions)",
                    onClick = { /* info */ },
                    actionColor = EmeraldPrimary
                )

                SettingsDivider()

                SettingsSwitchRow(
                    icon = Icons.Default.Security,
                    title = "Privacy-Conscious Ads",
                    subtitle = "Show relevant security & tech ads with zero data leaks",
                    checked = personalizedAds,
                    onCheckedChange = { viewModel.togglePersonalizedAds(it) },
                    testTag = "personalized_ads_toggle"
                )

                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.Default.Palette,
                    title = "Preview Full-Screen Ad",
                    subtitle = "Test interstitial ad experience (shown after calls)",
                    onClick = { viewModel.triggerInterstitialAd() },
                    testTag = "preview_interstitial_ad_btn"
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: About & Protocol
        SettingsSectionHeader(title = "ABOUT MUSAFFA TEXT")

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MusaffaLogo(size = 48.dp, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Musaffa Text v2.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Powered by Signal Protocol X25519, AES-256-GCM, & Bluetooth Mesh Network. Free forever for all users, supported exclusively through privacy-safe ads.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("About / Status Bio") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_bio_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserProfile(editName, editBio)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Cryptographic Keys Dialog
    if (showKeyDialog) {
        AlertDialog(
            onDismissRequest = { showKeyDialog = false },
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary) },
            title = { Text("Identity Key Fingerprint") },
            text = {
                Column {
                    Text(
                        text = "Your public identity key is shared with contacts to establish end-to-end encrypted sessions:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = userProfile.identityFingerprint,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp),
                            color = EmeraldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Protocol: Double Ratchet with X25519 & AES-256-GCM authenticated cipher",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showKeyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = EmeraldPrimary,
        modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldPrimary),
            modifier = if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    actionColor: Color? = null,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = actionColor ?: EmeraldPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = actionColor ?: MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    )
}

@Composable
private fun WallpaperChip(
    title: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary) else null,
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
