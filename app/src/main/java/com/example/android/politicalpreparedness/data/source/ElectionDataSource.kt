package com.example.android.politicalpreparedness.data.source

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.source.Result
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse


interface ElectionDataSource {
    suspend fun getUpcomingElectionInfo() : Result<ElectionResponse>
    suspend fun getVoterInfo(add:String,election_id:Int) : Result<VoterInfoResponse>
    suspend fun getRepresentativeInfo(add:String) : Result<RepresentativeResponse>
}