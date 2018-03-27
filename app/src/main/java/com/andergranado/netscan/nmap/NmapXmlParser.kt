package com.andergranado.netscan.nmap

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.io.Serializable

/**
 * A XML parser for Nmap XML command output
 */
class NmapXmlParser {

    data class NmapScan(val scanInfo: ScanInfo?,
                        val hosts: List<Host>,
                        val runStats: RunStats?) : Serializable

    data class ScanInfo(val numServices: Int,
                        val protocol: String,
                        val services: List<Int>) : Serializable

    data class Host(val status: HostStatus,
                    val address: Address,
                    val hostNames: List<HostName>,
                    val ports: List<Port>) : Serializable

    data class HostStatus(val state: String,
                          val reason: String) : Serializable

    data class Address(val address: String,
                       val addressType: String) : Serializable

    data class HostName(val name: String,
                        val type: String) : Serializable

    data class Port(val id: Int,
                    val type: String,
                    val service: String,
                    val state: PortState) : Serializable

    data class PortState(val state: String,
                         val reason: String) : Serializable

    data class RunStats(val timeElapsed: Float,
                        val exit: String,
                        val totalHosts: Int,
                        val hostsUp: Int,
                        val hostsDown: Int) : Serializable

    private val namespace: String? = null

    @Throws(XmlPullParserException::class, IOException::class) // For Java interoperability
    fun parse(ins: InputStream): NmapScan {
        ins.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readNmapRun(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readNmapRun(parser: XmlPullParser): NmapScan {
        var info: ScanInfo? = null
        val hosts = mutableListOf<Host>()
        var stats: RunStats? = null

        parser.require(XmlPullParser.START_TAG, namespace, "nmaprun")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            when (parser.name) {
                "scaninfo" -> info = readScanInfo(parser)
                "host" -> hosts.add(readHost(parser))
                "runstats" -> stats = readRunStats(parser)
                else -> skip(parser)
            }
        }
        return NmapScan(info, hosts, stats)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readScanInfo(parser: XmlPullParser): ScanInfo {
        parser.require(XmlPullParser.START_TAG, namespace, "scaninfo")
        val numServices = parser.getAttributeValue(null, "numservices").toInt()
        val protocol = parser.getAttributeValue(null, "protocol")
        val services = parser.getAttributeValue(null, "services")
        parser.next()


        return ScanInfo(numServices, protocol, servicesStringToList(services))
    }

    private fun servicesStringToList(services: String): List<Int> {
        val servicesStrList = services.split(",")
        val servicesList: MutableList<Int> = mutableListOf()
        for (str in servicesStrList) {
            if (str.toIntOrNull() != null) {
                servicesList.add(str.toInt())
            } else {
                val range = str.split("-")
                if (range.size == 2 && range[0].toIntOrNull() != null && range[1].toIntOrNull() != null) {
                    val from = range[0].toInt()
                    val to = range[1].toInt()
                    servicesList.addAll(from..to)
                }
            }
        }
        return servicesList
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readHost(parser: XmlPullParser): Host {
        var status: HostStatus? = null
        var address: Address? = null
        var hostNames = mutableListOf<HostName>()
        var ports = mutableListOf<Port>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            when (parser.name) {
                "status" -> status = readStatus(parser)
                "address" -> address = readAddress(parser)
                "hostnames" -> hostNames = readHostNames(parser)
                "ports" -> ports = readPorts(parser)
                else -> skip(parser)
            }
        }
        if (status != null && address != null)
            return Host(status, address, hostNames, ports)
        else
            throw XmlPullParserException("Can't read status or address")
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readStatus(parser: XmlPullParser): HostStatus {
        parser.require(XmlPullParser.START_TAG, namespace, "status")
        val reason = parser.getAttributeValue(null, "reason")
        val state = parser.getAttributeValue(null, "state")
        parser.next()

        return HostStatus(state, reason)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAddress(parser: XmlPullParser): Address {
        parser.require(XmlPullParser.START_TAG, namespace, "address")
        val addr = parser.getAttributeValue(null, "addr")
        val addrType = parser.getAttributeValue(null, "addrtype")
        parser.next()

        return Address(addr, addrType)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readHostNames(parser: XmlPullParser): MutableList<HostName> {
        val hostNames = mutableListOf<HostName>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            when (parser.name) {
                "hostname" -> hostNames.add(readHostName(parser))
                else -> skip(parser)
            }
        }

        return hostNames
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readHostName(parser: XmlPullParser): HostName {
        parser.require(XmlPullParser.START_TAG, namespace, "hostname")
        val name = parser.getAttributeValue(null, "name")
        val type = parser.getAttributeValue(null, "type")
        parser.next()

        return HostName(name, type)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readPorts(parser: XmlPullParser): MutableList<Port> {
        val ports = mutableListOf<Port>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            when (parser.name) {
                "port" -> ports.add(readPort(parser))
                else -> skip(parser)
            }
        }

        return ports
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readPort(parser: XmlPullParser): Port {
        parser.require(XmlPullParser.START_TAG, namespace, "port")

        val id = parser.getAttributeValue(null, "portid")
        val protocol = parser.getAttributeValue(null, "protocol")

        var service = ""
        var state = PortState("", "")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            when (parser.name) {
                "state" -> state = readState(parser)
                "service" -> service = readService(parser)
                else -> skip(parser)
            }
        }

        parser.require(XmlPullParser.END_TAG, namespace, "port")

        return Port(id.toInt(), protocol.toUpperCase(), service, state)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readState(parser: XmlPullParser): PortState {
        parser.require(XmlPullParser.START_TAG, namespace, "state")
        val state = parser.getAttributeValue(null, "state")
        val reason = parser.getAttributeValue(null, "reason")
        parser.next()

        return PortState(state, reason)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readService(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, namespace, "service")
        val serviceName = parser.getAttributeValue(null, "name")

        while (parser.next() != XmlPullParser.END_TAG && parser.name != "service") {
            skip(parser)
        }

        return serviceName
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readRunStats(parser: XmlPullParser): RunStats {
        var timeElapsed = 0f
        var exit = ""
        var totalHosts = 0
        var hostsUp = 0
        var hostsDown = 0

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue

            when (parser.name) {
                "finished" -> {
                    parser.require(XmlPullParser.START_TAG, namespace, "finished")
                    timeElapsed = parser.getAttributeValue(null, "elapsed").toFloat()
                    exit = parser.getAttributeValue(null, "exit")
                    parser.next()
                }
                "hosts" -> {
                    parser.require(XmlPullParser.START_TAG, namespace, "hosts")
                    totalHosts = parser.getAttributeValue(null, "total").toInt()
                    hostsUp = parser.getAttributeValue(null, "up").toInt()
                    hostsDown = parser.getAttributeValue(null, "down").toInt()
                    parser.next()
                }
                else -> skip(parser)
            }
        }

        return RunStats(timeElapsed, exit, totalHosts, hostsUp, hostsDown)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}