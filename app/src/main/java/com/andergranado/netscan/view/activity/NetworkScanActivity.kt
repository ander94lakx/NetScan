package com.andergranado.netscan.view.activity

import android.content.Context
import android.content.Intent
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import com.andergranado.netscan.R
import com.andergranado.netscan.async.SequentialNetworkScan
import com.andergranado.netscan.model.db.AppDatabase
import com.andergranado.netscan.model.db.Node
import com.andergranado.netscan.view.fragment.NodeListFragment
import kotlinx.android.synthetic.main.activity_network_scan.*

/**
 * An activity for run a Nmap network scan.
 */
class NetworkScanActivity : AppCompatActivity(),
        NodeListFragment.OnListFragmentInteractionListener {

    var ended = false

    private val nodeListFragment = NodeListFragment.newInstance() // No arguments passed to create an empty nodeListFragment

    private lateinit var db: AppDatabase
    private lateinit var wifiManager: WifiManager
    private lateinit var networkScanTask: NetworkScan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_scan)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setTitle(R.string.starting_scan)

        db = AppDatabase.getInstance(applicationContext)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        networkScanTask = NetworkScan()

        supportFragmentManager.beginTransaction().add(R.id.activity_network_scan, nodeListFragment).commit()
    }

    override fun onResume() {
        super.onResume()

        if (wifiManager.wifiState == WifiManager.WIFI_STATE_ENABLED
                && wifiManager.connectionInfo.supplicantState == SupplicantState.COMPLETED) {
            if (networkScanTask.status == AsyncTask.Status.PENDING)
                networkScanTask.execute()
        } else {
            AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning)
                    .setTitle(R.string.error)
                    .setMessage(R.string.no_wifi)
                    .setPositiveButton(R.string.ok, { _, _ -> finish() })
                    .show()
        }
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

    override fun onListFragmentInteraction(item: Node) {
        // TODO: maybe would be interesting doing something here?
        if (ended) {
            val intent = Intent(this, NodeListActivity::class.java)
            val bundle = Bundle()
            bundle.putInt("scan_id", item.id)
            bundle.putString("scan_name", item.name)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    inner class NetworkScan : SequentialNetworkScan(applicationContext, db, wifiManager) {

        override fun onPreExecute() {
            super.onPreExecute()

            network_scan_progress_bar.max = addresses.size
            network_scan_progress_bar.progress = 0
            network_scan_progress_bar.visibility = View.VISIBLE
            setTitle(R.string.scanning)
        }

        override fun onProgressUpdate(vararg values: Node?) {
            super.onProgressUpdate(*values)

            if (currentNode is Node)
                nodeListFragment.addNode(currentNode as Node)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            network_scan_progress_bar.visibility = View.GONE
            setTitle(R.string.scanned)
            ended = true
        }

        override fun updateUi() {
            network_scan_progress_bar.progress = triedHosts
        }
    }
}