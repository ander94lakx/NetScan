package com.andergranado.netscan.view.fragment


import android.arch.persistence.room.Room
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andergranado.netscan.R
import com.andergranado.netscan.controller.MyScanRecyclerViewAdapter
import com.andergranado.netscan.model.AppDatabase
import com.andergranado.netscan.model.Scan


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
class ScanListFragment : Fragment() {

    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_scan_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            if (mColumnCount <= 1) {
                view.layoutManager = LinearLayoutManager(context)
            } else {
                view.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            //view.adapter = MyScanRecyclerViewAdapter(ScanList.ITEMS, mListener)

            val db = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            //for (item in ScanList.ITEMS)
            //    db.scanDao().insertScan(item)
            //db.scanDao().insertScan(Scan(92384, "PENE"))
            //var i = 9
            //val names: List<String> = arrayListOf("PC", "Movil", "Raspberry Pi", "Tablet", "Otro")
            /*for (item in ScanList.ITEMS)
            {
                for (x in 0..Random().nextInt(10)) {
                    db.nodeDao().insertNode(Node(++i,
                                                names[Random().nextInt(5)],
                                                InetAddress.getByName("192.168.0." + i % 256),
                                                byteArrayOf(11,22,33,44,55).plus((i%256).toByte())))
                }
            }*/
            val scans: List<Scan> = db.scanDao().all

            view.adapter = MyScanRecyclerViewAdapter(scans, mListener)


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
        fun onListFragmentInteraction(item: Scan)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): ScanListFragment {
            val fragment = ScanListFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
