package com.andergranado.netscan.nmap

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.InputStreamReader


class NmapRunner(val activity: Activity, val context: Context, val scanType: ScanType = ScanType.REGULAR) {

    private val installer = NmapInstaller(activity, context)

    var scanProcess: Process? = null

    private val parser = NmapXmlParser(scanType)

    private val nmapOutputDir = installer.nmapPath + "/nmap/output"
    private val nmapExec: File

    private var processOutputStream: DataOutputStream? = null
    private var processInputReader: BufferedReader? = null

    init {
        nmapExec = installer.install(false)
    }

    fun runScan(hosts: List<String>): NmapXmlParser.NmapScan {
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
        var outputFile = File(nmapOutputDir + "/scan_direction.xml")
        if (!outputDir.exists())
            outputDir.mkdirs()
        else if (outputFile.exists())
            outputFile.delete()

        return outputFile
    }

    private fun commandBuilder(hosts: List<String>, outputFile: File): String {
        // TODO: program it to work with different scan types
        var hostsString = ""
        for (host in hosts)
            hostsString += host + " "
        // TODO: make it work with lambdas or some functional programming in order to make it good for my eyes :)
        return nmapExec.path + " " + hostsString + " -oX " + outputFile.path + "\n"
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
    }
}