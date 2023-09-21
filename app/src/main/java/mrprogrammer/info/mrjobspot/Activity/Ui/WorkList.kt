package mrprogrammer.info.mrjobspot.Activity.Ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.Interface.CompleteHandler
import mrprogrammer.info.mrjobspot.Adapter.HomeBottomSheetAdapter
import mrprogrammer.info.mrjobspot.Dialog.FilterBottomSheet
import mrprogrammer.info.mrjobspot.RealmModel.JobRealmModel
import mrprogrammer.info.mrjobspot.ViewModel.JobViewModel
import mrprogrammer.info.mrjobspot.databinding.FragmentWorklistBinding


class WorkList : Fragment() {
    lateinit var root: FragmentWorklistBinding
    lateinit var recycleViewAdapter: HomeBottomSheetAdapter
    lateinit var viewModel : JobViewModel
    var searchListOfJob = mutableListOf<JobRealmModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = FragmentWorklistBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this)[JobViewModel::class.java]

        initRecyclerView()
        initOnClick()
        viewModel.jobListCurrentUser.observe(viewLifecycleOwner){
            searchListOfJob.clear()
            searchListOfJob.addAll(it)
            recycleViewAdapter.notifyDataSetChanged()
            if(searchListOfJob.isEmpty()) {
                root.noData.visibility=View.VISIBLE
            } else {
                root.noData.visibility=View.GONE
            }
        }
        return root.root
    }

    private fun initOnClick() {
        root.more.setOnClickListener {
            val listOfFilter = mutableListOf<String>()
            listOfFilter.add("Delete All")
            val filterBottomSheet = FilterBottomSheet(requireContext(), -1, listOfFilter, object : CompleteHandler {
                override fun onFailure(e: String) {

                }

                override fun onSuccess(Message: Any) {

                }

            }, titleText = "Options", showCheckbox = false)
            filterBottomSheet.show()
        }
    }


    private fun initRecyclerView() {
        val recyclerView = root.jobData
        val layoutManager =  LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recycleViewAdapter = HomeBottomSheetAdapter(requireContext(),searchListOfJob,childFragmentManager, isCurrentUser = true)
        recyclerView.adapter  = recycleViewAdapter
    }
}