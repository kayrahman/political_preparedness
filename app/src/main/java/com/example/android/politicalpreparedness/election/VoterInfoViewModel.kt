package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.source.ElectionDataSource
import com.example.android.politicalpreparedness.data.source.remote.RemoteElectionDatasource
import com.example.android.politicalpreparedness.database.ElectionDao
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.data.source.Result
import com.example.android.politicalpreparedness.network.models.Election
import timber.log.Timber

class VoterInfoViewModel(private val localDataSource: ElectionDao,
                        private val remoteDataSource:ElectionDataSource) : ViewModel() {

    //TODO: Add live data to hold voter info
     val _votinglocationsUrl = MutableLiveData<String>()
     val _ballotInformationUrl = MutableLiveData<String>()

    val _election = MutableLiveData<Election>()

    val isFollowed = MutableLiveData<Boolean>()
    //TODO: Add var and methods to populate voter info
     fun getVoterInfo(election_id:Int) = viewModelScope.launch{
        val address = "1600 Amphitheatre Parkway,California, United States."
        val voterInfoResponse = remoteDataSource.getVoterInfo(address,election_id)
        when(voterInfoResponse){
            is Result.Success -> {
                //handle success
                val loc_url = voterInfoResponse.data.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
                val ballot_url = voterInfoResponse.data.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
                _votinglocationsUrl.postValue(loc_url)
                _ballotInformationUrl.postValue(ballot_url)
                _election.postValue(voterInfoResponse.data.election)
                Timber.d("voting locations ${voterInfoResponse.data.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl}")
                Timber.d("voting locations ${voterInfoResponse.data.state?.get(0)?.toString()}")

                //check if exists in local db
                val doesExistInLocalDb =  localDataSource.getElectionById(election_id)
                if(doesExistInLocalDb != null){
                    //updata toggle button state
                    isFollowed.value = true
                    Timber.d("following-yes")
                }else{
                    isFollowed.value = false
                    Timber.d("following-no")
                }

            }

            is Result.Loading ->{
                //handle loading
            }
            is Result.Error ->{
                //handle error
                Timber.d("voting locations error ${voterInfoResponse.exception.toString()}" )
            }

        }

    }



    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database

    fun saveOrRemoveElectionFromLocalDb(isTrue : Boolean) = viewModelScope.launch {
        if(_election.value != null) {
        if(isTrue){

                localDataSource.insertElection(_election.value!!)
                //navigate to Election fragment
                Timber.d("election_inserted${_election.toString()}")

            }else{
            localDataSource.deleteElectionEntryById(_election.value?.id!!)
            Timber.d("election_deleted${_election.toString()}")
        }
        }
    }


    val checkIfElectionExistsInLocalDb = viewModelScope.launch {

    }


    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}