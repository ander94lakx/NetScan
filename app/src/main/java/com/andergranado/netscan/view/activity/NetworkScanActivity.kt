package com.andergranado.netscan.view.activity

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.andergranado.netscan.R
import com.andergranado.netscan.model.Node
import com.andergranado.netscan.view.fragment.NodeListFragment


class NetworkScanActivity : AppCompatActivity(), NodeListFragment.OnListFragmentInteractionListener {

    val nodeListFragment: NodeListFragment = NodeListFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_scan)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setTitle(R.string.scanning)
    }

    override fun onListFragmentInteraction(item: Node) {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning)
                    .setTitle(R.string.cancel_scan_question)
                    .setMessage(R.string.really_cancel_scan_question)
                    .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { _, _ -> finish() })
                    .setNegativeButton(R.string.no, null)
                    .show()
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }
}
