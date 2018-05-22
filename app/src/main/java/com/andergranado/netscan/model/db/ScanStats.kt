package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 *
 */
@Entity(foreignKeys = [ForeignKey(entity = Scan::class, parentColumns = ["id"], childColumns = ["scanId"])])
open class ScanStats() {

    @PrimaryKey
    var scanId: Int = 0

    var scannedHosts: Int = 0

    var hostsUp: Int = 0

    var hostsDown: Int = 0

    var scanTime: Float = 0.0f

    constructor(pScanId: Int, pScannedHosts: Int, pHostsUp: Int, pHostsDown: Int, pScanTime: Float) : this() {
        scanId = pScanId
        scannedHosts = pScannedHosts
        hostsUp = pHostsUp
        hostsDown = pHostsDown
        scanTime = pScanTime

    }
}