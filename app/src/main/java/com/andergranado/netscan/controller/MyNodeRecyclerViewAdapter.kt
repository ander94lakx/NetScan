package com.andergranado.netscan.controller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andergranado.netscan.R
import com.andergranado.netscan.model.Node
import com.andergranado.netscan.view.fragment.NodeListFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [Node] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyNodeRecyclerViewAdapter(private val mValues: List<Node>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyNodeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_node, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = mValues[position]
        holder.nodeNameView.text = mValues[position].name
        holder.nodeIpView.text = mValues[position].ip
        holder.nodeMacView.text = mValues[position].getMacString()

        holder.mView.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.item as Node)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val nodeNameView: TextView = mView.findViewById<View>(R.id.node_name) as TextView
        val nodeIpView: TextView = mView.findViewById<View>(R.id.node_ip) as TextView
        val nodeMacView: TextView = mView.findViewById<View>(R.id.node_mac) as TextView
        var item: Node? = null
    }
}
