package com.andergranado.netscan.view.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.andergranado.netscan.R

/**
 * An activity for run a Nmap network scan.
 */
class NetworkScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_scan)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setTitle(R.string.scanning)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_warning)
                    .setTitle(R.string.cancel_scan_question)
                    .setMessage(R.string.really_cancel_scan_question)
                    .setPositiveButton(R.string.yes, { _, _ -> finish() })
                    .setNegativeButton(R.string.no, null)
                    .show()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}
