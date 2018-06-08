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
abstract class SequentialNetworkScan(db: AppDatabase, wifiManager: WifiManager) : NetworkScan(db, wifiManager) {

    final override val pingTimeout = 300 // TODO: Make this a setting?

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
}