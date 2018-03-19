package com.andergranado.netscan.model.converters

import android.arch.persistence.room.TypeConverter
import java.net.InetAddress
import java.util.*


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
    fun fromMacString(value: String?): ByteArray? {
        return if (value == null) null
        else {
            val mac = ByteArray(6)
            for ((index, char) in value.withIndex())
                mac[index] = char.toByte()
            mac
        }
    }

    @TypeConverter
    fun macToString(mac: ByteArray?): String? {
        return if (mac == null) null
        else {
            var str = ""
            for (byte in mac)
                str += byte.toChar()
            str
        }
    }

    @TypeConverter
    fun fromIpString(value: String?): InetAddress? {
        return if (value == null) null else InetAddress.getByName(value)
    }

    @TypeConverter
    fun ipToString(ip: InetAddress?): String? {
        return ip?.toString()
    }

}
