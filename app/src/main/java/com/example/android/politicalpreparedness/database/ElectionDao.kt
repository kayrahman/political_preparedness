package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election


@Dao
interface ElectionDao {

    //Add insert query
     @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertElection(election: Election)

    @Query("SELECT * FROM election_table")
    suspend fun getSavedElections():List<Election>

     // Add select all election query

     // Add select single election query
     @Query("SELECT * FROM election_table WHERE id = :id")
     suspend fun getElectionById(id:Int) : Election

     // Add delete query
     @Query("DELETE FROM election_table WHERE id = :id")
     suspend fun deleteElectionEntryById(id:Int)

     //Add clear query
     @Query("DELETE FROM election_table")
    suspend fun clearElectionTable()


}