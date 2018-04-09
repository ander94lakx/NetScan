package com.andergranado.netscan.model.db

import android.arch.persistence.room.*
import com.andergranado.netscan.model.Node

/**
 * DAO interface with all the DB management for a [Node].
 */
@Dao
interface NodeDao {

    @get:Query("SELECT * FROM node")
    val all: List<Node>

    // TODO: Fix the parameter default name use whe Room Library fixes it
    @Query("SELECT * FROM node WHERE id = :arg0")
    fun getNode(id: Int): Node

    @Query("SELECT * FROM node WHERE name = :arg0")
    fun getNode(name: String): Node

    @Query("SELECT * FROM node WHERE scanId = :arg0")
    fun getNodesFromScan(scanId: Int): List<Node>

    @Update
    fun updateNode(vararg node: Node)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNode(vararg node: Node)

    @Delete
    fun deleteNode(vararg node: Node)
}