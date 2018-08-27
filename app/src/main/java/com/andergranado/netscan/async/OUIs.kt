package com.andergranado.netscan.async

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * This singleton class is used to obtain the OUI (Organizationally Unique Identifier) associated
 * by the IEEE to a MAC address
 *
 * In order to do that it uses a plain text file obtained from the Wireshark servers that have all
 * the info directly generated from the IANA
 *
 * To download the file uses a simple Thread to avoid doin network operations on the main thread
 */
object OUIs {

    val urlString: String = "https://code.wireshark.org/review/gitweb?p=wireshark.git;a=blob_plain;f=manuf"

    private data class Vendor(val mac: String, val vendorShort: String, val vendorFull: String)
    private val vendorList: MutableList<Vendor> = mutableListOf()

    private val downloadThread = Thread(Runnable {
        val url = URL(urlString)
        val urlConnection = url.openConnection()
        val bufferedReader = BufferedReader(InputStreamReader(urlConnection.getInputStream()))
        val urlDataText = bufferedReader.readLines()
        val commentsRemoved = urlDataText.filter { it.isNotEmpty() && it[0] != '#' }
        for (line in commentsRemoved) {
            val data: List<String> = line.split('\t')
            if (data.size == 3) {
                vendorList.add(Vendor(data[0], data[1], data[2]))
            }
        }
        bufferedReader.close()
    })

    fun downloadOUIFile() {
        downloadThread.start()
        downloadThread.join()
    }

    fun isOuiDataDownloaded(wait: Boolean): Boolean {
        if (wait) {
            downloadThread.join()
            return true
        } else
            return downloadThread.isAlive
    }

    // TODO: Improve this method for all de MACS writed in CIDR mode
    fun checkVendorFromMac(mac: String): String {
        if (mac == "")
            return ""

        val macPrefix = mac.substring(0, 8)
        for (vendor in vendorList)
            if (vendor.mac == macPrefix)
                return vendor.vendorFull

        return ""
    }
}