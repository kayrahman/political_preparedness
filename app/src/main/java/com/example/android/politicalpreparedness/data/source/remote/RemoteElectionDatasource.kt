package com.example.android.politicalpreparedness.data.source.remote

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.data.source.ElectionDataSource
import com.example.android.politicalpreparedness.data.source.Result
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsHttpClient.Companion.API_KEY
import com.example.android.politicalpreparedness.network.models.*
import java.util.*

class RemoteElectionDatasource : ElectionDataSource {
    override suspend fun getUpcomingElectionInfo(): Result<ElectionResponse> {
        try {
            val election = CivicsApi.retrofitService.getElections().await()
            return Result.Success(election)
        }catch (e:Exception){
            return Result.Error(e)
        }

    }

    override suspend fun getVoterInfo(add:String,election_id:Int): Result<VoterInfoResponse> {
        try {
            val voterInfo = CivicsApi.retrofitService.getVoterInfo(add,election_id).await()
            return Result.Success(voterInfo)
        }catch (e:Exception){
            return Result.Error(e)
        }
    }

    override suspend fun getRepresentativeInfo(add:String): Result<RepresentativeResponse> {
        try {
            val voterInfo = CivicsApi.retrofitService.getRepresentatives(add).await()
            return Result.Success(voterInfo)
        }catch (e:Exception){
            return Result.Error(e)
        }
    }

}