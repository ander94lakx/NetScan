package com.andergranado.netscan.model

import java.io.Serializable

data class NmapPort(val id: Int,
                val type: Protocol,
                val service: String,
                val state: PortState) : Serializable

enum class Protocol { IP, TCP, UDP, SCTP }

data class PortState(val state: StateType,
                     val reason: String) : Serializable

enum class StateType { OPEN, FILTERED, UNFILTERED, CLOSED, OPEN_FILTERED, CLOSED_FILTERED, UNKNOWN }