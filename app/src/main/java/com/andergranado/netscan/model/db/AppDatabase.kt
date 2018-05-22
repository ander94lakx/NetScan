package com.andergranado.netscan.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

/**
 * A [RoomDatabase] subclass to interact with the database and their DAOs.
 */
@Database(entities = [Scan::class, Node::class, Port::class, ScanStats::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scanDao(): ScanDao

    abstract fun nodeDao(): NodeDao

    abstract fun portDao(): PortDao

    abstract fun scanStatsDao(): ScanStatsDao

    companion object {
        const val DATABASE_NAME: String = "NetScanDB"
    }
}
