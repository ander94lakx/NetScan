package com.andergranado.netscan.model.db

import android.arch.persistence.room.*

/**
 * DAO interface with all the DB management for a [Node].
 */
@Dao
interface NodeDao {

    @get:Query("SELECT * FROM node")
    val all: List<Node>

    @Query("SELECT max(id) FROM node")
    fun lastInsertedId(): Int

    @Query("SELECT * FROM node WHERE id = :id")
    fun getNode(id: Int): Node

    @Query("SELECT * FROM node WHERE scanId = :scanId AND ip = :ip")
    fun getNodeId(scanId: Int, ip: String): Node

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