package com.andergranado.netscan.async

import android.net.wifi.WifiManager
import android.os.AsyncTask
import com.andergranado.netscan.model.db.AppDatabase
import com.andergranado.netscan.model.db.Node
import com.andergranado.netscan.model.db.Scan
import com.andergranado.netscan.model.db.ScanStats
import com.andergranado.netscan.nmap.NmapRunner
import org.apache.commons.net.util.SubnetUtils
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

abstract class NetworkScan(protected val db: AppDatabase, private val wifiManager: WifiManager) : AsyncTask<Unit, Node, Unit>() {

    open val pingTimeout = 300 // TODO: Make this a setting?

    protected var addresses: Array<String> = arrayOf()
    protected var currentNode: Node? = null
    protected var triedHosts = 0
    protected var scanId = 0

    private var scanName = ""
    private var emptyScan = true
    private var scanStartTimestamp: Long? = null
    private var hostsUp = 0

    private val ARP_TABLE = "/proc/net/arp"
    private val ARP_INCOMPLETE = "0x0"
    private val ARP_INACTIVE = "00:00:00:00:00:00"

    override fun onPreExecute() {
        val ip = NmapRunner.intToIp(wifiManager.connectionInfo.ipAddress)
        val netmask = NmapRunner.intToIp(wifiManager.dhcpInfo.netmask)

        scanName = wifiManager.connectionInfo.ssid.trim('"')
        addresses = SubnetUtils(ip, netmask).info.allAddresses

        scanStartTimestamp = System.nanoTime()
    }

    override fun onProgressUpdate(vararg values: Node?) {
        if (emptyScan) {
            db.scanDao().insertScan(Scan(scanName))
            scanId = db.scanDao().lastInsertedId()
            emptyScan = false
        }

        for (scan in values)
            if (scan != null)
                hostsUp++
    }

    override fun onPostExecute(result: Unit?) {
        val scanTimeInSeconds = ((System.nanoTime() - scanStartTimestamp!!) / Math.pow(10.0, 9.0)).toFloat()
        db.scanStatsDao().insertScanStats(ScanStats(scanId, addresses.size, hostsUp, addresses.size - hostsUp, scanTimeInSeconds))
    }

    protected fun getMacAddress(ip: String): String? {
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

    /**
     * This method is called in parent class when a concrete node is scanned and must be
     * implemented in child classes to modify the UI to show the progress
     */
    abstract fun updateUi()
}