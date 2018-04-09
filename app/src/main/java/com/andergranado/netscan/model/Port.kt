package com.andergranado.netscan.model

import java.io.Serializable

data class Port(val id: Int,
                val type: String,
                val service: String,
                val state: PortState) : Serializable