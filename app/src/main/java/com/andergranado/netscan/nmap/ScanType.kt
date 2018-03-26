package com.andergranado.netscan.nmap

enum class ScanType {
    // If the order changes the scan_type string array needs to be changed
    REGULAR, PING, QUICK, FULL
}