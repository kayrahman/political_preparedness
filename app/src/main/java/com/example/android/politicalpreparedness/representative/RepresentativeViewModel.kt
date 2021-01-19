package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.data.source.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Address
import kotlinx.coroutines.launch
import com.example.android.politicalpreparedness.data.source.Result
import com.example.android.politicalpreparedness.network.models.Official
import com.example.android.politicalpreparedness.representative.model.Representative
import timber.log.Timber

class RepresentativeViewModel(val remote:ElectionDataSource,
                             val app:Application
                              ): BaseViewModel(app) {

    val address_line_1 = MutableLiveData<String>()
    val address_line_2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    val itemPosition = MutableLiveData<Int>()


   private val _address = MutableLiveData<Address>()
    val address:LiveData<Address>
    get() = _address

     val representatives = MutableLiveData<List<Representative>>()
    //TODO: Establish live data for representatives and address

    val showNoResult = MutableLiveData<Boolean>(false)
    val showLoadingSpinner = MutableLiveData<Boolean>(false)


    fun updateAddress(add:Address){
       // _address.postValue(add)
        address_line_1.value = add.line1
        address_line_2.value = add.line2
        city.value = add.city
        state.value = add.state
        zip.value = add.zip
    }

    fun fetchRepresentativesFromRemote()= viewModelScope.launch{
        if(areFieldsValidated()){
            showLoadingSpinner.value = true
            val add = validatedAddress().toFormattedString()
            Timber.d("representative response add${add}")
            val response = remote.getRepresentativeInfo(add)
            when(response){
                is Result.Success -> {
                    showLoadingSpinner.postValue(false)
                    /*   val officials = response.data.officials
                       val offices = response.data.offices
                       Timber.d("representative response official_size${officials.size} : offices${offices.size}")

                       //  representatives.value = offices.first().getRepresentatives(officials)


                       val reps = mutableListOf<Representative>()
                       for (office in offices) {
                           //  representatives.value = office.getRepresentatives(officials)
                           for (representative in office.getRepresentatives(officials)) {
                               reps.add(representative)
                           }
                       }
   */

                    val (offices, officials) = response.data
                    representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }


                    if(representatives.value!!.isNotEmpty()){
                       showNoResult.postValue(false)
                    }else{
                        showNoResult.postValue(true)
                    }

                }
                is Result.Error ->{
                    showLoadingSpinner.postValue(false)
                    showSnackBar.value = response.exception.toString()
                    showNoResult.postValue(true)
                    representatives.postValue(mutableListOf())
                    Timber.d("representative response${response.exception}")
                }
            }

        }else{
            //show a snackbar saying fields are empty
           // showSnackBar.value = app.resources.getString(R.string.field_empty)
        }

        invalidateShowNoData()
    }

    private fun areFieldsValidated() : Boolean {

        Timber.d("spinner position ${itemPosition.value.toString()}")

        if(address_line_1.value.isNullOrEmpty()){
            showSnackBarInt.value = R.string.empty_add_one
            return false
        }
        if(address_line_2.value.isNullOrEmpty()){
            showSnackBarInt.value = R.string.empty_add_two
            return false
        }
        if(city.value.isNullOrEmpty()){
            showSnackBarInt.value = R.string.empty_city
            return false
        }

        val stateArray = app.resources.getStringArray( R.array.states)
        state.value = stateArray[itemPosition.value!!]

        if(state.value.isNullOrEmpty()){
            showSnackBarInt.value = R.string.empty_state
            return false
        }


        if(zip.value.isNullOrEmpty()){
            showSnackBarInt.value = R.string.empty_zip
            return false
        }

        return true

    }

    private fun validatedAddress() : Address{
           return Address(address_line_1.value.toString().trim(),
                    address_line_2.value.toString().trim(),
                    city.value.toString().trim(),
                    state.value.toString().trim(),
                    zip.value.toString().trim())

    }


    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

    private fun invalidateShowNoData() {
        showNoData.value = representatives.value == null || representatives.value!!.isEmpty()
    }


}
