package com.andergranado.netscan.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andergranado.netscan.R
import com.andergranado.netscan.controller.MyServicesInfoRecyclerViewAdapter
import com.andergranado.netscan.nmap.NmapXmlParser

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ServicesInfoFragment : Fragment() {

    private var mListener: OnListFragmentInteractionListener? = null
    private var ports: List<NmapXmlParser.Port>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.services_info_list, container, false)

        if (view is RecyclerView) {
            val context = view.context
            view.layoutManager = LinearLayoutManager(context)
            if (ports != null)
                view.adapter = MyServicesInfoRecyclerViewAdapter(ports as List<NmapXmlParser.Port>, mListener)
        }
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
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
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: NmapXmlParser.Port)
    }

    companion object {

        fun newInstance(ports: List<NmapXmlParser.Port>): ServicesInfoFragment {
            val fragment = ServicesInfoFragment()
            fragment.ports = ports
            return fragment
        }
    }
}
