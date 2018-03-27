package com.andergranado.netscan.view.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andergranado.netscan.R
import com.andergranado.netscan.nmap.NmapXmlParser
import com.andergranado.netscan.view.fragment.ExtraInfoFragment.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_extra_info.*

/**
 * A fragment that shows some extra info a single host scans.
 *
 * Activities containing this fragment must implement the [OnFragmentInteractionListener]
 * interface.
 */
class ExtraInfoFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var stats: NmapXmlParser.RunStats? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_extra_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanned_hosts_num.text = stats?.totalHosts.toString()
        scanned_hosts_num_up.text = stats?.hostsUp.toString()
        scanned_hosts_num_down.text = stats?.hostsDown.toString()
        scan_total_time.text = stats?.timeElapsed.toString()
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
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        fun newInstance(stats: NmapXmlParser.RunStats): ExtraInfoFragment {
            val fragment = ExtraInfoFragment()
            fragment.stats = stats
            return fragment
        }
    }
}
