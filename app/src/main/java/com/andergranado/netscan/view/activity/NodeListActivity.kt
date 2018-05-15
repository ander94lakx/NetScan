package com.andergranado.netscan.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.andergranado.netscan.R
import com.andergranado.netscan.model.Node
import com.andergranado.netscan.view.fragment.NodeListFragment

/**
 * An activity that shows a list of [Node] from a concrete scan.
 */
class NodeListActivity : AppCompatActivity(), NodeListFragment.OnListFragmentInteractionListener {

    private var nodeListFragment: NodeListFragment? = null

    private var scanId: Int = 0
    private var scanName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.extras != null) {
            scanId = intent.extras.getInt("scan_id")
            scanName = intent.extras.getString("scan_name")

            getPreferences(Context.MODE_PRIVATE).edit().putInt("scan_id", scanId).apply()
            getPreferences(Context.MODE_PRIVATE).edit().putString("scan_name", scanName).apply()
        } else {
            scanId = getPreferences(Context.MODE_PRIVATE).getInt("scan_id", -1)
            scanName = getPreferences(Context.MODE_PRIVATE).getString("scan_name", "ERROR")
        }

        title = scanName
        nodeListFragment = NodeListFragment.newInstance(scanId, scanName)
        supportFragmentManager.beginTransaction().add(R.id.content_node_list, nodeListFragment).commit()
    }

    override fun onListFragmentInteraction(item: Node) {
        val intent = Intent(this, NodeInfoActivity::class.java)
        val bundle = Bundle()
        bundle.putInt("node_id", item.id)
        bundle.putString("node_name", item.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
