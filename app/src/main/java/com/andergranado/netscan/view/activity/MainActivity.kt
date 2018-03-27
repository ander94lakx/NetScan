package com.andergranado.netscan.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.andergranado.netscan.R
import com.andergranado.netscan.model.Scan
import com.andergranado.netscan.view.fragment.AboutFragment
import com.andergranado.netscan.view.fragment.ScanDirectionFragment
import com.andergranado.netscan.view.fragment.ScanListFragment
import com.andergranado.netscan.view.fragment.ShareFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

/**
 * The MainActivity of the app, that contains a NavigationView and interacts with some other
 * fragments to show different parts of the UI.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        ShareFragment.OnFragmentInteractionListener,
        ScanListFragment.OnListFragmentInteractionListener,
        ScanDirectionFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener {

    private val scanListFragment = ScanListFragment()
    private val scanDirectionFragment = ScanDirectionFragment()
    private val shareFragment = ShareFragment()
    private val aboutFragment = AboutFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivity(Intent(this, NetworkScanActivity::class.java))
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().add(R.id.content_main, scanListFragment).commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        when (item.itemId) {
            R.id.nav_my_scans -> {
                transaction.replace(R.id.content_main, scanListFragment).commit()
            }
            R.id.nav_scan_direction -> {
                transaction.replace(R.id.content_main, scanDirectionFragment).commit()
            }
            R.id.nav_share -> {
                transaction.replace(R.id.content_main, shareFragment).commit()
            }
            R.id.nav_about -> {
                transaction.replace(R.id.content_main, aboutFragment).commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        item.isChecked = true
        return true
    }

    override fun onListFragmentInteraction(item: Scan) {
        val intent = Intent(this, NodeListActivity::class.java)
        val bundle = Bundle()
        bundle.putInt("scan_id", item.id)
        bundle.putString("scan_name", item.name)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onFragmentInteraction(uri: Uri) {}

    fun startDirectionScan() {
        scanDirectionFragment.startDirectionScan()
    }
}
