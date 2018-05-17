package com.andergranado.netscan.model

import java.io.Serializable

data class NmapScan(val scanInfo: NmapScanInfo?,
                    val hosts: List<NmapHost>,
                    val runStats: NmapRunStats?) : Serializable