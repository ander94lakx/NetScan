package com.andergranado.netscan.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andergranado.netscan.R
import com.andergranado.netscan.model.db.AppDatabase
import com.andergranado.netscan.model.db.Node
import com.andergranado.netscan.model.db.Port
import com.andergranado.netscan.view.fragment.ServicesInfoFragment
import kotlinx.android.synthetic.main.content_node_info.*

/**
 * An activity that shows info from a concrete node.
 */
class NodeInfoActivity : AppCompatActivity(), ServicesInfoFragment.OnListFragmentInteractionListener {

    private var nodeName: String = ""
    private var nodeId: Int = 0

    private lateinit var db: AppDatabase

    private lateinit var servicesInfoFragment: ServicesInfoFragment

    private lateinit var node: Node

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nodeName = intent.extras.getString("node_name")
        nodeId = intent.extras.getInt("node_id")
        title = nodeName
        if (intent.extras == null)
            throw NotImplementedError("The Intent that starts NodeInfoActivity is not correctly implemented")

        db = AppDatabase.getInstance(applicationContext)

        val ports = db.portDao().getPortsOfNode(nodeId)
        servicesInfoFragment = ServicesInfoFragment.newInstance(ports)

        node = db.nodeDao().getNode(nodeId)

        node_info_name.text = node.name
        node_info_ip.text = node.ip
        node_info_mac.text = node.mac
        node_info_vendor.text = "unknown" // TODO: change this when vendor name is implemented

        servicesInfoFragment = supportFragmentManager.findFragmentById(R.id.fragment_node_ports) as ServicesInfoFragment
        servicesInfoFragment.addServices(ports)

    }

    override fun onListFragmentInteraction(item: Port) {}
}
