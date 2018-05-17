package com.andergranado.netscan.controller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andergranado.netscan.R
import com.andergranado.netscan.model.NmapPort
import com.andergranado.netscan.view.fragment.ServicesInfoFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [Port] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyServicesInfoRecyclerViewAdapter(private val values: List<NmapPort>,
                                        private val listener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyServicesInfoRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_services_info, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = values[position]
        holder.portIdView.text = values[position].id.toString()
        holder.portTypeView.text = values[position].type.toString()
        holder.portNameView.text = values[position].service
        holder.portStateView.text = values[position].state.state.toString()

        holder.view.setOnClickListener {
            listener?.onListFragmentInteraction(holder.item as NmapPort)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val portIdView: TextView = view.findViewById<View>(R.id.port_id) as TextView
        val portTypeView: TextView = view.findViewById<View>(R.id.port_type) as TextView
        val portNameView: TextView = view.findViewById<View>(R.id.port_name) as TextView
        val portStateView: TextView = view.findViewById<View>(R.id.port_state) as TextView
        var item: NmapPort? = null

        override fun toString(): String {
            return super.toString() + " '" + portNameView.text + "'"
        }
    }
}
