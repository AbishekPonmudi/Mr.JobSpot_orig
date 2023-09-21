package mrprogrammer.info.mrjobspot.Utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import mrprogrammer.info.mrjobspot.Utils.Const

class LocalFirebase {
    companion object{
        fun getDatabaseReferences() : DatabaseReference{
            return FirebaseDatabase.getInstance().getReference(Const.APP_NAME)
        }
    }
}