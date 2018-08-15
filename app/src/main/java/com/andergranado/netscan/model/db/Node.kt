package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * A single node in a network scan.
 */
@Entity
open class Node(val name: String,
                val ip: String,
                val mac: String,
                val vendor: String,
                val timeElapsed: Float,
                val scanId: Int) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}