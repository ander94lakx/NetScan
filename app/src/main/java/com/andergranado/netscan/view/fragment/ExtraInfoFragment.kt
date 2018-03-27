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
import kotlinx.android.synthetic.main.fragment_extra_info.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ExtraInfoFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ExtraInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExtraInfoFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
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

        fun newInstance(stats: NmapXmlParser.RunStats): ExtraInfoFragment {
            val fragment = ExtraInfoFragment()
            fragment.stats = stats
            return fragment
        }
    }
}
