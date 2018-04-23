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
    @Query("SELECT * FROM node WHERE id = :id")
    fun getNode(id: Int): Node

    @Query("SELECT * FROM node WHERE name = :name")
    fun getNode(name: String): Node

    @Query("SELECT * FROM node WHERE scanId = :scanId")
    fun getNodesFromScan(scanId: Int): List<Node>

    @Update
    fun updateNode(vararg node: Node)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNode(vararg node: Node)

    @Delete
    fun deleteNode(vararg node: Node)
}