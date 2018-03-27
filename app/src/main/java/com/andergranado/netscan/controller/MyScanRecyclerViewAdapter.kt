package com.andergranado.netscan.controller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andergranado.netscan.R
import com.andergranado.netscan.model.Scan
import com.andergranado.netscan.view.fragment.ScanListFragment.OnListFragmentInteractionListener
import java.text.SimpleDateFormat

/**
 * [RecyclerView.Adapter] that can display a [Scan] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyScanRecyclerViewAdapter(private val values: List<Scan>,
                                private val listener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyScanRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_scan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = values[position]

        holder.scanNameView.text = values[position].name

        val date = values[position].date
        val formatter = SimpleDateFormat.getDateTimeInstance()
        holder.scanDateView.text = formatter.format(date)

        holder.view.setOnClickListener {
            listener?.onListFragmentInteraction(holder.item as Scan)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val scanNameView: TextView = view.findViewById<View>(R.id.scan_name) as TextView
        val scanDateView: TextView = view.findViewById<View>(R.id.scan_time) as TextView
        var item: Scan? = null

        override fun toString(): String {
            return super.toString() + " '" + scanNameView.text + "'"
        }
    }
}
