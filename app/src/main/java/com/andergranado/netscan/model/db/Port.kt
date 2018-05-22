package com.andergranado.netscan.model.db

import android.arch.persistence.room.Entity
import com.andergranado.netscan.model.Protocol
import com.andergranado.netscan.model.StateType

/**
 * A single port of a concrete node.
 */
@Entity(primaryKeys = ["id", "nodeId"])
open class Port() {

    var id: Int = 0

    var nodeId: Int = 0

    var protocol: Protocol = Protocol.TCP

    var service: String = ""

    var state: StateType = StateType.UNKNOWN

    var reason: String = ""

    constructor(pId: Int, pNodeId: Int, pProtocol: Protocol, pService: String, pState: StateType, pReason: String ) : this() {
        id = pId
        nodeId = pNodeId
        protocol = pProtocol
        service = pService
        state = pState
        reason = pReason
    }

}