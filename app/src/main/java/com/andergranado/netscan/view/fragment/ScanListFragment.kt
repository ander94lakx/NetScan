package com.andergranado.netscan.view.fragment

import android.arch.persistence.room.Room
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andergranado.netscan.R
import com.andergranado.netscan.controller.MyScanRecyclerViewAdapter
import com.andergranado.netscan.model.db.Scan
import com.andergranado.netscan.model.db.AppDatabase
import com.andergranado.netscan.view.fragment.ScanListFragment.OnListFragmentInteractionListener

/**
 * A fragment representing a list of [Scan].
 *
 * Activities containing this fragment must implement the [OnListFragmentInteractionListener]
 * interface.
 */
class ScanListFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scan_list, container, false)

        if (view is RecyclerView) {
            view.layoutManager = LinearLayoutManager(view.context)

            val db = Room.databaseBuilder(context as Context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build()
            val scans: List<Scan> = db.scanDao().all

            view.adapter = MyScanRecyclerViewAdapter(scans, listener)
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
        fun onListFragmentInteraction(item: Scan)
    }
}
