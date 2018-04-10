package com.andergranado.netscan.model

import java.io.Serializable

enum class HostStates { UP, DOWN, UNKNOWN, SKIPPED }

data class HostStatus(val state: HostStates,
                      val reason: String) : Serializable