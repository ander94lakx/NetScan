package com.andergranado.netscan.async

abstract class DownloadableResource {

    abstract val urlString: String
    abstract var downloaded: Boolean
        protected set
    abstract protected val downloadThread: Thread

    abstract fun downloadFile()
    abstract fun waitForDownload()
}