package com.andergranado.netscan.model.db

import android.arch.persistence.room.*

/**
 * DAO interface with all the DB management for a [Scan].
 */
@Dao
interface ScanDao {

    @get:Query("SELECT * FROM scan")
    val all: List<Scan>

    @Query("SELECT max(id) FROM scan")
    fun lastInsertedId(): Int

    @Query("SELECT * FROM scan WHERE id = :id")
    fun getScan(id: Int): Scan

    @Query("SELECT * FROM scan WHERE name = :name")
    fun getScan(name: String): Scan

    @Query("SELECT * FROM node WHERE scanId = :id")
    fun getNodeList(id: Int): List<Node>

    @Update
    fun updateScan(vararg scans: Scan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScan(vararg scans: Scan)

    @Delete
    fun deleteScan(vararg scans: Scan)
}
