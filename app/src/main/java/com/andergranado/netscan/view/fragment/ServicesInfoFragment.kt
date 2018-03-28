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
import com.andergranado.netscan.view.fragment.ServicesInfoFragment.OnListFragmentInteractionListener

/**
 * A fragment representing a list of opened ports for a single host.
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class ServicesInfoFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private var ports: List<NmapXmlParser.Port>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.services_info_list, container, false)

        if (view is RecyclerView) {
            val context = view.context
            view.layoutManager = LinearLayoutManager(context)
            if (ports != null)
                view.adapter = MyServicesInfoRecyclerViewAdapter(ports as List<NmapXmlParser.Port>, listener)
        }
        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

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
