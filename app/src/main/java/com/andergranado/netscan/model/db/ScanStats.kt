package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 *
 */
@Entity(foreignKeys = [ForeignKey(entity = Scan::class, parentColumns = ["id"], childColumns = ["scanId"])])
open class ScanStats(@PrimaryKey
                     var scanId: Int,
                     var scannedHosts: Int,
                     var hostsUp: Int,
                     var hostsDown: Int,
                     var scanTime: Float)