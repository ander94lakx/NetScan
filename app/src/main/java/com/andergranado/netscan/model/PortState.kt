package com.andergranado.netscan.model

import java.io.Serializable

enum class StateType { OPEN, FILTERED, UNFILTERED, CLOSED, OPEN_FILTERED, CLOSED_FILTERED, UNKNOWN }

data class PortState(val state: StateType,
                     val reason: String) : Serializable