package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.source.ElectionDataSource
import com.example.android.politicalpreparedness.data.source.Result
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(val remote_datasource : ElectionDataSource,
                         val local_datasource : ElectionDao,
                         app: Application): BaseViewModel(app) {

    //TODO: Create live data val for upcoming elections

    private val _electionInfo = MutableLiveData<List<Election>>()
    val electionInfo : LiveData<List<Election>> = _electionInfo
    val showLoadingSpinner = MutableLiveData<Boolean>(false)

   fun fetchElectionInfoFromRemote() = viewModelScope.launch{
       showLoadingSpinner.value = true
       val info = remote_datasource.getUpcomingElectionInfo()
       //val info = repo.getRepresentativeInfo()

       when(info){
          is Result.Success -> {
            _electionInfo.postValue(info.data.elections)
              showLoadingSpinner.postValue(false)
              Timber.d("election_response ${info.data.toString()}")
          }
          is Result.Error -> {
              showLoadingSpinner.postValue(false)
              Timber.d("election_response ${info.exception.toString()}")
          }
       }

   }

    //TODO: Create live data val for saved elections
    val elections = MutableLiveData<List<Election>>()
    fun getElectionListFromLocalDb() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            try {
                val list = local_datasource.getSavedElections()
                elections.postValue(list)
                Timber.d("local election ${list.toString()}")

            }catch (e:Exception){
                Timber.d("local election ${e.toString()}")
            }
        }
    }

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database


    //TODO: Create functions to navigate to saved or upcoming election voter info

}