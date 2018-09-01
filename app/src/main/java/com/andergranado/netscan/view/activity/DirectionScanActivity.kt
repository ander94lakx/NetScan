package com.andergranado.netscan.view.activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.andergranado.netscan.R
import com.andergranado.netscan.model.NmapScan
import com.andergranado.netscan.model.NmapPort
import com.andergranado.netscan.model.NmapRunStats
import com.andergranado.netscan.model.Protocol
import com.andergranado.netscan.model.db.Port
import com.andergranado.netscan.view.fragment.BasicInfoFragment
import com.andergranado.netscan.view.fragment.ExtraInfoFragment
import com.andergranado.netscan.view.fragment.ServicesInfoFragment
import kotlinx.android.synthetic.main.activity_direction_scan.*

/**
 * An activity that contains all the info about a completed single host scan.
 */
class DirectionScanActivity : AppCompatActivity(),
        BasicInfoFragment.OnFragmentInteractionListener,
        ServicesInfoFragment.OnListFragmentInteractionListener,
        ExtraInfoFragment.OnFragmentInteractionListener {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter]
     */
    private var sectionsPagerAdapter: SectionsPagerAdapter? = null

    var basicInfoFragment: BasicInfoFragment? = null
    var servicesInfoFragment: ServicesInfoFragment? = null
    var extraInfoFragment: ExtraInfoFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direction_scan)
        setSupportActionBar(toolbar)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark) // Workaround to set the correct color to the status bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val scan = intent.extras.get("scan") as NmapScan

        if (!scan.hosts.isEmpty()) {
            basicInfoFragment = BasicInfoFragment.newInstance(scan.hosts[0].hostNames[0].name, scan.hosts[0].address.address)

            val ports = mutableListOf<Port>()
            for (nmapPort in scan.hosts[0].ports) {
                ports.add(Port(nmapPort.id, 0, nmapPort.type, nmapPort.service, nmapPort.state.state, nmapPort.state.reason))
            }
            servicesInfoFragment = ServicesInfoFragment.newInstance(ports.toList())
            if (scan.runStats is NmapRunStats)
                extraInfoFragment = ExtraInfoFragment.newInstance(scan.runStats)
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity
        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter
        container.adapter = sectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onFragmentInteraction(uri: Uri) {}

    override fun onListFragmentInteraction(item: Port) {}

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page
            return when (position % 3 + 1) {
                2 -> servicesInfoFragment as Fragment
                3 -> extraInfoFragment as Fragment
                else -> basicInfoFragment as Fragment
            }
        }

        override fun getCount(): Int = 3
    }
}
