package com.andergranado.netscan.model

import java.io.Serializable

data class NmapRunStats(val timeElapsed: Float,
                    val exit: Exit,
                    val totalHosts: Int,
                    val hostsUp: Int,
                    val hostsDown: Int) : Serializable

enum class Exit { SUCCESS, ERROR }