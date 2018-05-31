package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * A single node in a network scan.
 */
@Entity
open class Node(val name: String,
                val ip: String,
                val mac: ByteArray,
                val timeElapsed: Float,
                val scanId: Int) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun getMacString(): String {
        return mac[0].toInt().toString() +
                ":" + mac[1].toInt().toString() +
                ":" + mac[2].toInt().toString() +
                ":" + mac[3].toInt().toString() +
                ":" + mac[4].toInt().toString() +
                ":" + mac[5].toInt().toString()
    }
}