package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.source.ElectionDataSource
import com.example.android.politicalpreparedness.database.ElectionDao

class VoterInfoViewModelFactory(val local_datasource:ElectionDao,
                                val remote_datasource:ElectionDataSource
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VoterInfoViewModel(local_datasource,remote_datasource) as T
    }

}