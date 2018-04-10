package com.andergranado.netscan.nmap

import android.util.Xml
import com.andergranado.netscan.model.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

/**
 * A XML parser for Nmap XML command output
 */
class NmapXmlParser {

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
        val protocol = when (parser.getAttributeValue(null, "protocol")) {
            "ip" -> Protocol.IP
            "tcp" -> Protocol.TCP
            "udp" -> Protocol.UDP
            "sctp" -> Protocol.SCTP
            else -> Protocol.TCP // The default scan uses only TCP, and the XML attribute is required in the nmap.dtd
        }
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
        val state = when (parser.getAttributeValue(null, "state")) {
            "up" -> HostStates.UP
            "down" -> HostStates.DOWN
            "skipped" -> HostStates.SKIPPED
            else -> HostStates.UNKNOWN
        }
        parser.next()

        return HostStatus(state, reason)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAddress(parser: XmlPullParser): Address {
        parser.require(XmlPullParser.START_TAG, namespace, "address")
        val addr = parser.getAttributeValue(null, "addr")
        val addrType = when (parser.getAttributeValue(null, "addrtype")) {
            "ipv6" -> AddressType.IPV6
            "mac" -> AddressType.MAC
            else -> AddressType.IPV4
        }
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
        val type = if (parser.getAttributeValue(null, "type") == "user") HostType.USER else HostType.PTR
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
        val protocol = when (parser.getAttributeValue(null, "protocol")) {
            "ip" -> Protocol.IP
            "tcp" -> Protocol.TCP
            "udp" -> Protocol.UDP
            "sctp" -> Protocol.SCTP
            else -> Protocol.TCP // The default scan uses only TCP, and the XML attribute is required in the nmap.dtd
        }
        var service = ""
        var state = PortState(StateType.UNKNOWN, "")

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

        return Port(id.toInt(), protocol, service, state)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readState(parser: XmlPullParser): PortState {
        parser.require(XmlPullParser.START_TAG, namespace, "state")
        val state = when (parser.getAttributeValue(null, "state")) {
            "open" -> StateType.OPEN
            "filtered" -> StateType.FILTERED
            "unfiltered" -> StateType.UNFILTERED
            "closed" -> StateType.CLOSED
            "open|filtered" -> StateType.OPEN_FILTERED
            "closed|filtered" -> StateType.CLOSED_FILTERED
            else ->StateType.UNKNOWN
        }
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
        var exit = Exit.ERROR
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
                    exit = if (parser.getAttributeValue(null, "exit") == "error") Exit.ERROR else Exit.SUCCESS
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