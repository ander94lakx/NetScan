package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * A single node in a network scan.
 */
@Entity
class Scan() {

    @PrimaryKey(autoGenerate = false)
    var id: Int = 0

    var name: String = ""

    var date: Date = Date()

    constructor(pId: Int, pName: String) : this() {
        id = pId
        name = pName
    }

}

