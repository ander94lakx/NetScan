package com.andergranado.netscan.model

import android.arch.persistence.room.*


// TODO: Fix the parameter default name use whe Room Library fixes it
@Dao
interface NodeDao {

    @get:Query("SELECT * FROM node")
    val all: List<Node>

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