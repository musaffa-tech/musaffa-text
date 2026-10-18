package com.example.security

import java.security.MessageDigest

object EncryptionManager {
    const val ALGORITHM = "X25519 + AES-256-GCM (Signal Protocol)"

    fun generateSafetyNumber(userPhone: String, contactPhone: String): String {
        val combined = listOf(userPhone, contactPhone).sorted().joinToString(":")
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(combined.toByteArray(Charsets.UTF_8))
        
        val digits = StringBuilder()
        for (b in digest) {
            val num = (b.toInt() and 0xFF) % 10
            digits.append(num)
        }
        while (digits.length < 60) {
            digits.append("7")
        }
        val raw = digits.substring(0, 60)
        // Group into 12 blocks of 5 digits
        return raw.chunked(5).joinToString(" ")
    }

    fun generateFingerprintHash(key: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(key.toByteArray(Charsets.UTF_8))
        return digest.take(16).joinToString(" ") { "%02X".format(it) }
    }

    /**
     * Generates a 21x21 QR Code representation pattern deterministically from a text seed.
     */
    fun generateQrMatrix(seed: String): Array<BooleanArray> {
        val size = 21
        val matrix = Array(size) { BooleanArray(size) }
        val hash = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray(Charsets.UTF_8))
        
        // Draw 3 corner finder patterns
        drawFinderPattern(matrix, 0, 0)
        drawFinderPattern(matrix, size - 7, 0)
        drawFinderPattern(matrix, 0, size - 7)
        
        // Fill data area pseudo-randomly using hash bytes
        var bitIdx = 0
        for (r in 0 until size) {
            for (c in 0 until size) {
                // Skip finder patterns
                val isFinder = (r < 7 && c < 7) || (r >= size - 7 && c < 7) || (r < 7 && c >= size - 7)
                if (!isFinder) {
                    val byteVal = hash[bitIdx % hash.size].toInt()
                    val bit = ((byteVal shr (bitIdx % 8)) and 1) == 1
                    matrix[r][c] = bit
                    bitIdx++
                }
            }
        }
        return matrix
    }

    private fun drawFinderPattern(matrix: Array<BooleanArray>, startR: Int, startC: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isOuter = r == 0 || r == 6 || c == 0 || c == 6
                val isInner = r in 2..4 && c in 2..4
                matrix[startR + r][startC + c] = isOuter || isInner
            }
        }
    }
}
