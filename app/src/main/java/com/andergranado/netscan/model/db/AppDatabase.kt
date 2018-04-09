package com.andergranado.netscan.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.andergranado.netscan.model.Converters
import com.andergranado.netscan.model.Node
import com.andergranado.netscan.model.Scan

/**
 * A [RoomDatabase] subclass to interact with the database and their DAOs.
 */
@Database(entities = arrayOf(Scan::class, Node::class), version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scanDao(): ScanDao

    abstract fun nodeDao(): NodeDao

    companion object {
        const val DATABASE_NAME: String = "NetScanDB"
    }
}
