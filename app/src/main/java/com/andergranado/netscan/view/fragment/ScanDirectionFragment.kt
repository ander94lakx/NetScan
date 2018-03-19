package com.andergranado.netscan.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.andergranado.netscan.R
import com.andergranado.netscan.nmap.NmapRunner
import com.andergranado.netscan.nmap.NmapXmlParser
import com.andergranado.netscan.nmap.ScanType
import com.andergranado.netscan.view.activity.DirectionScanActivity
import kotlinx.android.synthetic.main.fragment_scan_direction.*
import java.io.Serializable
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ScanDirectionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ScanDirectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScanDirectionFragment : Fragment() {

    // TODO: Try to locate regex strings on strings.xml
    val IPPattern: Pattern = Pattern.compile(
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}\$")
    val FQDNPattern: Pattern = Pattern.compile(
            "(?=^.{4,253}$)(^((?!-)[a-zA-Z0-9-]{1,63}(?<!-)\\.)+[a-zA-Z]{2,63}$)")


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_scan_direction, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    /**
     * onClick handler of Ping Only switch, who changes other switches to disable more features on
     * the scan and re-enables clickable property when Ping Only isn't checked
     */
    fun onOnlyPingSwitchTouched(v: View) {
        // Kotlin synthetic imports seems that don't work here :|
        val onlyPingSwitch = activity.findViewById<Switch>(R.id.switch_only_ping)
        val scanServicesSwitch = activity.findViewById<Switch>(R.id.switch_scan_services)
        val additionalInfoSwitch = activity.findViewById<Switch>(R.id.switch_get_host_extra_info)

        if (onlyPingSwitch != null && scanServicesSwitch != null && additionalInfoSwitch != null) {
            if (onlyPingSwitch.isChecked) {
                scanServicesSwitch.isClickable = false
                scanServicesSwitch.isChecked = false
                additionalInfoSwitch.isClickable = false
                additionalInfoSwitch.isChecked = false

            } else {
                scanServicesSwitch.isClickable = true
                additionalInfoSwitch.isClickable = true
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun startDirectionScan(v: View) {
        val hostStr = host_to_scan.text.toString()
        if (IPPattern.matcher(hostStr).matches() || FQDNPattern.matcher(hostStr).matches()) {
            if (NmapRunner.isNetworkAvailable(activity))
                ScanDirectionTask().execute(hostStr)
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

    private inner class ScanDirectionTask : AsyncTask<String, Unit, Unit>() {

        private var scan: NmapXmlParser.NmapScan? = null

        override fun doInBackground(vararg hosts: String) {
            val nmapRunner = NmapRunner(activity, context, ScanType.REGULAR)
            scan = nmapRunner.runScan(hosts.asList())

            if (!isCancelled) nmapRunner.scanProcess?.waitFor()
        }

        override fun onPostExecute(result: Unit?) {
            val intent = Intent(activity, DirectionScanActivity::class.java)
            intent.putExtra("scan", scan as Serializable)
            startActivity(intent)
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }
}
