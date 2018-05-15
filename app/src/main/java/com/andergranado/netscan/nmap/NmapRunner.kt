package com.andergranado.netscan.nmap

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import com.andergranado.netscan.model.NmapScan
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader
import java.util.Date

/**
 * A class for running Nmap commands on the device.
 */
class NmapRunner(private val scanType: ScanType = ScanType.REGULAR) {

    var scanProcess: Process? = null

    private val parser = NmapXmlParser()
    private val nmapOutputDir = "${NmapInstaller.nmapPath}/nmap/output"
    private var processOutputStream: DataOutputStream? = null
    private var processInputReader: BufferedReader? = null

    fun runScan(hosts: List<String>): NmapScan? {

        if(!NmapInstaller.installed)
            throw Exception("Nmap is not installed in the device") // TODO: Refactor this exception

        startProcess()
        var outputFile = setupOutputFile()

        processOutputStream?.writeBytes(commandBuilder(hosts, outputFile))
        processOutputStream?.writeBytes("exit\n")
        processOutputStream?.flush()

        // Gets all the output from the process
        var pstdout: String? = processInputReader?.readLine()
        val wholeOutput = mutableListOf<String>()
        while (pstdout != null) {
            pstdout += "\n"
            wholeOutput.add(pstdout)
            pstdout = processInputReader?.readLine()
        }

        scanProcess?.waitFor()
        outputFile = File(outputFile.path)

        val scan = parser.parse(outputFile.inputStream())

        outputFile.delete()

        endProcess()

        return scan
    }

    private fun startProcess() {
        if (scanProcess == null || processInputReader == null || processOutputStream == null) {
            // Creates a new process that runs a shell
            val processBuilder = ProcessBuilder("sh")
            processBuilder.redirectErrorStream(true)
            scanProcess = processBuilder.start()

            // Creates a couple of streams to redirect the IO
            processOutputStream = DataOutputStream(scanProcess?.outputStream)
            processInputReader = BufferedReader(InputStreamReader(scanProcess?.inputStream))
        }
    }

    private fun setupOutputFile(): File {
        val outputDir = File(nmapOutputDir)
        val outputFile = File("$nmapOutputDir/scan_direction-" + Date().time.toString() + ".xml")
        if (!outputDir.exists())
            outputDir.mkdirs()
        else if (outputFile.exists())
            outputFile.delete()

        return outputFile
    }

    private fun commandBuilder(hosts: List<String>, outputFile: File): String {
        var hostsString = " "
        hosts.forEach { hostsString += "$it " }
        val args = when (scanType) {
            ScanType.REGULAR -> ""
            ScanType.PING -> "-sn"
            ScanType.QUICK -> "-T4"
            ScanType.FULL -> "-A --no-stylesheet"
        }
        return "${NmapInstaller.nmapBinPath} $args $hostsString -oX ${outputFile.path}\n"
    }

    private fun endProcess() {
        scanProcess?.destroy()
        processOutputStream = null
        processInputReader = null
    }

    companion object {

        fun isNetworkAvailable(activity: Activity): Boolean {
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun intToIp(i: Int): String {
            return (i and 0xFF).toString() + "." +
                    (i shr 8 and 0xFF) + "." +
                    (i shr 16 and 0xFF) + "." +
                    (i shr 24 and 0xFF)
        }
    }
}