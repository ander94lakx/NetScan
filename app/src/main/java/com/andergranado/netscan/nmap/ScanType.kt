package com.andergranado.netscan.nmap

/**
 * Different types of scans that have different parameters in Nmap.
 */
enum class ScanType {

    // If the order changes the scan_type string array needs to be changed
    REGULAR, ONLY_PING, QUICK, NO_PING, FULL
}