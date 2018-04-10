package com.andergranado.netscan.model

import java.io.Serializable

enum class Protocol { IP, TCP, UDP, SCTP }

data class Port(val id: Int,
                val type: Protocol,
                val service: String,
                val state: PortState) : Serializable