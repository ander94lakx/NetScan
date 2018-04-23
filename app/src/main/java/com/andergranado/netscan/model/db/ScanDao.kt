package com.andergranado.netscan.model.db

import android.arch.persistence.room.*
import com.andergranado.netscan.model.Node
import com.andergranado.netscan.model.Scan

/**
 * DAO interface with all the DB management for a [Node].
 */
@Dao
interface ScanDao {

    @get:Query("SELECT * FROM scan")
    val all: List<Scan>

    // TODO: Fix the parameter default name use whe Room Library fixes it
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
