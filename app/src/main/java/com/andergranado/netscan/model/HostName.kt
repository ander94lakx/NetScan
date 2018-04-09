package com.andergranado.netscan.model

import java.io.Serializable

data class HostName(val name: String,
                    val type: String) : Serializable