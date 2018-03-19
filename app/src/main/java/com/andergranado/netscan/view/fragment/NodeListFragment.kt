package com.andergranado.netscan.view.fragment

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andergranado.netscan.R
import com.andergranado.netscan.controller.MyNodeRecyclerViewAdapter
import com.andergranado.netscan.model.AppDatabase
import com.andergranado.netscan.model.Node
import com.andergranado.netscan.view.fragment.NodeListFragment.OnListFragmentInteractionListener


/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class NodeListFragment : Fragment() {

    private var scanId: Int = 0
    private var scanName: String = ""
    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            scanId = arguments.getInt(ARG_SCAN_ID)
            scanName = arguments.getString(ARG_SCAN_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_node_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            //view.adapter = MyNodeRecyclerViewAdapter(NodeList.ITEMS, mListener)

            val db: AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build()

            val nodes: List<Node> = db.nodeDao().all

            view.adapter = MyNodeRecyclerViewAdapter(nodes, mListener)
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
        fun onListFragmentInteraction(item: Node)
    }

    companion object {

        private val ARG_SCAN_ID = "scan_id"
        private val ARG_SCAN_NAME = "scan_name"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param scanId The ID of the scan whose nodes are going to be displayed
         * @param scanName The name of the scan whose nodes are going to be displayed
         * @return A new instance of fragment NodeListFragment
         */
        fun newInstance(scanId: Int, scanName: String): NodeListFragment {
            val fragment = NodeListFragment()
            val args = Bundle()
            args.putInt(ARG_SCAN_ID, scanId)
            args.putString(ARG_SCAN_NAME, scanName)
            fragment.arguments = args
            return fragment
        }
    }
}
