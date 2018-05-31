package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * A single node in a network scan.
 */
@Entity
open class Scan(val name: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var date: Date = Date()
}

