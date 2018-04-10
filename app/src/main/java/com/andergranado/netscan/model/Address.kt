package com.andergranado.netscan.model

import java.io.Serializable

enum class AddressType { IPV4, IPV6, MAC }

data class Address(val address: String,
                   val addressType: AddressType) : Serializable