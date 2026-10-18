package com.example.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.example.model.BluetoothPeer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothMeshManager(private val context: Context) {

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _peers = MutableStateFlow<List<BluetoothPeer>>(emptyList())
    val peers: StateFlow<List<BluetoothPeer>> = _peers.asStateFlow()

    private val _meshActive = MutableStateFlow(true)
    val meshActive: StateFlow<Boolean> = _meshActive.asStateFlow()

    private val defaultPeers = listOf(
        BluetoothPeer(
            id = "bt_node_1",
            displayName = "Sarah Chen (Mesh Node)",
            macAddress = "D4:36:39:81:AA:1F",
            rssi = -52,
            distanceEstimatedMeters = 3.2,
            isConnected = true,
            hops = 1,
            batteryLevel = 92
        ),
        BluetoothPeer(
            id = "bt_node_2",
            displayName = "Omar Farooq (Offline Peer)",
            macAddress = "E8:99:A2:14:BC:70",
            rssi = -68,
            distanceEstimatedMeters = 7.8,
            isConnected = true,
            hops = 1,
            batteryLevel = 74
        ),
        BluetoothPeer(
            id = "bt_node_3",
            displayName = "Musaffa Relay Node #4",
            macAddress = "F0:12:33:DE:44:81",
            rssi = -81,
            distanceEstimatedMeters = 15.0,
            isConnected = false,
            hops = 2,
            batteryLevel = 100
        )
    )

    init {
        _peers.value = defaultPeers
    }

    fun startScanning() {
        _isScanning.value = true
    }

    fun stopScanning() {
        _isScanning.value = false
    }

    fun togglePeerConnection(peerId: String) {
        _peers.value = _peers.value.map {
            if (it.id == peerId) it.copy(isConnected = !it.isConnected) else it
        }
    }

    fun toggleMeshService(enabled: Boolean) {
        _meshActive.value = enabled
    }

    fun isBluetoothHardwareAvailable(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter?.isEnabled == true
    }
}
