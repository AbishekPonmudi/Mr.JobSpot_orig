package mrprogrammer.info.mrjobspot.Repository

import com.mrprogrammer.Utils.Realm.RealmManager
import io.realm.RealmResults
import mrprogrammer.info.mrjobspot.RealmModel.UserProfileInformation

object UserRepository {
    fun getUserDetails(): UserProfileInformation? {
        val realm = RealmManager.getInstance()
       return realm.copyFromRealm(realm.where(UserProfileInformation::class.java).findFirst())
    }

    fun getUserDetailsBasesOnId(id:String): UserProfileInformation? {
        val realm = RealmManager.getInstance()
        return realm.copyFromRealm(realm.where(UserProfileInformation::class.java).equalTo("id",id).findFirst())
    }

    fun saveProfile(data:UserProfileInformation) {
        val realm = RealmManager.getInstance()
        realm.executeTransaction {
            realm.copyToRealmOrUpdate(data)
        }
    }
}