package mrprogrammer.info.mrjobspot.Firebase

import com.google.firebase.database.FirebaseDatabase
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.Interface.CompleteHandler
import mrprogrammer.info.mrjobspot.Model.LocalWorkModel
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions

class FirebaseUploadUserProfile {
    companion object{
        fun saveUserDataToFirebase(
            email: String,
            aboutText: String,
            skillList: MutableList<String>,
            lanList: MutableList<String>,
            listOfWork: MutableList<LocalWorkModel>,
            completeHandler: CompleteHandler,
            lat: String,
            lon: String,
            address: String,
            locality: String,
            number: String
        ) {
            val skillString  = LocalFunctions.buildConsecutiveString(skillList)
            val lanString  = LocalFunctions.buildConsecutiveString(lanList)
            val db = FirebaseDatabase.getInstance().reference
            val email = CommonFunctions.firebaseClearString(email).toString()
            db.child("Userdata").child(email).child("skill").setValue(skillString)
            db.child("Userdata").child(email).child("about").setValue(aboutText)
            db.child("Userdata").child(email).child("lat").setValue(lat)
            db.child("Userdata").child(email).child("Mobile").setValue(number)
            db.child("Userdata").child(email).child("lon").setValue(lon)
            db.child("Userdata").child(email).child("address").setValue(address)
            db.child("Userdata").child(email).child("locality").setValue(locality)
            db.child("Userdata").child(email).child("work").removeValue()
            val ref = db.child("Userdata").child(email).child("work")
            listOfWork.forEach {
                val ref1 = ref.push()
                ref1.child("office").setValue(it.company)
                ref1.child("job").setValue(it.job)
                ref1.child("start").setValue(it.start)
                ref1.child("end").setValue(it.end)
                ref1.child("dis").setValue(it.description)
            }
            db.child("Userdata").child(email).child("lan").setValue(lanString).addOnCompleteListener {
                completeHandler.onSuccess("")
            }.addOnFailureListener {
                completeHandler.onFailure(it.message.toString())
            }
        }
    }
}