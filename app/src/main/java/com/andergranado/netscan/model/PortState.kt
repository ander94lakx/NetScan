package com.andergranado.netscan.model

import java.io.Serializable

data class PortState(val state: String,
                     val reason: String) : Serializable