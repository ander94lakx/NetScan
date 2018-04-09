package com.andergranado.netscan.model

import java.io.Serializable

data class ScanInfo(val numServices: Int,
                    val protocol: Protocol,
                    val services: List<Int>) : Serializable
