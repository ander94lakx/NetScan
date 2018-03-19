package com.andergranado.netscan.nmap

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * A class to help with the extraction and installation of Nmap binaries in the device
 */
class NmapInstaller(val activity: Activity, val context: Context) {

    val nmapPath: String = context.filesDir.path
    private val filePrefix: String = "nmap-7.31-android-"
    private val fileSuffix: String = "-bin.zip"
    private val nmapBinPath = nmapPath + "/nmap/bin/nmap"

    init {
        install(false)
    }

    fun install(force: Boolean = false): File {
        if (!nmapDirExists() || force) {
            val assetManager = activity.assets
            val ins = assetManager.open(filePrefix + Build.SUPPORTED_ABIS[0] + fileSuffix)
            val zin = ZipInputStream(ins)
            try {
                var entry: ZipEntry = zin.nextEntry
                do {
                    if (entry.isDirectory) {
                        val myDir = File(nmapPath + "/" + entry.name)

                        if (!myDir.isDirectory)
                            myDir.mkdirs()

                    } else {
                        val buffer = ByteArray(2048)

                        val outStream = FileOutputStream(nmapPath + "/" + entry.name)
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

                } while (entry != null) // TODO: figure out why Kotlin thinks this condition is always true
            } catch (e: Exception) {
                Log.e("Nmap unzipping...", e.message)
            }
            zin.close()

            nseDbUpdate()
        }

        val nmapExec = File(nmapBinPath)
        nmapExec.setExecutable(true)

        return nmapExec
    }

    private fun nmapDirExists(): Boolean {
        return File(nmapBinPath).exists()
    }

    fun nseDbUpdate() {
        val processBuilder = ProcessBuilder("sh")
        processBuilder.redirectErrorStream(true)
        val scanProcess: Process = processBuilder.start()

        val outputStream = DataOutputStream(scanProcess.outputStream)
        val inputStream = BufferedReader(InputStreamReader(scanProcess.inputStream))

        val nmapExec = File(nmapBinPath)
        nmapExec.setExecutable(true)

        outputStream.writeBytes(nmapExec.path + " --script-updatedb\n")
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