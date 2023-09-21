package mrprogrammer.info.mrjobspot.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mrprogrammer.info.mrjobspot.RealmModel.JobRealmModel
import mrprogrammer.info.mrjobspot.Repository.JobRepository

class JobViewModel:ViewModel() {
    val jobList : LiveData<MutableList<JobRealmModel>>
        get() = JobRepository.getJobList()

    val jobListCurrentUser : LiveData<MutableList<JobRealmModel>>
        get() = JobRepository.getJobListCurrentUser()
}