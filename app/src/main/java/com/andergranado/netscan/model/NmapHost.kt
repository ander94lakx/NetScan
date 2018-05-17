package com.andergranado.netscan.model

import java.io.Serializable

data class NmapHost(val status: HostStatus,
                val address: Address,
                val hostNames: List<HostName>,
                val ports: List<NmapPort>) : Serializable

data class HostStatus(val state: HostStates,
                      val reason: String) : Serializable

enum class HostStates { UP, DOWN, UNKNOWN, SKIPPED }

data class Address(val address: String,
                   val addressType: AddressType) : Serializable

enum class AddressType { IPV4, IPV6, MAC }

data class HostName(val name: String,
                    val type: HostType) : Serializable

enum class HostType { USER, PTR }