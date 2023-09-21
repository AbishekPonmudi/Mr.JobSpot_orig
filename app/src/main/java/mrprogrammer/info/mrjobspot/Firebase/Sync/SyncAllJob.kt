package mrprogrammer.info.mrjobspot.Firebase.Sync

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mrprogrammer.Utils.Realm.RealmManager
import mrprogrammer.info.mrjobspot.FirebaseModel.JobModelFirebaseModel
import mrprogrammer.info.mrjobspot.RealmModel.JobRealmModel
import mrprogrammer.info.mrjobspot.Utils.Const

class SyncAllJob {

        fun syncAllJob() {
            val ref = FirebaseDatabase.getInstance().getReference(Const.JOB)
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue(JobModelFirebaseModel::class.java)
                    if (data != null) {
                        val realmData = JobRealmModel()
                        realmData.id = snapshot.key.toString()
                        realmData.image = data.image
                        realmData.name = data.name
                        realmData.about = data.about
                        realmData.title = data.title
                        realmData.imagekey = data.imagekey
                        realmData.address = data.address
                        realmData.locality = data.locality
                        realmData.from = data.from
                        realmData.skill = data.skill
                        realmData.mobile = data.mobile
                        realmData.date = data.date
                        realmData.time = data.time
                        realmData.lat = data.lat
                        realmData.lon = data.lon
                        saveJobLocally(realmData)

                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue(JobModelFirebaseModel::class.java)
                    if (data != null) {
                        val realmData = JobRealmModel()
                        realmData.id = snapshot.key.toString()
                        realmData.image = data.image
                        realmData.name = data.name
                        realmData.about = data.about
                        realmData.title = data.title
                        realmData.imagekey = data.imagekey
                        realmData.address = data.address
                        realmData.locality = data.locality
                        realmData.from = data.from
                        realmData.skill = data.skill
                        realmData.date = data.date
                        realmData.time = data.time
                        realmData.mobile = data.mobile
                        realmData.lat = data.lat
                        realmData.lon = data.lon
                        saveJobLocally(realmData)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    removeJobLocally(snapshot.key.toString())
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {
                print(error)
                }

            })
        }

        private fun saveJobLocally(realmData: JobRealmModel) {
            val realm = RealmManager.getInstance()
            realm.executeTransaction {
                it.copyToRealmOrUpdate(realmData)
            }

        }

        private fun removeJobLocally(key: String) {
            val realm = RealmManager.getInstance()
            realm.executeTransaction {
                it.where(JobRealmModel::class.java).equalTo("id",key).findAll().deleteAllFromRealm()
            }
        }

}