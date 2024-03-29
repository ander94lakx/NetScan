package com.andergranado.netscan.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andergranado.netscan.R
import com.andergranado.netscan.controller.MyNodeRecyclerViewAdapter
import com.andergranado.netscan.model.db.AppDatabase
import com.andergranado.netscan.model.db.Node
import com.andergranado.netscan.view.fragment.NodeListFragment.OnListFragmentInteractionListener


/**
 * A fragment representing a list of [Node].
 *
 * Activities containing this fragment must implement the [OnListFragmentInteractionListener]
 * interface.
 */
class NodeListFragment : Fragment() {

    private var scanId: Int? = null
    private var scanName: String = ""
    private var listener: OnListFragmentInteractionListener? = null

    private lateinit var nodeRecyclerViewAdapter: MyNodeRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            scanId = (arguments as Bundle).getInt(ARG_SCAN_ID)
            scanName = (arguments as Bundle).getString(ARG_SCAN_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_node_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val db: AppDatabase = AppDatabase.getInstance(view.context)
            val nodes =
                    if (scanId != null)
                        db.nodeDao().getNodesFromScan(scanId!!)
                    else
                        listOf()
            nodeRecyclerViewAdapter = MyNodeRecyclerViewAdapter(nodes.toMutableList(), listener)
            view.adapter = nodeRecyclerViewAdapter
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

    fun addNode(node: Node) {
        nodeRecyclerViewAdapter.addItem(node)
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Node)
    }

    companion object {

        private const val ARG_SCAN_ID = "scan_id"
        private const val ARG_SCAN_NAME = "scan_name"

        fun newInstance(scanId: Int, scanName: String): NodeListFragment {
            val fragment = NodeListFragment()
            val args = Bundle()
            args.putInt(ARG_SCAN_ID, scanId)
            args.putString(ARG_SCAN_NAME, scanName)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): NodeListFragment {
            val fragment = NodeListFragment()
            fragment.arguments = null
            return fragment
        }
    }
}
