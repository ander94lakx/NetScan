package com.andergranado.netscan.model.db

import android.arch.persistence.room.*

/**
 * DAO interface with all the DB management for a [Port].
 */
@Dao
interface PortDao {

    @Query("SELECT * FROM port WHERE id = :id AND nodeId = :nodeId")
    fun get(id: Int, nodeId: Int): Port

    @Query("SELECT * FROM port WHERE nodeId = :nodeId")
    fun getPortsOfNode(nodeId: Int): List<Port>

    @Update
    fun updatePort(vararg port: Port)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPort(vararg port: Port)

    @Delete
    fun deletePort(vararg port: Port)
}