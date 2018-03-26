package com.andergranado.netscan.controller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andergranado.netscan.R
import com.andergranado.netscan.nmap.NmapXmlParser.Port
import com.andergranado.netscan.view.fragment.ServicesInfoFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [Port] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyServicesInfoRecyclerViewAdapter(private val mValues: List<Port>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyServicesInfoRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_services_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = mValues[position]
        holder.portIdView.text = mValues[position].id.toString()
        holder.portTypeView.text = mValues[position].type
        holder.portNameView.text = mValues[position].service
        holder.portStateView.text = mValues[position].state.state

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.item as Port)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val portIdView: TextView = mView.findViewById<View>(R.id.port_id) as TextView
        val portTypeView: TextView = mView.findViewById<View>(R.id.port_type) as TextView
        val portNameView: TextView = mView.findViewById<View>(R.id.port_name) as TextView
        val portStateView: TextView = mView.findViewById<View>(R.id.port_state) as TextView
        var item: Port? = null

        override fun toString(): String {
            return super.toString() + " '" + portNameView.text + "'"
        }
    }
}
