package com.andergranado.netscan.view.activity

import android.arch.persistence.room.Room
import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import com.andergranado.netscan.R
import com.andergranado.netscan.model.HostStates
import com.andergranado.netscan.model.NmapScan
import com.andergranado.netscan.model.db.AppDatabase
import com.andergranado.netscan.model.db.Node
import com.andergranado.netscan.model.db.Scan
import com.andergranado.netscan.nmap.NmapRunner
import com.andergranado.netscan.nmap.ScanType
import kotlinx.android.synthetic.main.activity_network_scan.*
import org.apache.commons.net.util.SubnetUtils
import java.net.InetAddress
import java.util.*

/**
 * An activity for run a Nmap network scan.
 */
class NetworkScanActivity : AppCompatActivity() {

    var ended = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_scan)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setTitle(R.string.scanning)

        SequentialNetworkScanTask().execute()
        network_scan_progress_bar.visibility = View.VISIBLE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && !ended) {
            AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning)
                    .setTitle(R.string.cancel_scan_question)
                    .setMessage(R.string.really_cancel_scan_question)
                    .setPositiveButton(R.string.yes, { _, _ -> finish() })
                    .setNegativeButton(R.string.no, null)
                    .show()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    private inner class SequentialNetworkScanTask : AsyncTask<Unit, NmapScan, Unit>() {

        private val activity = this@NetworkScanActivity
        private val context = activity.applicationContext

        private var addresses: Array<String> = arrayOf()
        private val scanId = Date().time.toInt()
        private var nodeId = 0

        private var emptyScan = true
        private var scanName = ""

        private val db: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries().fallbackToDestructiveMigration().build()

        override fun onPreExecute() {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ip = NmapRunner.intToIp(wm.connectionInfo.ipAddress)
            val netmask = NmapRunner.intToIp(wm.dhcpInfo.netmask)

            scanName = wm.connectionInfo.ssid.trim('"')
            addresses = SubnetUtils(ip, netmask).info.allAddresses
        }

        override fun doInBackground(vararg __nothing: Unit) {
            val nmapRunner = NmapRunner(ScanType.REGULAR)
            for (address in addresses) {
                val inetAddress = InetAddress.getByName(address)
                val reachable = inetAddress.isReachable(100)

                if (reachable) {
                    val singleHostScan = nmapRunner.runScan(listOf(address))

                    if (!isCancelled) nmapRunner.scanProcess?.waitFor()

                    if (singleHostScan != null)
                        if (singleHostScan.hosts.isNotEmpty())
                            if (singleHostScan.hosts[0].status.state == HostStates.UP)
                                publishProgress(singleHostScan)
                }
            }

            if (!isCancelled) nmapRunner.scanProcess?.waitFor()
        }

        override fun onProgressUpdate(vararg values: NmapScan?) {
            if (emptyScan) {
                db.scanDao().insertScan(Scan(scanId, scanName))
                emptyScan = false
            }

            for (scan in values) {
                if (scan is NmapScan && scan.hosts.isNotEmpty()) {
                    val ip = scan.hosts[0].address.address
                    val name = if(scan.hosts[0].hostNames.isNotEmpty()) scan.hosts[0].hostNames[0].name else ip
                    val mac = ByteArray(6) // TODO: Implement the MAC direction obtainment method

                    val node = Node(++nodeId, name, ip, mac, scanId)
                    db.nodeDao().insertNode(node)
                }

                // TODO: Update the UI to show every host when It's discovered
            }
        }

        override fun onPostExecute(result: Unit?) {
            network_scan_progress_bar.visibility = View.GONE
            setTitle(R.string.scanned)
            ended = true
        }
    }
}