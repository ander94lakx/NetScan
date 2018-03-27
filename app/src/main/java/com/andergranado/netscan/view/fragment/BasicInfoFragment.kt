package com.andergranado.netscan.view.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andergranado.netscan.R
import com.andergranado.netscan.view.fragment.BasicInfoFragment.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_basic_info.*

/**
 * A fragment that shows some basic info a single host scans.
 *
 * Activities containing this fragment must implement the [OnFragmentInteractionListener]
 * interface.
 */
class BasicInfoFragment : Fragment() {

    private var hostname: String? = null
    private var ip: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            hostname = arguments.getString(ARG_HOSTNAME)
            ip = arguments.getString(ARG_IP)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_basic_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // View elements can only be accessed (even with Kotlin synthetic properties) after view creation
        ip_text.text = ip
        hostname_text.text = hostname
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
        private const val ARG_HOSTNAME = "hostname"
        private const val ARG_IP = "ip"

        fun newInstance(hostname: String, ip: String): BasicInfoFragment {
            val fragment = BasicInfoFragment()
            val args = Bundle()
            args.putString(ARG_HOSTNAME, hostname)
            args.putString(ARG_IP, ip)
            fragment.arguments = args
            return fragment
        }
    }
}
