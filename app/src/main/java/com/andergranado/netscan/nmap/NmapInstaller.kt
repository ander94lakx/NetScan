package com.andergranado.netscan.nmap

import android.app.Activity
import android.os.Build
import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * A class to extract and install Nmap binaries in the device.
 */
object NmapInstaller {

    var nmapPath: String? = null
        private set
    var nmapBinPath: String? = null
        private set

    var installed = false
        get() {
            return field || nmapDirExists()
        }

    private const val filePrefix: String = "nmap-android-"
    private const val fileSuffix: String = "-bin.zip"

    fun install(activity: Activity, force: Boolean = false): File {

        val context = activity.applicationContext
        nmapPath = context.filesDir.path
        nmapBinPath = "$nmapPath/nmap/bin/nmap"

        if (!nmapDirExists() || !installed || force) {
            val assetManager = activity.assets
            val ins = assetManager.open(filePrefix + Build.SUPPORTED_ABIS[0] + fileSuffix)
            val zin = ZipInputStream(ins)
            try {
                var entry: ZipEntry = zin.nextEntry
                do {
                    if (entry.isDirectory) {
                        val myDir = File("$nmapPath/${entry.name}")

                        if (!myDir.isDirectory)
                            if (!myDir.mkdirs())
                                throw IOException("Cannot create the directory")

                    } else {
                        val buffer = ByteArray(2048)
                        val outStream = FileOutputStream("$nmapPath/${entry.name}")
                        val bufferOut = BufferedOutputStream(outStream, buffer.size)

                        var size = zin.read(buffer, 0, buffer.size)
                        while (size != -1) {
                            bufferOut.write(buffer, 0, size)
                            size = zin.read(buffer, 0, buffer.size)
                        }

                        bufferOut.flush()
                        bufferOut.close()
                    }
                    entry = zin.nextEntry
                } while (entry != null)
            } catch (e: Exception) {
                Log.e("Nmap unzipping...", e.message)
            }
            zin.close()
            nseDbUpdate()
        }

        val nmapExec = File(nmapBinPath)
        nmapExec.setExecutable(true)

        installed = true

        return nmapExec
    }

    private fun nmapDirExists(): Boolean {
        return if (nmapBinPath == null)
            false
        else {
            if (File(nmapBinPath).exists()) {
                installed = true
                true
            } else
                false
        }
    }

    private fun nseDbUpdate() {
        val processBuilder = ProcessBuilder("sh")
        processBuilder.redirectErrorStream(true)
        val scanProcess: Process = processBuilder.start()

        val outputStream = DataOutputStream(scanProcess.outputStream)
        val inputStream = BufferedReader(InputStreamReader(scanProcess.inputStream))

        val nmapExec = File(nmapBinPath)
        nmapExec.setExecutable(true)

        outputStream.writeBytes("${nmapExec.path} --script-updatedb\n")
        outputStream.writeBytes("exit\n")
        outputStream.flush()
        outputStream.close()

        var pstdout: String? = inputStream.readLine()
        val wholeOutput = mutableListOf<String>()
        while (pstdout != null) {
            pstdout += "\n"
            wholeOutput.add(pstdout)
            pstdout = inputStream.readLine()
        }

        scanProcess.waitFor()
    }
}