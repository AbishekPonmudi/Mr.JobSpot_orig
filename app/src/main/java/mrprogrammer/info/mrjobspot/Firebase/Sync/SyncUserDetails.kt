package mrprogrammer.info.mrjobspot.Firebase.Sync

import android.app.Activity
import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import com.mrprogrammer.Utils.Realm.RealmManager
import io.realm.RealmList
import mrprogrammer.info.mrjobspot.FirebaseModel.UserInfo
import mrprogrammer.info.mrjobspot.FirebaseModel.Work
import mrprogrammer.info.mrjobspot.RealmModel.UserProfileInformation
import mrprogrammer.info.mrjobspot.RealmModel.WorkRealm
import mrprogrammer.info.mrjobspot.SingleTon.MrContext

class SyncUserDetails {

        fun syncProfile(context: Context){
            val email =CommonFunctions.firebaseClearString(UserValue.getUserEmail(context)).toString()
            val db = FirebaseDatabase.getInstance().reference
            val result = db.child("Userdata").child(email)

            result.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(UserInfo::class.java)
                    val userProfileInformation = UserProfileInformation()
                    userProfileInformation.id = CommonFunctions.firebaseClearString(email).toString()
                    userProfileInformation.about = data?.about.toString()
                    userProfileInformation.skills = data?.skill.toString()
                    userProfileInformation.languages = data?.lan.toString()
                    userProfileInformation.mobile = data?.Mobile.toString()
                    userProfileInformation.username = data?.Username.toString()
                    userProfileInformation.imageUrl = data?.Imageurl.toString()
                    userProfileInformation.email = data?.Email.toString()
                    userProfileInformation.lat = data?.lat.toString()
                    userProfileInformation.lon = data?.lon.toString()
                    userProfileInformation.address = data?.address.toString()
                    userProfileInformation.locality = data?.locality.toString()
                    val listOfWork = mutableListOf<WorkRealm>()
                    data?.work?.forEach {
                        val localWork = WorkRealm()
                        localWork.id = it.key
                        localWork.dis = it.value.dis.toString()
                        localWork.end = it.value.end.toString()
                        localWork.start = it.value.start.toString()
                        localWork.job = it.value.job.toString()
                        localWork.office = it.value.office.toString()
                        listOfWork.add(localWork)
                    }
                    if (userProfileInformation.work == null) {
                        userProfileInformation.work = RealmList<WorkRealm>()
                    }
                    userProfileInformation.work?.addAll(listOfWork)
                    saveUserProfileLocally(userProfileInformation)
                }

                override fun onCancelled(error: DatabaseError) {
                    print(error)
                }

            })
        }

        fun removeJobFromDatabase(context:Context,id:String) {
            val email =CommonFunctions.firebaseClearString(UserValue.getUserEmail(context)).toString()
            val db = FirebaseDatabase.getInstance().reference
            db.child("Userdata").child(email).child("work").child(id).removeValue().addOnCompleteListener {
                MrContext.MrToast().success(context as Activity,"Removed !")
            }.addOnFailureListener {
                MrContext.MrToast().error(context as Activity,"Error !")
            }
        }

        private fun saveUserProfileLocally(userProfileInformation: UserProfileInformation) {
            val realm = RealmManager.getInstance()
            realm.executeTransaction {
                it.copyToRealmOrUpdate(userProfileInformation)
            }
        }
}