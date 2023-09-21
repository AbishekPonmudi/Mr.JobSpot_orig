package mrprogrammer.info.mrjobspot.Firebase

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions.Companion.firebaseClearString
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import com.mrprogrammer.Utils.Interface.CompleteHandler
import mrprogrammer.info.mrjobspot.Repository.UserRepository
import mrprogrammer.info.mrjobspot.Utils.Const
import mrprogrammer.info.mrjobspot.Utils.UploadImages

class FirebaseUploadJob {

    companion object {
        fun saveJobToFirebase(context: Context,jobTitle: String, aboutJob: String, mobile: String, skill: String, location: String, image: String, locality: String, imageKey: String,
                              completeHandler: CompleteHandler,currentUserEmail:String,lat:String,lon:String) {
            val ref = FirebaseDatabase.getInstance().getReference(Const.JOB).push()

            val userName = UserValue.getUserName(context)

            ref.child("name").setValue(userName)
            ref.child("title").setValue(jobTitle)
            ref.child("about").setValue(aboutJob)
            ref.child("from").setValue(currentUserEmail)
            ref.child("mobile").setValue(mobile)
            ref.child("skill").setValue(skill)
            ref.child("image").setValue(image)
            ref.child("imagekey").setValue(imageKey)
            ref.child("address").setValue(location)
            ref.child("date").setValue(CommonFunctions.getDate())
            ref.child("time").setValue(CommonFunctions.getTime())
            ref.child("lat").setValue(lat)
            ref.child("lon").setValue(lon)
            ref.child("locality").setValue(locality).addOnCompleteListener {
                completeHandler.onSuccess("")
            }.addOnFailureListener {
                completeHandler.onFailure("")
            }

        }

        fun removeJobDetails(id:String, completeHandler: CompleteHandler) {
            FirebaseDatabase.getInstance().getReference(Const.JOB).child(id).removeValue().addOnSuccessListener {
                completeHandler.onSuccess("")
            }.addOnFailureListener {
                completeHandler.onFailure("")
            }
        }


        fun removeJobImage(id: String, completeHandler: CompleteHandler) {
            val currentUserEmail = UserRepository.getUserDetails()?.email
            val key = firebaseClearString(currentUserEmail)
            val ref = FirebaseStorage.getInstance().reference.child(Const.JOB_IMAGE + "/" + key + "/" + id+".jpg")
            ref.delete().addOnSuccessListener {
                completeHandler.onSuccess("")
            }.addOnFailureListener {
                completeHandler.onFailure("")
            }

        }
    }
}