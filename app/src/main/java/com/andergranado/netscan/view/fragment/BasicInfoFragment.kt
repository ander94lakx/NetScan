package com.andergranado.netscan.view.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andergranado.netscan.R
import kotlinx.android.synthetic.main.fragment_basic_info.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BasicInfoFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BasicInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BasicInfoFragment : Fragment() {

    private var hostname: String? = null
    private var ip: String? = null

    private var mListener: OnFragmentInteractionListener? = null

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
        host_title.text = hostname?.toUpperCase() ?: "Host" // TODO: Remove the domain and capitalize it
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
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

    companion object {
        private val ARG_HOSTNAME = "hostname"
        private val ARG_IP = "ip"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param hostname The scanned direction Hostname
         * @param ip The scanned direction IP
         * @return A new instance of fragment BasicInfoFragment.
         */
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
