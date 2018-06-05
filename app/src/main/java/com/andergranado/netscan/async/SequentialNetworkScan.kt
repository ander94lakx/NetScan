package com.andergranado.netscan.async

import android.net.wifi.WifiManager
import android.os.AsyncTask
import com.andergranado.netscan.model.HostStates
import com.andergranado.netscan.model.NmapScan
import com.andergranado.netscan.model.db.*
import com.andergranado.netscan.nmap.NmapRunner
import com.andergranado.netscan.nmap.ScanType
import org.apache.commons.net.util.SubnetUtils
import java.net.InetAddress

/**
 * An AsyncTask to make a full scan of the current connected WiFi network, witch saves all
 * the collected data in the app's database. This class makes the scan sequentially, i.e. one by one
 */
abstract class SequentialNetworkScan(private val db: AppDatabase, private val wifiManager: WifiManager) : AsyncTask<Unit, NmapScan, Unit>() {

    protected var addresses: Array<String> = arrayOf()
    protected var currentNode: Node? = null
    protected var triedHosts = 0

    private var emptyScan = true
    private var scanName = ""
    private var scanId = 0
    private val scanStartTimestamp = System.nanoTime()
    private var hostsUp = 0

    private val pingTimeout = 300 // TODO: Make this a setting?

    override fun onPreExecute() {
        val ip = NmapRunner.intToIp(wifiManager.connectionInfo.ipAddress)
        val netmask = NmapRunner.intToIp(wifiManager.dhcpInfo.netmask)

        scanName = wifiManager.connectionInfo.ssid.trim('"')
        addresses = SubnetUtils(ip, netmask).info.allAddresses
    }

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
                        && singleHostScan.hosts[0].status.state == HostStates.UP)
                    publishProgress(singleHostScan)
            }
            triedHosts++
            updateUi()
        }
    }

    override fun onProgressUpdate(vararg values: NmapScan?) {
        if (emptyScan) {
            db.scanDao().insertScan(Scan(scanName))
            scanId = db.scanDao().lastInsertedId()
            emptyScan = false
        }

        for (scan in values) {
            if (scan is NmapScan && scan.hosts.isNotEmpty()) {
                val ip = scan.hosts[0].address.address
                val name = if (scan.hosts[0].hostNames.isNotEmpty()) scan.hosts[0].hostNames[0].name else ip
                val mac = ByteArray(6) // TODO: Implement the MAC direction obtainment method
                val timeElapsed: Float = if (scan.runStats != null) scan.runStats.timeElapsed else -1.0f
                val scanId = db.scanDao().lastInsertedId()

                currentNode = Node(name, ip, mac, timeElapsed, scanId)
                db.nodeDao().insertNode(currentNode as Node)


                for (nmapPort in scan.hosts[0].ports) {
                    val id = nmapPort.id
                    val protocol = nmapPort.type
                    val service = nmapPort.service
                    val state = nmapPort.state.state
                    val reason = nmapPort.state.reason
                    val nodeId = db.nodeDao().lastInsertedId()

                    val port = Port(id, nodeId, protocol, service, state, reason)
                    db.portDao().insertPort(port)
                }
            } else {
                currentNode = null
            }

            hostsUp++
        }
    }

    override fun onPostExecute(result: Unit?) {
        val scanTimeInSeconds = ((System.nanoTime() - scanStartTimestamp) / Math.pow(10.0, 9.0)).toFloat()
        db.scanStatsDao().insertScanStats(ScanStats(scanId, addresses.size, hostsUp, addresses.size - hostsUp, scanTimeInSeconds))
    }

    /**
     * This method is called in parent class when a concrete node is scanned and must be
     * implemented in child classes to modify the UI to show the progress
     */
    abstract fun updateUi()
}