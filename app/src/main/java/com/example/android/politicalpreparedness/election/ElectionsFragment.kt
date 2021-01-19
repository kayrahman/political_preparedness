package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.base.NavigationCommand
import com.example.android.politicalpreparedness.data.source.remote.RemoteElectionDatasource
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import timber.log.Timber

class ElectionsFragment: BaseFragment() {
    private val viewModel by viewModels<ElectionsViewModel>{
        ElectionsViewModelFactory(RemoteElectionDatasource(),ElectionDatabase.getInstance(requireContext()).electionDao ,activity?.application!!)
    }

    private lateinit var binding : FragmentElectionBinding
    override val _viewModel: ElectionsViewModel
        get() = viewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentElectionBinding.inflate(inflater,container,false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        _viewModel.fetchElectionInfoFromRemote()
        _viewModel.getElectionListFromLocalDb()
        populateRecyclerAdapter()

    }

    private fun populateRecyclerAdapter() {
        val electionAdapter = ElectionListAdapter(ElectionListener {
            // Go to election Info
            navigateToElectionInfoScreen(it)

        })

        binding.rvUpcomingElections.adapter = electionAdapter
        //TODO: Refresh adapters when fragment loads
        _viewModel.electionInfo.observe(viewLifecycleOwner, Observer {
            Timber.d("ElectionInfoList ${it.toString()}")
            electionAdapter.submitList(it)
        })

        val localElectionAdapter = ElectionListAdapter(ElectionListener {
            //Go to Election Info viewModel
            navigateToElectionInfoScreen(it)

        })
        binding.rvSavedElections.adapter = localElectionAdapter
        _viewModel.elections.observe(viewLifecycleOwner,Observer{
            localElectionAdapter.submitList(it)
        })
    }

    private fun navigateToElectionInfoScreen(election:Election) {
        _viewModel.navigationCommand.postValue(
                NavigationCommand.To(ElectionsFragmentDirections.toVoterInfoFragment(election.id,election.division))
        )
    }
}