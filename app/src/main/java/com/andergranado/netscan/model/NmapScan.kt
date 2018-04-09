package com.andergranado.netscan.model

import java.io.Serializable

data class NmapScan(val scanInfo: ScanInfo?,
                    val hosts: List<Host>,
                    val runStats: RunStats?) : Serializable