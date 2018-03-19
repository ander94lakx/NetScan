package com.andergranado.netscan.model

import android.arch.persistence.room.*

// TODO: Fix the parameter default name use whe Room Library fixes it
@Dao
interface ScanDao {

    @get:Query("SELECT * FROM scan")
    val all: List<Scan>

    @Query("SELECT * FROM scan WHERE id = :arg0")
    fun getScan(id: Int): Scan

    @Query("SELECT * FROM scan WHERE name = :arg0")
    fun getScan(name: String): Scan

    @Query("SELECT * FROM node WHERE scanId = :arg0")
    fun getNodeList(id: Int): List<Node>

    @Update
    fun updateScan(vararg scans: Scan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScan(vararg scans: Scan)

    @Delete
    fun deleteScan(vararg scans: Scan)
}
