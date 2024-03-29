package com.andergranado.netscan.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.andergranado.netscan.R
import com.andergranado.netscan.model.NmapScan
import com.andergranado.netscan.nmap.NmapRunner
import com.andergranado.netscan.nmap.ScanType
import com.andergranado.netscan.view.activity.DirectionScanActivity
import com.andergranado.netscan.view.activity.MainActivity
import com.andergranado.netscan.view.fragment.ScanDirectionFragment.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_scan_direction.*
import java.io.Serializable
import java.util.regex.Pattern

/**
 * A fragment for make a single host scan.
 *
 * Activities containing this fragment must implement the [OnFragmentInteractionListener]
 * interface.
 */
class ScanDirectionFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val ipPattern: Pattern = Pattern.compile(
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}\$")
    private val fqdnPattern: Pattern = Pattern.compile(
            "(?=^.{4,253}$)(^((?!-)[a-zA-Z0-9-]{1,63}(?<!-)\\.)+[a-zA-Z]{2,63}$)")
    private var scanType = ScanType.REGULAR
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var scanDirectionTask: ScanDirectionTask

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_direction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter.createFromResource(context, R.array.scan_type, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        scan_type_spinner.adapter = adapter
        scan_type_spinner.onItemSelectedListener = this
        progress_bar.visibility = View.GONE
        button_scan_host.visibility = View.VISIBLE

        host_to_scan.setOnKeyListener { v: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                        startDirectionScan(v)
                        true
                    }
                    else -> false
                }
            } else false
        }

        scanDirectionTask = ScanDirectionTask()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        scanType = ScanType.REGULAR
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, long: Long) {
        scanType = ScanType.values()[pos]
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        val currentActivity = activity
        if (currentActivity is MainActivity)
            currentActivity.showScanFab()
    }

    // It's necessary for Android using view parameter even without using it
    fun startDirectionScan(v: View) {
        val hostStr = host_to_scan.text.toString()
        val activity = activity as Activity
        if (ipPattern.matcher(hostStr).matches() || fqdnPattern.matcher(hostStr).matches()) {
            if (NmapRunner.isNetworkAvailable(activity)) {
                if (scanDirectionTask.status == AsyncTask.Status.PENDING)
                    scanDirectionTask.execute(hostStr)
            }
            else {
                AlertDialog.Builder(activity)
                        .setIcon(R.drawable.ic_warning)
                        .setTitle(R.string.error)
                        .setMessage(R.string.no_internet)
                        .setPositiveButton(R.string.ok, null)
                        .show()
            }
        } else {
            AlertDialog.Builder(activity)
                    .setIcon(R.drawable.ic_warning)
                    .setTitle(R.string.error)
                    .setMessage(R.string.not_valid_ip_domain)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            host_to_scan.text.clear()
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    private inner class ScanDirectionTask : AsyncTask<String, Unit, Unit>() {

        private var scan: NmapScan? = null

        override fun onPreExecute() {
            super.onPreExecute()
            button_scan_host.isEnabled = false
            host_to_scan.isEnabled = false
            scan_type_spinner.isEnabled = false
            progress_bar.visibility = View.VISIBLE
            button_scan_host.visibility = View.GONE
            val currentActivity = activity
            if (currentActivity is MainActivity)
                currentActivity.hideScanFab()
        }

        override fun doInBackground(vararg hosts: String) {
            val nmapRunner = NmapRunner(scanType)
            scan = nmapRunner.runScan(hosts.asList())

            if (!isCancelled) nmapRunner.scanProcess?.waitFor()
        }

        override fun onPostExecute(result: Unit?) {
            val scan = scan // I love Kotlin
            if (scan != null) {
                if (!scan.hosts.isEmpty()) {
                    val intent = Intent(activity, DirectionScanActivity::class.java)
                    intent.putExtra("scan", scan as Serializable)
                    startActivity(intent)
                } else {
                    AlertDialog.Builder(activity as Activity)
                            .setIcon(R.drawable.ic_warning)
                            .setTitle(R.string.error)
                            .setMessage(R.string.no_host_at)
                            .setPositiveButton(R.string.ok, null)
                            .show()
                }
            }
            button_scan_host.isEnabled = true
            host_to_scan.isEnabled = true
            scan_type_spinner.isEnabled = true
            progress_bar.visibility = View.GONE
            val currentActivity = activity
            if (currentActivity is MainActivity)
                currentActivity.showScanFab()
        }
    }
}
