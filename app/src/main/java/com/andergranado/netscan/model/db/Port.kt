package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import com.andergranado.netscan.model.Protocol
import com.andergranado.netscan.model.StateType

/**
 * A single port of a concrete node.
 */
@Entity(primaryKeys = ["id", "nodeId"])
open class Port(val id: Int,
                val nodeId: Int,
                val protocol: Protocol,
                val service: String,
                val state: StateType,
                val reason: String)