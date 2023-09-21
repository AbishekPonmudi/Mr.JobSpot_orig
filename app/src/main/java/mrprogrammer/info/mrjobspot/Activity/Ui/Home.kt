package mrprogrammer.info.mrjobspot.Activity.Ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import com.mrprogrammer.Utils.Interface.CompleteHandler
import com.mrprogrammer.Utils.Realm.RealmManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mrprogrammer.info.mrjobspot.Activity.Profile
import mrprogrammer.info.mrjobspot.Adapter.HomeBottomSheetAdapter
import mrprogrammer.info.mrjobspot.Dialog.FilterBottomSheet
import mrprogrammer.info.mrjobspot.RealmModel.JobRealmModel
import mrprogrammer.info.mrjobspot.Repository.UserRepository
import mrprogrammer.info.mrjobspot.Utils.Const
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.ViewModel.JobViewModel
import mrprogrammer.info.mrjobspot.databinding.FragmentHomeBinding


class Home : Fragment() {
    lateinit var root:FragmentHomeBinding
    lateinit var viewModel : JobViewModel
    lateinit var bottomSheetAdapter: HomeBottomSheetAdapter
    var searchListOfJob = mutableListOf<JobRealmModel>()
    var listOfJob = mutableListOf<JobRealmModel>()
    var currentSelectedPosition = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = FragmentHomeBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this)[JobViewModel::class.java]
        initUserDetails()
        return root.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheet()
        viewModel.jobList.observe(viewLifecycleOwner){
            searchListOfJob.clear()
            searchListOfJob.addAll(it)
            listOfJob.clear()
            listOfJob.addAll(RealmManager.getInstance().copyFromRealm(it))
            bottomSheetAdapter.notifyDataSetChanged()
            checkForDataSize()
            GlobalScope.launch {
                initJobDashBoard()
            }
        }
    }

    private suspend fun initJobDashBoard() {
        val total = listOfJob.size
        withContext(Dispatchers.Main) {
            root.total.text = total.toString()
        }


        val userSkill = LocalFunctions.splitStringToList(UserRepository.getUserDetails()?.skills.toString())
        val skillDashBoard = mutableListOf<JobRealmModel>()

        val userLocation =UserRepository.getUserDetails()?.locality.toString()
        val locationDashBoard = mutableListOf<JobRealmModel>()


        listOfJob.forEach {
            userSkill.forEach {skill ->
                if(skill.contains(it.skill)) {
                    withContext(Dispatchers.Main) {
                        skillDashBoard.add(it)
                        root.skill.text = skillDashBoard.size.toString()
                    }
                }
            }
        }

        listOfJob.forEach {
            if(userLocation.contains(it.locality)) {
                withContext(Dispatchers.Main) {
                    locationDashBoard.add(it)
                    root.location.text = locationDashBoard.size.toString()
                }
            }
        }

    }

    private fun checkForDataSize() {
        if(searchListOfJob.isEmpty()) {
            root.bottomSheetLayout.noData.visibility = View.VISIBLE
        } else {
            root.bottomSheetLayout.noData.visibility = View.GONE
        }
    }

    private fun initBottomSheetData(bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>) {
        val recyclerView = root.bottomSheetLayout.jobData
        val layoutManager =  LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        bottomSheetAdapter = HomeBottomSheetAdapter(requireContext(),searchListOfJob,childFragmentManager)
        recyclerView.adapter  = bottomSheetAdapter
        initBottomSheetEvent(bottomSheetBehavior)
    }

    private fun initBottomSheetEvent(bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>) {
        val isCollapsed = bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
        val search = root.bottomSheetLayout.search
        val filter = root.bottomSheetLayout.filter

        search.setOnTouchListener { view, motionEvent ->
            if(isCollapsed) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            false
        }


        search.setOnClickListener {

        }

        search.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                filterTheJobList(editable.toString())
            }

        })

        filter.setOnClickListener {
            if(isCollapsed) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            val listOfFilter = mutableListOf<String>()
            listOfFilter.add("Date")
            listOfFilter.add("Skill")
            listOfFilter.add("Location")
            val filterBottomSheet = FilterBottomSheet(requireContext(), currentSelectedPosition, listOfFilter, object : CompleteHandler{
                override fun onFailure(e: String) {
                    filterTheJobList("")
                    currentSelectedPosition = -1
                    checkForDataSize()
                }

                override fun onSuccess(Message: Any) {
                    currentSelectedPosition = listOfFilter.indexOf(Message.toString())
                    filterBasedOn(Message.toString())
                }

            }, titleText = "Based On")
            filterBottomSheet.show()
        }
    }

    private fun filterBasedOn(basedOn:String) {
        when (basedOn) {
            "Date" -> {
                val todayDate = CommonFunctions.getDate().toString()
                searchListOfJob.clear()
                listOfJob.forEach {
                    if (todayDate.contains(it.date)) {
                        searchListOfJob.add(it)
                    }

                }
                bottomSheetAdapter.notifyDataSetChanged()
                checkForDataSize()

            }

            "Skill" -> {
                val userSkill = LocalFunctions.splitStringToList(UserRepository.getUserDetails()?.skills.toString())
                searchListOfJob.clear()
                listOfJob.forEach {
                    userSkill.forEach {skill ->
                        if(skill.contains(it.skill)) {
                            searchListOfJob.add(it)
                        }
                    }
                }
                bottomSheetAdapter.notifyDataSetChanged()
                checkForDataSize()
            }

            "Location" -> {
                val userLocation =UserRepository.getUserDetails()?.locality.toString()
                searchListOfJob.clear()
                listOfJob.forEach {
                        if(userLocation.contains(it.locality)) {
                            searchListOfJob.add(it)
                        }
                }
                bottomSheetAdapter.notifyDataSetChanged()
                checkForDataSize()
            }
        }
    }

    private fun filterTheJobList(string: String){
        searchListOfJob.clear()
        listOfJob.forEach {
            if(it.title.contains(string,true) || it.about.contains(string,true) || it.skill.contains(string, true)) {
                searchListOfJob.add(it)
            }
        }
        bottomSheetAdapter.notifyDataSetChanged()
        checkForDataSize()
    }
    private fun initBottomSheet(){
        val bottomSheet = root.bottomSheetLayout.bottomSheet
        val marginLayoutParams = bottomSheet.layoutParams as ViewGroup.MarginLayoutParams
        bottomSheet.layoutParams = marginLayoutParams
        val screenHeight = resources.displayMetrics.heightPixels
       val  bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isHideable = false
        bottomSheet.post {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = screenHeight - LocalFunctions.convertDimensionToPixels(requireContext(), (root.userDetailLayout.height).toFloat())
            bottomSheet.layoutParams = layoutParams
            bottomSheetBehavior.peekHeight =  root.bottomSheetLayout.bottomSheetLayout.getChildAt(2).top

        }
        initBottomSheetData(bottomSheetBehavior)
    }

    private fun initUserDetails() {
        val image = UserValue.getUserImageUrl(requireContext())
        val name = UserValue.getUserName(requireContext())
        Glide.with(requireContext()).load(image).into(root.userProfileImage)
        root.userProfileText.text = "${Const.greeting.random()}, \n${name}."
        root.userProfileImage.setOnClickListener {
            val intent = Intent(requireActivity(), Profile::class.java)
            startActivity(intent)
            LocalFunctions.activityAnimation(requireContext(),true)
        }
    }
}