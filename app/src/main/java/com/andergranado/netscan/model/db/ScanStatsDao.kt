package com.andergranado.netscan.model.db

import android.arch.persistence.room.*

/**
 * DAO interface with all the DB management for [ScanStats].
 */
@Dao
interface ScanStatsDao {

    @get:Query("SELECT * FROM scanstats")
    val all: List<ScanStats>

    @Query("SELECT * FROM scanstats WHERE scanId = :scanId")
    fun getScanStats(scanId: Int): ScanStats

    @Update
    fun updateScanStats(vararg node: ScanStats)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScanStats(vararg node: ScanStats)

    @Delete
    fun deleteScanStats(vararg node: ScanStats)
}