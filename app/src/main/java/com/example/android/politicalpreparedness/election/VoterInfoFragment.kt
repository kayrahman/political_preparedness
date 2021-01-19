package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.data.source.remote.RemoteElectionDatasource
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import timber.log.Timber


class VoterInfoFragment : Fragment() {
    private val viewModel by viewModels<VoterInfoViewModel>{
        VoterInfoViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao, RemoteElectionDatasource())
    }
    private lateinit var binding : FragmentVoterInfoBinding
    private val voterInfoSafeArgs : VoterInfoFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentVoterInfoBinding.inflate(inflater, container, false)

        //setHasOptionsMenu(true)
        return binding.root
        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */

        //TODO: Handle loading of URLs
        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    //TODO: Create method to load URL intents

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val election_id = voterInfoSafeArgs.argElectionId
        val division = voterInfoSafeArgs.argDivision
        //Set Toolbar Title
        viewModel.getVoterInfo(election_id)


        Timber.d("Election_id: ${election_id.toString()} division : ${division.toString()} ")

    }

}