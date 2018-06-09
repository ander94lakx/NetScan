package com.andergranado.netscan.async

import android.net.wifi.WifiManager
import com.andergranado.netscan.model.HostStates
import com.andergranado.netscan.model.db.AppDatabase
import com.andergranado.netscan.model.db.Node
import com.andergranado.netscan.model.db.Port
import com.andergranado.netscan.nmap.NmapRunner
import com.andergranado.netscan.nmap.ScanType
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.InetAddress

/**
 * An AsyncTask to make a full scan of the current connected WiFi network, witch saves all
 * the collected data in the app's database. This class makes the scan sequentially, i.e. one by one
 */
abstract class SequentialNetworkScan(db: AppDatabase, wifiManager: WifiManager) : NetworkScan(db, wifiManager) {

    final override val pingTimeout = 300 // TODO: Make this a setting?

    private val ARP_TABLE = "/proc/net/arp"
    private val ARP_INCOMPLETE = "0x0"
    private val ARP_INACTIVE = "00:00:00:00:00:00"

    override fun doInBackground(vararg __nothing: Unit) {
        val nmapRunner = NmapRunner(ScanType.REGULAR)
        for (address in addresses) {
            val inetAddress = InetAddress.getByName(address)
            val reachable = inetAddress.isReachable(pingTimeout)

            if (reachable) {
                val singleHostScan = nmapRunner.runScan(listOf(address))

                if (!isCancelled) nmapRunner.scanProcess?.waitFor()

                if (singleHostScan != null
                        && singleHostScan.hosts.isNotEmpty()
                        && singleHostScan.hosts[0].status.state == HostStates.UP) {

                    val ip = singleHostScan.hosts[0].address.address
                    val name =
                            if (singleHostScan.hosts[0].hostNames.isNotEmpty())
                                singleHostScan.hosts[0].hostNames[0].name
                            else
                                ip
                    val mac: String = getMacAddress(ip) ?: ""
                    val timeElapsed: Float =
                            if (singleHostScan.runStats != null)
                                singleHostScan.runStats.timeElapsed
                            else
                                -1.0f
                    val scanId = db.scanDao().lastInsertedId()

                    currentNode = Node(name, ip, mac, timeElapsed, scanId)
                    db.nodeDao().insertNode(currentNode as Node)


                    for (nmapPort in singleHostScan.hosts[0].ports) {
                        val port = Port(nmapPort.id,
                                db.nodeDao().lastInsertedId(),
                                nmapPort.type,
                                nmapPort.service,
                                nmapPort.state.state,
                                nmapPort.state.reason)
                        db.portDao().insertPort(port)
                    }

                    publishProgress(currentNode)

                } else {
                    currentNode = null
                }
            }
            triedHosts++
            updateUi()
        }
    }

    private fun getMacAddress(ip: String): String? {
        val reader = BufferedReader(InputStreamReader(FileInputStream(ARP_TABLE), "UTF-8"))
        reader.use {
            var line = it.readLine()

            while (line != null) {
                val arpLine = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }

                val arpIp = arpLine[0]
                val flag = arpLine[2]
                val macAddress = arpLine[3]

                if (arpIp == ip)
                    if (flag != ARP_INCOMPLETE && macAddress != ARP_INACTIVE)
                        return macAddress.toUpperCase()

                line = it.readLine()
            }
        }
        return null
    }
}