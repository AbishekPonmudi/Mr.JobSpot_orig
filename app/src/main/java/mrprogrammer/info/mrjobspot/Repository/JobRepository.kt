package mrprogrammer.info.mrjobspot.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import com.mrprogrammer.Utils.Realm.RealmManager
import io.realm.RealmChangeListener
import io.realm.RealmResults
import mrprogrammer.info.mrjobspot.RealmModel.JobRealmModel
import mrprogrammer.info.mrjobspot.SingleTon.MrContext

object JobRepository {
   private var jobList = MutableLiveData<MutableList<JobRealmModel>>()
   private var jobListForCurrentUser = MutableLiveData<MutableList<JobRealmModel>>()

    init {
        postJobData()
        postJobDataForCurrentUser()
    }

    fun getJobList():LiveData<MutableList<JobRealmModel>> {
        return jobList
    }

    fun getJobListCurrentUser():LiveData<MutableList<JobRealmModel>> {
        return jobListForCurrentUser
    }

    private fun postJobData() {
        val realm = RealmManager.getInstance()
        val  result = realm.where(JobRealmModel::class.java).findAll()
        jobList.postValue(result)
        val listener = RealmChangeListener<RealmResults<JobRealmModel>> { results ->
            jobList.postValue(results)
        }
        result.addChangeListener(listener)
    }

    private fun postJobDataForCurrentUser() {
        val email = UserValue.getUserEmail(MrContext.context)
        val realm = RealmManager.getInstance()
        val  result = realm.where(JobRealmModel::class.java).equalTo("from", email).findAll()
        jobListForCurrentUser.postValue(result)
        val listener = RealmChangeListener<RealmResults<JobRealmModel>> { results ->
            jobListForCurrentUser.postValue(results)
        }
        result.addChangeListener(listener)
    }

}