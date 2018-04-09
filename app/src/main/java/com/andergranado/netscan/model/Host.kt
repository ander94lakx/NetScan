package com.andergranado.netscan.model

import java.io.Serializable

data class Host(val status: HostStatus,
                val address: Address,
                val hostNames: List<HostName>,
                val ports: List<Port>) : Serializable