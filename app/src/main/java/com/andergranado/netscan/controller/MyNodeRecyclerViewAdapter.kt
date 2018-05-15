package com.andergranado.netscan.controller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.andergranado.netscan.R
import com.andergranado.netscan.model.db.Node
import com.andergranado.netscan.view.fragment.NodeListFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [Node] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyNodeRecyclerViewAdapter(private val values: MutableList<Node>,
                                private val listener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyNodeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_node, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = values[position]
        holder.nodeNameView.text = values[position].name
        holder.nodeIpView.text = values[position].ip
        holder.nodeMacView.text = values[position].getMacString()

        holder.view.setOnClickListener {
            listener?.onListFragmentInteraction(holder.item as Node)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    fun addItem(node: Node) {
        values.add(node)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nodeNameView: TextView = view.findViewById<View>(R.id.node_name) as TextView
        val nodeIpView: TextView = view.findViewById<View>(R.id.node_ip) as TextView
        val nodeMacView: TextView = view.findViewById<View>(R.id.node_mac) as TextView
        var item: Node? = null
    }
}
