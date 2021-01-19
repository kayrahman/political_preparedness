package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.source.ElectionDataSource
import com.example.android.politicalpreparedness.database.ElectionDao

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class RepresentativeViewModelFactory(val remote : ElectionDataSource,
                                     val app:Application
                                    ): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (RepresentativeViewModel(remote,app) as T)
    }

}