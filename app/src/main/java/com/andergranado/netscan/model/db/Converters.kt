package com.andergranado.netscan.model.db

import android.arch.persistence.room.TypeConverter
import com.andergranado.netscan.model.Protocol
import com.andergranado.netscan.model.StateType
import java.net.InetAddress
import java.util.*

/**
 * An utility class for conversions between data types for storing them in a database.
 */
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromProtocolString(value: String?): Protocol? {
        return if (value == null) null else Protocol.valueOf(value)
    }

    @TypeConverter
    fun protocolToString(protocol: Protocol): String? {
        return protocol.toString()
    }

    @TypeConverter
    fun fromStateString(value: String?): StateType? {
        return if (value == null) null else StateType.valueOf(value)
    }

    @TypeConverter
    fun stateToString(state: StateType): String? {
        return state.toString()
    }

}
