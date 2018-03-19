package com.andergranado.netscan.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andergranado.netscan.R

class NodeInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val nodeName = intent.extras.getString("node_name")
        if (intent.extras != null)
            title = nodeName
        else
            throw NotImplementedError("Change Node name in toolbar not implemented")
    }
}
