package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * A single node in a network scan.
 */
@Entity
open class Node() {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var scanId = 0

    var name: String = ""

    var ip: String = ""

    var mac: ByteArray = ByteArray(6)

    var timeElapsed: Float = 0.0f

    constructor(pName: String, pIp: String, pMac: ByteArray, pTimeElapsed: Float, pScanId: Int) : this() {
        name = pName
        ip = pIp
        mac = pMac
        timeElapsed = pTimeElapsed
        scanId = pScanId
    }

    fun getMacString(): String {
        return mac[0].toInt().toString() +
                ":" + mac[1].toInt().toString() +
                ":" + mac[2].toInt().toString() +
                ":" + mac[3].toInt().toString() +
                ":" + mac[4].toInt().toString() +
                ":" + mac[5].toInt().toString()
    }
}