package com.andergranado.netscan.model

import java.io.Serializable

data class HostStatus(val state: String,
                      val reason: String) : Serializable