package com.andergranado.netscan.model

import java.io.Serializable

enum class HostType { USER, PTR }

data class HostName(val name: String,
                    val type: HostType) : Serializable