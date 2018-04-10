package com.andergranado.netscan.model

import java.io.Serializable

enum class Exit { SUCCESS, ERROR }

data class RunStats(val timeElapsed: Float,
                    val exit: Exit,
                    val totalHosts: Int,
                    val hostsUp: Int,
                    val hostsDown: Int) : Serializable