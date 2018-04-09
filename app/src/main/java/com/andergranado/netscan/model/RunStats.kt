package com.andergranado.netscan.model

import java.io.Serializable

data class RunStats(val timeElapsed: Float,
                    val exit: String,
                    val totalHosts: Int,
                    val hostsUp: Int,
                    val hostsDown: Int) : Serializable